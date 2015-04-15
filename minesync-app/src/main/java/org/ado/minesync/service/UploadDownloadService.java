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

package org.ado.minesync.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.dropbox.sync.android.DbxException;
import org.ado.minesync.ActivityTracker;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.config.AppConfiguration;
import org.ado.minesync.db.SyncTypeEnum;
import org.ado.minesync.exception.DropboxAccountException;
import org.ado.minesync.exception.MineSyncException;
import org.ado.minesync.gui.ExceptionNotifier;
import org.ado.minesync.gui.notification.ConfigurationNotification;
import org.ado.minesync.gui.notification.MineSyncNotification;
import org.ado.minesync.minecraft.*;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Service to upload/download all worlds.
 *
 * @author andoni
 * @since 1.0.0
 */
public class UploadDownloadService extends IntentService {

    public static final String BROADCAST_ACTION = "org.ado.minesync";
    public static final String BROADCAST_PROGRESS = "progress";
    public static final String OPERATION_TYPE = "Operation_Type_Enum";
    public static final String OPERATION_TITLE = "Operation_Title";
    public static final String OPERATION_WORLD_NAME = "operation_world_name";
    public static final String OPERATION_WORLD_SYNC_TYPE = "pperation_world_sync_type";
    private static final String TAG = UploadDownloadService.class.getName();
    private final IBinder binder = new UploadDownloadBinder();
    private final Handler handler = new Handler();
    Intent intent;
    private MinecraftWorldManager minecraftWorldManager;
    private MinecraftData minecraftData;
    private ActivityTracker activityTracker;
    private ConfigurationNotification configurationNotification;
    private MineSyncNotification mineSyncNotification;
    private int progress;
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            if (progress < 100) {
                displayLoggingInfo();
                handler.postDelayed(this, 1000);
            } else {
                try {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    };
    private String operationTitle;

    public UploadDownloadService() {
        this(UploadDownloadService.class.getName());
    }

    public UploadDownloadService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ALog.d(TAG, "onCreate.");
        minecraftWorldManager =
                new MinecraftWorldManager(AppConfiguration.getDropboxAccountManager(getApplicationContext()),
                        getApplicationContext());
        minecraftData = new MinecraftData();
        activityTracker = new ActivityTracker(getApplicationContext());
        configurationNotification = new ConfigurationNotification(getApplicationContext());
        mineSyncNotification = new MineSyncNotification(this);
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ALog.d(TAG, "onHandleIntent. intent[" + intent + "].");
        removeCallbacks();
        handler.postDelayed(sendUpdatesToUI, 5000);

        operationTitle = intent.getStringExtra(OPERATION_TITLE);
        try {
            startProcess((OperationTypeEnum) intent.getSerializableExtra(OPERATION_TYPE),
                    intent.getStringExtra(OPERATION_WORLD_NAME),
                    (SyncTypeEnum) intent.getSerializableExtra(OPERATION_WORLD_SYNC_TYPE));
        } catch (MineSyncException e) {
            ExceptionNotifier.notifyException(getApplicationContext(), e);
        } catch (DropboxAccountException e) {
            ExceptionNotifier.notifyException(getApplicationContext(), e);
        }
    }

    private void displayLoggingInfo() {
        ALog.v(TAG, "entered DisplayLoggingInfo");

        intent.putExtra(BROADCAST_PROGRESS, progress);
        intent.putExtra(OPERATION_TITLE, operationTitle);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        ALog.d(TAG, "onDestroy.");
        super.onDestroy();
    }

    private void removeCallbacks() {
        handler.removeCallbacks(sendUpdatesToUI);
    }

    private void startProcess(OperationTypeEnum operationType, String worldName, SyncTypeEnum syncType) throws MineSyncException, DropboxAccountException {
        switch (operationType) {
            case DOWNLOAD:
                mineSyncNotification.buildNotification(false, false);
                mineSyncNotification.updateSyncState(MineSyncService.SyncStateEnum.UPLOADING_DONWLOADING);
                minecraftWorldManager.downloadWorld(worldName, syncType, getMinecraftWorldListener(worldName));
                break;
            case DOWNLOAD_ALL:
                configurationNotification.show();
                minecraftWorldManager.downloadAll(getMinecraftWorldAllListener(operationType));
                break;
            case UPLOAD:
                mineSyncNotification.buildNotification(false, false);
                mineSyncNotification.updateSyncState(MineSyncService.SyncStateEnum.UPLOADING_DONWLOADING);
                minecraftWorldManager.uploadWorld(worldName, syncType, getMinecraftWorldListener(worldName));
                break;
            case UPLOAD_ALL:
                configurationNotification.show();
                minecraftWorldManager.uploadAll(getMinecraftWorldAllListener(operationType));
                break;
        }
    }

    private MinecraftWorldListener getMinecraftWorldListener(final String worldName) {
        return new AbstractMinecraftWorldListener() {
            @Override
            public void operationFinished(MinecraftWorldActionEnum minecraftWorldAction, File file) {
                if (MinecraftWorldActionEnum.NETWORK == minecraftWorldAction
                        && StringUtils.equals(MinecraftUtils.getWorldName(file), worldName)) {
                    ALog.i(TAG, "File \"%s\" successfully transferred.", file.getName());
                    mineSyncNotification.updateSyncState(MineSyncService.SyncStateEnum.SYNC_ACTIVE);
                }
            }
        };
    }

    private MinecraftWorldListener getMinecraftWorldAllListener(final OperationTypeEnum operationType) {
        return new MinecraftWorldListener() {
            int item = 0;

            @Override
            public void operationFinished(MinecraftWorldActionEnum minecraftWorldAction, File file) {
                if (MinecraftWorldActionEnum.NETWORK.equals(minecraftWorldAction)
                        && OperationTypeEnum.DOWNLOAD_ALL.equals(operationType)) {
                    ALog.d(TAG, "file downloaded [%s]", file.getName());
                    int total = 0;
                    try {
                        total = getDropboxNumberOfFiles();
                    } catch (DropboxAccountException e) {
                        e.printStackTrace();
                    }
                    item++;
                    publishProgress((int) getPercentage(total));

                } else if (OperationTypeEnum.UPLOAD_ALL.equals(operationType)) {
                    int total = minecraftData.getWorlds().size() * 2;
                    item++;
                    publishProgress((int) getPercentage(total));

                } else {
                    throw new IllegalStateException("Unexpected state. operationType \"" + operationType
                            + "\" minecraftWorldAction \"" + minecraftWorldAction
                            + "\" file \"" + file.getName() + "\".");
                }
            }

            private float getPercentage(int total) {
                float percentage = (float) ((item * 100) / total);
                ALog.d(TAG, "progress: %s", percentage);
                return percentage;
            }

            @Override
            public void operationFinished() {
                ALog.d(TAG, "operationFinished");
                progress = 100;
                displayLoggingInfo();
                activityTracker.setConfigurationProcess(true);
                activityTracker.setNeedToShowConfigurationProcessFinishedDialog(true);
                configurationNotification.setFinished();
            }
        };
    }

    private void publishProgress(int percentage) {
        ALog.d(TAG, "publishProgress [" + percentage + "]");
        this.progress = percentage;
        configurationNotification.setProgress(percentage);
    }

    private int getDropboxNumberOfFiles() throws DropboxAccountException {
        try {
            return minecraftWorldManager.getDropboxNumberOfFiles();
        } catch (DbxException e) {
            Log.w(TAG, "Cannot access Dropbox", e);
            return 0;
        }
    }

    public class UploadDownloadBinder extends Binder {
        public UploadDownloadService getService() {
            return UploadDownloadService.this;
        }
    }
}