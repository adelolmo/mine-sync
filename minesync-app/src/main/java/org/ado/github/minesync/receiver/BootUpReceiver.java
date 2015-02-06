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
import android.os.PowerManager;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.service.MineSyncService;

import static org.ado.github.minesync.config.AppConstants.INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE;

public class BootUpReceiver extends BroadcastReceiver {

    private static final String TAG = BootUpReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            ALog.i(TAG, "screen is on? [" + isScreenOn + "]");
            Intent mineSyncServiceIntent = new Intent(context, MineSyncService.class);
            mineSyncServiceIntent.setAction(MineSyncService.START_SYNC_ACTION);
            mineSyncServiceIntent.putExtra(INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE, isScreenOn);
            context.startService(mineSyncServiceIntent);
            ALog.i(TAG, "Boot completed intent received.");
        }
    }
}