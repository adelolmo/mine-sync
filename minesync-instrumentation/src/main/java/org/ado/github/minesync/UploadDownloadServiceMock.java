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

package org.ado.github.minesync;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.service.UploadDownloadService;

/**
 * Class description here.
 *
 * @author andoni
 * @since 16.06.2014
 */
public class UploadDownloadServiceMock extends IntentService {

    private static final String TAG = UploadDownloadServiceMock.class.getName();

    private int progress;
    private String operationTitle;
    Intent intent;

    public UploadDownloadServiceMock() {
        super(UploadDownloadServiceMock.class.getName());
    }

    @Override
    public void onCreate() {
        intent = new Intent(UploadDownloadService.BROADCAST_ACTION);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent - intent [" + intent + "]");
        operationTitle = intent.getStringExtra(UploadDownloadService.OPERATION_TITLE);
        progress = 100;
        displayLoggingInfo();
    }

    private void displayLoggingInfo() {
        ALog.d(TAG, "entered DisplayLoggingInfo");

        intent.putExtra(UploadDownloadService.BROADCAST_PROGRESS, progress);
        intent.putExtra(UploadDownloadService.OPERATION_TITLE, operationTitle);
        sendBroadcast(intent);
    }
}
