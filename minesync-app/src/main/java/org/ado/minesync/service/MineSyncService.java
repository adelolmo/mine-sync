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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.config.AppConfiguration;
import org.ado.minesync.dropbox.DbxFileQueue;
import org.ado.minesync.dropbox.WorldPathListener;
import org.ado.minesync.exception.DbxQueueException;
import org.ado.minesync.gui.notification.MineSyncNotification;
import org.ado.minesync.receiver.ScreenReceiver;

import static org.ado.minesync.config.AppConstants.*;

/**
 * Main service. Manages the synchronization of worlds and watches for the foreground app.
 *
 * @author andoni
 * @since 1.0.0
 */
public class MineSyncService extends Service {

    public static final String START_SYNC_ACTION = "org.ado.minesync.START_SYNC";
    public static final String STOP_SYNC_ACTION = "org.ado.minesync.STOP_SYNC";
    private static final String TAG = MineSyncService.class.getName();
    private static boolean watcherEnable = false;
    private final IBinder binder = new MineSyncBinder();
    private DbxAccountManager dbxAccountManager;
    private DbxFileSystem dbxFileSystem;
    private WorldPathListener worldPathListener;
    private MineSyncNotification mineSyncNotification;
    private Handler handler;
    private Runnable runnable;
    private String applicationInForeground;
    private boolean isSyncActive = false;
    private ScreenReceiver screenReceiver;

    public MineSyncService() {
        handler = new Handler();
        this.applicationInForeground = "";
    }

    @Override
    public IBinder onBind(Intent intent) {
        handler.postDelayed(runnable, 100);
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mineSyncNotification = new MineSyncNotification(this);
        worldPathListener = new WorldPathListener(getApplicationContext(), DbxPath.ROOT);
        screenReceiver = new ScreenReceiver();
        initDropboxFileSystem();
        startScreenReceiver();
        ALog.i(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (initDbxAccountManager() && intent != null) {
            mineSyncNotification.buildNotification(isSyncActive);
            handleIntent(intent);
        }
        return START_STICKY;
    }

    @Override
    public void onLowMemory() {
        ALog.i(TAG, "onLowMemory");
        mineSyncNotification.updateSyncState(SyncStateEnum.SYNC_DISABLE);
        stopDropboxSync();
//        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        ALog.i(TAG, "onTaskRemoved - rootIntent [%s].", rootIntent);
        // this should only be notified for KitKat - workaround!
        if (isKitKat()) {
            mineSyncNotification.updateSyncState(SyncStateEnum.SYNC_DISABLE);
        }
    }

    private boolean isKitKat() {
        ALog.i(TAG, "SDK version [%d].", Build.VERSION.SDK_INT);
        boolean b = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        ALog.i(TAG, "is KitKat? [%s].", b);
        return b;
    }

    @Override
    public void onTrimMemory(int level) {
        ALog.i(TAG, "onTrimMemory - level [%d].", level);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ALog.i(TAG, "Destroying service...");
        try {
            if (this.dbxFileSystem != null) {
                DbxFileQueue.getInstance().removeAll();
                stopDropboxSync();
                stopForegroundApplicationWatcher();
                dbxAccountManager = null;
            }
            ALog.i(TAG, "Service destroyed.");
        } catch (DbxQueueException e) {
            ALog.e(TAG, e, "Cannot release file listeners");
        }
        handler.removeCallbacks(this.runnable);
        unregisterReceiver(screenReceiver);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ALog.i(TAG, "onUnbind - stop handler.");
        handler.removeCallbacks(this.runnable);
        return super.onUnbind(intent);
    }

    private void handleIntent(Intent intent) {
        final String action = intent.getAction();
        ALog.d(TAG, "action [%s]", action);
        if (START_SYNC_ACTION.equals(action)) {
            startDropboxSync();
            startForegroundApplicationWatcher();
        } else if (STOP_SYNC_ACTION.equals(action)) {
            stopDropboxSync();
            stopForegroundApplicationWatcher();

        } else {
            boolean foregroundWatcherEnable = isForegroundWatcherEnable(intent);
            ALog.d(TAG, "foreground_watcher_enable? [%s]", foregroundWatcherEnable);
            if (foregroundWatcherEnable) {
                startForegroundApplicationWatcher();
            } else {
                stopForegroundApplicationWatcher();
            }
        }
        mineSyncNotification.updateSyncState(getState(isSyncActive, intent.getBooleanExtra(INTENT_PARAMETER_MINESYNC_UPLOADING_WORLD, false)));
    }

    private void initDropboxFileSystem() {
        try {
            if (initDbxAccountManager()) {
                try {
                    dbxFileSystem = DbxFileSystem.forAccount(dbxAccountManager.getLinkedAccount());
                    if (dbxFileSystem == null) {
                        notificationDropboxConnectionError();
                    }
                } catch (NullPointerException exception) {
                    notificationDropboxConnectionError();
                }
            } else {
                notificationDropboxConnectionError();
            }
        } catch (DbxException.Unauthorized unauthorized) {
            ALog.e(TAG, unauthorized, "Cannot access dropbox folder");
        }
    }

    private void notificationDropboxConnectionError() {
        mineSyncNotification.buildNotification(false);
        mineSyncNotification.updateSyncState(SyncStateEnum.DROPBOX_CONNECTION_ERROR);
    }

    private SyncStateEnum getState(boolean syncActive, boolean mineSyncUploading) {
        if (mineSyncUploading) {
            return SyncStateEnum.UPLOADING_DOWNLOADING;
        } else {
            return syncActive ? SyncStateEnum.SYNC_ACTIVE : SyncStateEnum.SYNC_DISABLE;
        }
    }

    private void startDropboxSync() {
        ALog.i(TAG, "Start Dropbox sync.");
        if (initDbxAccountManager() && dbxAccountManager.hasLinkedAccount() && dbxFileSystem != null) {
            worldPathListener.resume();
            dbxFileSystem.addPathListener(worldPathListener, DbxPath.ROOT, DbxFileSystem.PathListener.Mode.PATH_OR_CHILD);
            isSyncActive = true;
        } else {
            ALog.i(TAG, "Service started but not dropbox account is linked. Stopping service now.");
            mineSyncNotification.updateSyncState(SyncStateEnum.DROPBOX_CONNECTION_ERROR);
        }
    }

    private boolean initDbxAccountManager() {
        dbxAccountManager = AppConfiguration.getDropboxAccountManager(getApplicationContext());
        return dbxAccountManager != null;
    }

    private void stopDropboxSync() {
        ALog.i(TAG, "Stop Dropbox sync.");
        if (initDbxAccountManager() && dbxAccountManager.hasLinkedAccount()) {
            worldPathListener.pause();
            dbxFileSystem.removePathListenerForAll(worldPathListener);
            isSyncActive = false;
        }
    }

    private void startScreenReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, filter);
    }

    private boolean isForegroundWatcherEnable(Intent intent) {
        return intent == null
                || intent.getBooleanExtra(INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE, false);
    }

    public void startForegroundApplicationWatcher() {
        ALog.d(TAG, "start foreground application watcher.");
        watcherEnable = true;
        runnable = createRunnable();
        handler.postDelayed(runnable, 100);
    }

    public void stopForegroundApplicationWatcher() {
        ALog.d(TAG, "stop foreground application watcher.");
        watcherEnable = false;
        handler.removeCallbacks(this.runnable);
    }

    private Runnable createRunnable() {
        return new Runnable() {
            public void run() {
                if (watcherEnable) {
                    logApplications();
                    handler.postDelayed(this, 5000);
                } else {
                    try {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        };
    }

    private void logApplications() {
        ActivityManager am = (ActivityManager) super.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        String packageName = am.getRunningAppProcesses().get(0).processName;
        ALog.v(TAG, "%s     in use[%s]", packageName, applicationInForeground);
        if (!applicationInForeground.equals(packageName)) {
            notifyApplicationInUse(packageName);
            this.applicationInForeground = packageName;
        }
    }

    private void notifyApplicationInUse(String applicationInUse) {
        Intent foregroundAppIntent = new Intent(INTENT_FOREGROUND_APP);
        foregroundAppIntent.putExtra(INTENT_PARAMETER_FOREGROUND_APP, applicationInUse);
        sendBroadcast(foregroundAppIntent);
        ALog.d(TAG, "application currently in foreground [%s]", applicationInUse);
    }

    public enum SyncStateEnum {
        SYNC_ACTIVE,
        SYNC_DISABLE,
        DROPBOX_CONNECTION_ERROR,
        UPLOADING_DOWNLOADING
    }

    public class MineSyncBinder extends Binder {
        MineSyncService getService() {
            return MineSyncService.this;
        }
    }
}