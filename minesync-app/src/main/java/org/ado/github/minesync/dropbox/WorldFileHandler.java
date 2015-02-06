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

package org.ado.github.minesync.dropbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;
import com.dropbox.sync.android.DbxException;
import org.ado.github.minesync.ActivityTracker;
import org.ado.github.minesync.R;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.config.AppConfiguration;
import org.ado.github.minesync.exception.DbxQueueException;
import org.ado.github.minesync.exception.MineSyncException;
import org.ado.github.minesync.gui.notification.WorldUpdateNotification;
import org.ado.github.minesync.minecraft.MinecraftWorldManager;

import java.io.IOException;
import java.io.InputStream;

import static org.ado.github.minesync.config.AppConstants.MINECRAFT_APP_PACKAGE;
import static org.ado.github.minesync.minecraft.MinecraftUtils.getWorldName;

/**
 * <code>DropboxFileHandler</code> implementation to handle modifications in world files in dropbox.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldFileHandler extends AbstractDropboxFileHandler {

    private static final String ZIP_FILE_EXTENSION = ".zip";
    private final String TAG = WorldFileHandler.class.getName();
    MinecraftWorldManager minecraftWorldManager;
    private Context context;
    private ActivityTracker activityTracker;
    private WorldUpdateNotification worldUpdateNotification;

    public WorldFileHandler(Context context) {
        this.context = context;
        activityTracker = new ActivityTracker(context);
        worldUpdateNotification = new WorldUpdateNotification(context);
        minecraftWorldManager = new MinecraftWorldManager(
                AppConfiguration.getDropboxAccountManager(context),
                context);
    }

    @Override
    public boolean canHandle(FileInfo fileInfo) {
        return !fileInfo.isFolder()
                && fileInfo.getPath().endsWith(ZIP_FILE_EXTENSION);
    }

    @Override
    public void handle(String filename, InputStream inputStream, Context context) {
        try {
            processSyncedWorld(filename, inputStream);

        } catch (DbxException e) {
            ALog.e(TAG, e, "Cannot access remote file \"" + filename + "\".");
        } catch (IOException e) {
            ALog.e(TAG, e, "Cannot read stream for file \"" + filename + "\".");
        } catch (DbxQueueException e) {
            ALog.e(TAG, e, "Cannot process file \"" + filename + "\".");
        } catch (MineSyncException e) {
            notifyConflictedCopy(filename, e);
        }
    }

    private void notifyConflictedCopy(String filename, MineSyncException e) {
        ALog.e(TAG, e, "Cannot update local world for conflicted zip \""
                + filename + "\".");

        String conflictWorldText =
                String.format(context.getResources().getText(R.string.toast_conflict_world).toString(),
                        filename);
        Toast.makeText(context, conflictWorldText, Toast.LENGTH_LONG).show();
    }

    private void processSyncedWorld(String filename, InputStream inputStream) throws MineSyncException, IOException, DbxQueueException {
        killMinecraftProcess();
        ALog.d(TAG, "Update local World [" + filename + "].");
        minecraftWorldManager.update(filename, inputStream);
        notifyWoldSyncFinished(filename);
    }

    private void killMinecraftProcess() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(MINECRAFT_APP_PACKAGE);
    }

    private void notifyWoldSyncFinished(String worldFilename) {
        ALog.i(TAG, "World updated [" + getWorldName(worldFilename) + "].");
        worldUpdateNotification.notifyWorldUpdate(context, getWorldName(worldFilename));
    }
}