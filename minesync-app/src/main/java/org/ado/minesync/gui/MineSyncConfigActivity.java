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

package org.ado.minesync.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import org.ado.minesync.ActivityTracker;
import org.ado.minesync.R;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.service.OperationTypeEnum;
import org.ado.minesync.service.UploadDownloadService;

public class MineSyncConfigActivity extends Activity {

    private static final String TAG = MineSyncConfigActivity.class.getName();

    private ProgressDialog progressDialog;
    private ActivityTracker activityTracker;
    private ServiceConnection mConnection;
    private UploadDownloadService uploadDownloadService;

    private MinecraftDropboxStatusEnum minecraftDropboxStatusEnum;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ALog.d(TAG, "onCreate. savedInstanceState [" + savedInstanceState + "]");

        createContentView();
        activityTracker = new ActivityTracker(getApplicationContext());
        mConnection = getServiceConnection();
        minecraftDropboxStatusEnum = getIntentExtraStatus();
        bindUploadDownloadService();
        registerReceiver(broadcastReceiver, new IntentFilter(UploadDownloadService.BROADCAST_ACTION));
    }

    private MinecraftDropboxStatusEnum getIntentExtraStatus() {
        return getIntent().getSerializableExtra("status") != null
                ? (MinecraftDropboxStatusEnum) getIntent().getSerializableExtra("status")
                : MinecraftDropboxStatusEnum.NONE;
    }

    @Override
    protected void onStart() {
        ALog.d(TAG, "onStart");
        ALog.d(TAG, "status [" + minecraftDropboxStatusEnum + "]");
        switch (minecraftDropboxStatusEnum) {
            case LOCAL_ONLY:
                uploadToDropbox(null);
                break;
            case DROPBOX_ONLY:
                downloadFromDropbox(null);
                break;
        }
        super.onStart();
    }

    private void createContentView() {
        setContentView(R.layout.activity_config);
    }

    @Override
    protected void onResume() {
        ALog.d(TAG, "onResume");
        ALog.d(TAG, "resume. uploadDownloadService [" + uploadDownloadService + "].");
        ALog.d(TAG, "resume. progressDialog [" + progressDialog + "].");
        super.onResume();
    }

    private void bindUploadDownloadService() {
        Intent bindIntent = new Intent(this, UploadDownloadService.class);
        ALog.d(TAG, "bindService - mConnection[" + mConnection + "].");
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection getServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ALog.d(TAG, "onServiceConnected - name[" + name + "] service[" + service.getClass().getSimpleName() + "].");
                uploadDownloadService = ((UploadDownloadService.UploadDownloadBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ALog.d(TAG, "onServiceDisconnected - name[" + name + "].");
                uploadDownloadService = null;
            }
        };
    }

    @Override
    protected void onPause() {
        ALog.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        ALog.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ALog.d(TAG, "onDestroy");
        unbindService(mConnection);
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            // ignore
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ALog.d(TAG, "onKeyDown code [" + keyCode + "] event [" + event + "]");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            activityTracker.setConfigurationProcess(true);
            activityTracker.setNeedToShowConfigurationProcessFinishedDialog(true);
            setResult(RESULT_OK);
        }

        return super.onKeyDown(keyCode, event);
    }

    public void uploadToDropbox(View view) {
        ALog.i(TAG, "Uploading all worlds to Dropbox.");
        createAndShowProgressDialog(100, getResources().getText(R.string.label_upload), 0);
        Intent service = new Intent(this, UploadDownloadService.class);
        service.putExtra(UploadDownloadService.OPERATION_TYPE, OperationTypeEnum.UPLOAD_ALL);
        service.putExtra(UploadDownloadService.OPERATION_TITLE, getResources().getText(R.string.label_upload));
        startService(service);
    }

    public void downloadFromDropbox(View view) {
        ALog.i(TAG, "Downloading all worlds from Dropbox.");
        createAndShowProgressDialog(100, getResources().getText(R.string.label_download), 0);
        Intent service = new Intent(this, UploadDownloadService.class);
        service.putExtra(UploadDownloadService.OPERATION_TYPE, OperationTypeEnum.DOWNLOAD_ALL);
        service.putExtra(UploadDownloadService.OPERATION_TITLE, getResources().getText(R.string.label_download));
        startService(service);
    }

    private void createAndShowProgressDialog(int max, CharSequence title, int progress) {
        ALog.i(TAG, "Create progress dialog. title [" + title + "] max [" + max + "] progress [" + progress + "]");
        ALog.d(TAG, "progressDialog is null? [" + progressDialog + "]");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(progress);
        progressDialog.setMax(max);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    private void updateUI(Intent intent) {
        int progress = intent.getIntExtra(UploadDownloadService.BROADCAST_PROGRESS, 1);
        String title = intent.getStringExtra(UploadDownloadService.OPERATION_TITLE);
        ALog.v(TAG, "updateUI - progress[" + progress + "] title[" + title + "].");
        if (progressDialog == null) {
            createAndShowProgressDialog(100, title, progress);
        }
        progressDialog.setProgress(progress);

        if (progress == 100) {
            configurationProcessFinished();
        }
    }

    private void configurationProcessFinished() {
        sleep(2000);
        progressDialog.dismiss();
        unregisterReceiver(broadcastReceiver);
        ALog.d(TAG, "setResult [OK][-1]");
        setResult(RESULT_OK);
        finish();
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}