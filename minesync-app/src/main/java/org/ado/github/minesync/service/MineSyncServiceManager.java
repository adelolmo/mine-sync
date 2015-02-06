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

package org.ado.github.minesync.service;

import android.content.Context;
import android.content.Intent;
import org.ado.github.minesync.commons.ALog;

import static org.ado.github.minesync.config.AppConstants.INTENT_PARAMETER_MINESYNC_UPLOADING_WORLD;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Manages the start and stop of the <code>MineSyncService</code>.
 *
 * @author andoni
 * @see org.ado.github.minesync.service.MineSyncService
 * @since 1.2.0
 */
public class MineSyncServiceManager {

    private static final String TAG = MineSyncServiceManager.class.getName();

    public static void startWorldSync(Context context) {
        notNull(context, "context cannot be null");

        ALog.d(TAG, "start world sync.");
        Intent service = new Intent(context, MineSyncService.class);
        service.setAction(MineSyncService.START_SYNC_ACTION);
        context.startService(service);
    }

    public static void stopWorldSync(Context context) {
        notNull(context, "context cannot be null");

        ALog.d(TAG, "stop world sync.");
        Intent service = new Intent(context, MineSyncService.class);
        service.setAction(MineSyncService.STOP_SYNC_ACTION);
        service.putExtra(INTENT_PARAMETER_MINESYNC_UPLOADING_WORLD, true);
        context.startService(service);
    }
}
