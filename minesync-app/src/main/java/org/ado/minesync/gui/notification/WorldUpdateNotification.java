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
import android.support.v4.app.NotificationCompat;
import org.ado.minesync.R;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.gui.ActiveWorldUpdateCache;
import org.ado.minesync.gui.MineSyncMainActivity;
import org.ado.minesync.gui.widget.WorldListView;

/**
 * Android notification bar handler for Minecraft world updates.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldUpdateNotification {

    private static final String TAG = WorldUpdateNotification.class.getName();

    private Context context;

    public WorldUpdateNotification(Context context) {
        this.context = context;
    }

    public void notifyWorldUpdate(Context context, String worldName) {
        WorldListView worldListView = ActiveWorldUpdateCache.getInstance().get(worldName);
        if (isNotificationNeeded(worldListView)) {
            ALog.d(TAG, "add notification for world [" + worldName + "]");
            notify(context, worldName);

        } else {
            ALog.d(TAG, "no notification needed for world [" + worldName + "]");
            ActiveWorldUpdateCache.getInstance().remove(worldName);
        }
    }

    private void notify(Context context, String worldName) {
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(getResourceText(context, R.string.label_notification_sync_title))
                .setContentText(getResourceText(context, R.string.txt_notification_sync_text_downloaded, worldName))
                .setSmallIcon(R.drawable.ic_stat_notify_sync)
                .setContentIntent(getPendingIntent())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private boolean isNotificationNeeded(WorldListView worldListView) {
        return worldListView == null;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MineSyncMainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private CharSequence getResourceText(Context context, int resourceId, String string) {
        return String.format(getResourceText(context, resourceId).toString(), string);
    }

    private CharSequence getResourceText(Context context, int resourceId) {
        return context.getResources().getText(resourceId);
    }
}
