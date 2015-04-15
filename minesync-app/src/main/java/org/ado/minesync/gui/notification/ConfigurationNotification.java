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
import android.content.Context;
import android.content.Intent;
import org.ado.minesync.R;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.gui.MineSyncConfigActivity;
import org.ado.minesync.gui.MineSyncMainActivity;

import static org.ado.minesync.config.AppConstants.NOTIFICATION_CONFIGURATION;

/**
 * Notification for the configuration process.
 *
 * @author andoni
 * @since 1.0.5
 */
public class ConfigurationNotification {

    private static final String TAG = ConfigurationNotification.class.getName();

    private Context context;
    private Notification.Builder builder;

    public ConfigurationNotification(Context context) {
        this.context = context;
        this.builder = getBuilder();
    }

    private Notification.Builder getBuilder() {
        return new Notification.Builder(context)
                .setContentTitle(getResourceText(context, R.string.label_notification_config_title))
                .setContentText(getResourceText(context, R.string.txt_notification_config_text_active))
                .setSmallIcon(R.drawable.ic_stat_upload_download)
                .setContentIntent(getConfigPendingIntent())
                .setAutoCancel(false);
    }

    public void show() {
        ALog.d(TAG, "show");
        notifyConfiguration(builder.build());
    }

    public void setProgress(int progress) {
        notifyConfiguration(builder.setProgress(100, progress, false).build());
    }

    public void setFinished() {
        ALog.d(TAG, "set finished");
        notifyConfiguration(
                new Notification.Builder(context)
                        .setContentTitle(getResourceText(context, R.string.label_notification_config_title))
                        .setContentText(getResourceText(context, R.string.txt_notification_config_text_finished))
                        .setSmallIcon(R.drawable.ic_stat_config_done)
                        .setContentIntent(getMainPendingIntent())
                        .setAutoCancel(true)
                        .build());
    }

    private void notifyConfiguration(Notification build) {
        getNotificationManager(context).notify(NOTIFICATION_CONFIGURATION, build);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private CharSequence getResourceText(Context context, int resourceId, String string) {
        return String.format(getResourceText(context, resourceId).toString(), string);
    }

    private CharSequence getResourceText(Context context, int resourceId) {
        return context.getResources().getText(resourceId);
    }

    private PendingIntent getConfigPendingIntent() {
        Intent intent = new Intent(context, MineSyncConfigActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private PendingIntent getMainPendingIntent() {
        Intent intent = new Intent(context, MineSyncMainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }
}