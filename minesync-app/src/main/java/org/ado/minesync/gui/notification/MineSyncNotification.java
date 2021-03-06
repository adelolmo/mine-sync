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

package org.ado.minesync.gui.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import org.ado.minesync.R;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.gui.MineSyncMainActivity;
import org.ado.minesync.service.MineSyncService;

import static org.ado.minesync.config.AppConstants.NOTIFICATION_SYNC;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Notification for the MineSync Service.
 *
 * @author andoni
 * @since 1.0.0
 */
public class MineSyncNotification {

    private static final String TAG = MineSyncNotification.class.getName();
    private final Service service;
    private final NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews notificationTemplate;

    public MineSyncNotification(final Service service) {
        this.service = service;
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notNull(notificationManager, "notificationManager shouldn't be null");
    }

    public void buildNotification(boolean syncActive) {
        notificationTemplate = new RemoteViews(service.getPackageName(), R.layout.layout_notification);
        initializeNotification(getMainPendingIntent());
        initActions(syncActive);
    }

    public void updateSyncState(MineSyncService.SyncStateEnum state) {
        ALog.d(TAG, "updateSyncState - state [%s].", state);
        if (notification == null) {
            ALog.w(TAG, "Notification is null. Initializing now...");
            initializeNotification(getMainPendingIntent());
        }
        if (notificationTemplate != null) {
            switch (state) {
                case UPLOADING_DOWNLOADING:
                    notification.icon = R.drawable.ic_stat_upload_download;
                    notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_upload_download);
                    notificationTemplate.setViewVisibility(R.id.button_service_start, View.INVISIBLE);
                    notificationTemplate.setViewVisibility(R.id.button_service_stop, View.INVISIBLE);
                    notificationTemplate
                            .setTextViewText(R.id.txt_notification_sync_content,
                                    getResourceText(R.string.txt_notification_service_text_active));
                    break;
                case SYNC_ACTIVE:
                    notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_service);
                    notificationTemplate.setViewVisibility(R.id.button_service_start, View.INVISIBLE);
                    notificationTemplate.setViewVisibility(R.id.button_service_stop, View.VISIBLE);
                    notificationTemplate
                            .setTextViewText(R.id.txt_notification_sync_content,
                                    getResourceText(R.string.txt_notification_service_text_active));
                    break;
                case SYNC_DISABLE:
                    notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_service_disable);
                    notificationTemplate.setViewVisibility(R.id.button_service_start, View.VISIBLE);
                    notificationTemplate.setViewVisibility(R.id.button_service_stop, View.INVISIBLE);
                    notificationTemplate
                            .setTextViewText(R.id.txt_notification_sync_content,
                                    getResourceText(R.string.txt_notification_service_text_paused));
                    break;
                case DROPBOX_CONNECTION_ERROR:
                    notification.icon = R.drawable.ic_stat_error;
                    notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_error);
                    notificationTemplate.setViewVisibility(R.id.button_service_start, View.VISIBLE);
                    notificationTemplate.setViewVisibility(R.id.button_service_stop, View.INVISIBLE);
                    notificationTemplate
                            .setTextViewText(R.id.txt_notification_sync_content,
                                    getResourceText(R.string.txt_notification_dropbox_connection_error));
                    break;
            }
            if (isLolliPop()) {
                notificationTemplate.setTextColor(R.id.txt_notification_sync_title, getColor(android.R.color.black));
                notificationTemplate.setTextColor(R.id.txt_notification_sync_content, getColor(android.R.color.black));
            }
            notificationTemplate.setOnClickPendingIntent(R.id.button_service_start,
                    getSyncAction(MineSyncService.START_SYNC_ACTION));
            notificationTemplate.setOnClickPendingIntent(R.id.button_service_stop,
                    getSyncAction(MineSyncService.STOP_SYNC_ACTION));
        }
        notificationManager.notify(NOTIFICATION_SYNC, notification);
    }

    private void initActions(boolean syncActive) {
        setState(syncActive);
        notificationTemplate.setOnClickPendingIntent(R.id.button_service_start, getSyncAction(MineSyncService.START_SYNC_ACTION));
        notificationTemplate.setOnClickPendingIntent(R.id.button_service_stop, getSyncAction(MineSyncService.STOP_SYNC_ACTION));
    }

    private void initializeNotification(PendingIntent pendingIntent) {
        notification = new NotificationCompat.Builder(service.getApplicationContext())
                .setTicker(getResourceText(R.string.label_notification_service_title))
                .setContent(notificationTemplate)
                .setSmallIcon(android.R.color.transparent)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setAutoCancel(false)
                .setColor(getColor(R.color.dropbox_blue))
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
    }

    private void setState(boolean syncActive) {
        if (syncActive) {
            notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_service);
            notificationTemplate.setViewVisibility(R.id.button_service_start, View.INVISIBLE);
            notificationTemplate.setViewVisibility(R.id.button_service_stop, View.VISIBLE);
            notificationTemplate
                    .setTextViewText(R.id.txt_notification_sync_content, getResourceText(R.string.txt_notification_service_text_active));
        } else {
            notificationTemplate.setImageViewResource(R.id.status_icon, R.drawable.ic_stat_service_disable);
            notificationTemplate.setViewVisibility(R.id.button_service_start, View.VISIBLE);
            notificationTemplate.setViewVisibility(R.id.button_service_stop, View.INVISIBLE);
            notificationTemplate
                    .setTextViewText(R.id.txt_notification_sync_content, getResourceText(R.string.txt_notification_service_text_paused));
        }
    }

    private PendingIntent getSyncAction(String action) {
        final ComponentName serviceName = new ComponentName(service, MineSyncService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(service, getRequestCode(action), intent, 0);
    }

    private int getRequestCode(String action) {
        if (MineSyncService.START_SYNC_ACTION.equals(action)) {
            return 1;
        } else {
            return 2;
        }
    }

    private PendingIntent getMainPendingIntent() {
        Intent intent = new Intent(service, MineSyncMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(service, 0, intent, 0);
    }

    private boolean isLolliPop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private CharSequence getResourceText(int resourceId) {
        return service.getResources().getText(resourceId);
    }

    private int getColor(int black) {
        return service.getResources().getColor(black);
    }
}
