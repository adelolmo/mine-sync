/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Andoni del Olmo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.ado.github.minesync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.dropbox.sync.android.DbxAccountManager;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.config.AppConfiguration;
import org.ado.github.minesync.db.MineSyncWorldStatus;
import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.db.WorldEntity;
import org.ado.github.minesync.exception.MineSyncException;
import org.ado.github.minesync.minecraft.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ado.github.minesync.config.AppConstants.*;

public class MinecraftPeStateReceiver extends BroadcastReceiver {

    private static final String TAG = MinecraftPeStateReceiver.class.getName();
    private static final int IDLE = 0;
    private static final int SYNCING = 1;
    private static final int SLEEP = 2;
    private static final int UNLINKED = 3;
    private static final String[] STATE_DESC = new String[]{"IDLE", "SYNCING", "SLEEP", "UNLINKED"};
    private static final String[] SKIPPING_TRANSITION_APPS = new String[]{"org.ado.minesync", "com.android.systemui"};
    private static int state = SLEEP;
    private MinecraftData minecraftData;
    private List<MinecraftWorld> syncWorlds;
    private MineSyncWorldStatus mineSyncWorldStatus;
    private MinecraftWorldManager minecraftWorldManager;

    public MinecraftPeStateReceiver() {
        super();
        minecraftData = new MinecraftData();
        syncWorlds = new ArrayList<MinecraftWorld>();
        ALog.i(TAG, "receiver created");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mineSyncWorldStatus = new MineSyncWorldStatus(context.getApplicationContext());
        minecraftWorldManager = new MinecraftWorldManager(AppConfiguration.getDropboxAccountManager(context), context);
        ALog.v(TAG, "intent action [%s].", intent.getAction());
        ALog.d(TAG, "onReceive. Current state is [%s]", STATE_DESC[state]);
        if (INTENT_DROPBOX_ACCOUNT.equalsIgnoreCase(intent.getAction())) {
            processDropboxEvent(intent.getStringExtra(INTENT_PARAMETER_ACCOUNT_STATUS));
        }
        if (INTENT_FOREGROUND_APP.equalsIgnoreCase(intent.getAction())) {
            String foregroundApp = intent.getStringExtra(INTENT_PARAMETER_FOREGROUND_APP);
            ALog.v(TAG, "foregroundApp [%s].", foregroundApp);
            if (isValidApplicationTransition(foregroundApp)) {
                processApplicationEvent(context, foregroundApp);
            } else {
                ALog.v(TAG, "skipping not valid application transition [%s]", foregroundApp);
            }
        }
        ALog.d(TAG, "New state is [%s]", STATE_DESC[state]);
    }

    private void processDropboxEvent(String accountStatus) {
        ALog.i(TAG, "Dropbox account status changes to [%s].", accountStatus);
        if (INTENT_PARAMETER_VALUE_LINKED.equalsIgnoreCase(accountStatus)) {
            state = SLEEP;
        } else if (INTENT_PARAMETER_VALUE_UNLINKED.equalsIgnoreCase(accountStatus)) {
            state = UNLINKED;
        }
    }

    private void processApplicationEvent(Context context, String foregroundApp) {
        if (state == IDLE) {
            if (!isMinecraftForegroundApp(foregroundApp)) {
                ALog.d(TAG, "Minecraft is not active.");
                uploadMinecraftWorlds(context);
            }
        } else if (state == SYNCING) {
            ALog.d(TAG, "Syncing already.");

        } else if (state == SLEEP) {
            if (isMinecraftForegroundApp(foregroundApp)) {
                ALog.d(TAG, "Minecraft is active");
                state = IDLE;
            }
        } else if (state == UNLINKED) {
            // ignore
        } else {
            ALog.w(TAG, "Wrong state [%s]", STATE_DESC[state]);
        }
    }

    private void uploadMinecraftWorlds(Context context) {
        try {
            DbxAccountManager dbxAccountManager = AppConfiguration.getDropboxAccountManager(context);
            if (dbxAccountManager.hasLinkedAccount()) {
                ALog.d(TAG, "Dropbox account linked.");
                for (MinecraftWorld world : minecraftData.getWorlds()) {
                    handleWorld(context, world);
                }
                if (syncWorlds.isEmpty()) {
                    state = SLEEP;
                }
            } else {
                ALog.d(TAG, "No Dropbox account linked. Won't sync.");
                state = SLEEP;
            }
        } catch (Exception e) {
            ALog.e(TAG, e, "Cannot access local file.");
            state = SLEEP;
        }
    }

    private void handleWorld(Context context, MinecraftWorld world) {
        ALog.d(TAG, "processing world [%s]...", world);
        WorldEntity worldEntity = getWorldInDatabase(world);
        try {
            if (worldEntity != null) {
                if (SyncTypeEnum.AUTO == worldEntity.getSyncType()
                        && MinecraftUtils.isWorldChanged(world, worldEntity)) {

                    ALog.i(TAG, "World \"%s\" has changed and will be uploaded.", world.getName());
                    uploadWorld(context, world);
                }
            } else {
                ALog.i(TAG, "World \"%s\" is new and will be uploaded.", world.getName());
                uploadWorld(context, world);
            }
        } catch (Exception e) {
            state = SLEEP;
            notifySyncError(context, world.getName(), e);
        }
    }

    private void uploadWorld(final Context context, final MinecraftWorld minecraftWorld)
            throws IOException, MineSyncException {

        ALog.d(TAG, "upload world [%s] to Dropbox.", minecraftWorld.getName());
        state = SYNCING;
        syncWorlds.add(minecraftWorld);
        try {
            minecraftWorldManager.uploadWorld(minecraftWorld, new AbstractMinecraftWorldListener() {
                @Override
                public void operationFinished(MinecraftWorldActionEnum minecraftWorldAction, File file) {
                    if (MinecraftWorldActionEnum.NETWORK.equals(minecraftWorldAction)
                            && minecraftWorld.getName().equals(MinecraftUtils.getWorldName(file))) {

                        syncWorlds.remove(minecraftWorld);
                        ALog.i(TAG, "Uploading of localFile successful [%s]", file);

                        if (syncWorlds.isEmpty()) {
                            ALog.i(TAG, "Uploading of worlds finished successfully.");
                            state = SLEEP;
                            ALog.d(TAG, "New state is [%s]", STATE_DESC[state]);
                            notifySyncSuccessful(context, MinecraftUtils.getWorldName(file));
                        }
                    }
                }
            }, true);
        } catch (MineSyncException e) {
            notifySyncError(context, minecraftWorld.getName(), e);
        }
    }

    private WorldEntity getWorldInDatabase(MinecraftWorld world) {
        return mineSyncWorldStatus.getWorld(world.getName());
    }

    private void notifySyncSuccessful(Context context, String worldName) {
        ALog.d(TAG, "call to start world's sync.");
        writeToastWorldSaveSuccess(context);
    }

    private void notifySyncError(Context context, String worldName, Exception e) {
        ALog.e(TAG, e, "Unable to upload world [" + worldName + "]. Skipping.");
        writeToastWorldSaveError(context);
    }

    private void writeToastWorldSaveSuccess(Context context) {
        Toast toast = Toast.makeText(context, context.getResources().getText(R.string.toast_world_upload_ok), Toast.LENGTH_LONG);
        toast.show();
    }

    private void writeToastWorldSaveError(Context context) {
        Toast toast = Toast.makeText(context, context.getResources().getText(R.string.toast_world_upload_error), Toast.LENGTH_LONG);
        toast.show();
    }

    private CharSequence getResourceText(Context context, int resourceId, String string) {
        return String.format(getResourceText(context, resourceId).toString(), string);
    }

    private CharSequence getResourceText(Context context, int resourceId) {
        return context.getResources().getText(resourceId);
    }

    private boolean isMinecraftForegroundApp(String foregroundApp) {
        return MINECRAFT_APP_PACKAGE.equalsIgnoreCase(foregroundApp);
    }

    private boolean isValidApplicationTransition(String foregroundApp) {
        for (String app : SKIPPING_TRANSITION_APPS) {
            if (app.equalsIgnoreCase(foregroundApp)) {
                return false;
            }
        }
        return true;
    }
}