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

package org.ado.minesync.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.dropbox.sync.android.*;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.config.AppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class description here.
 *
 * @author adelolmo
 * @since 05.09.2013
 */
public class SyncManagerReceiver extends BroadcastReceiver {

    private static final String TAG = SyncManagerReceiver.class.getName();
    private static ConnectionTypeEnum connectionState;

    private static List<SyncFile> queueSyncFileList;

    public SyncManagerReceiver() {
        super();
        queueSyncFileList = new ArrayList<SyncFile>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // WIFI_STATE_ENABLED

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            boolean connected = info.isConnected();
            if (connected) {
                connectionState = ConnectionTypeEnum.WIFI;
                this.processPendingUploads(context);
            } else {
                connectionState = ConnectionTypeEnum.NO_CONNECTION;
            }
        }

        if ("org.ado.minesync.UPLOAD_ALL".equalsIgnoreCase(intent.getAction())) {
            this.initializeConnectionStateIfUnknown(context);
            if (ConnectionTypeEnum.WIFI.equals(connectionState)) {
                this.uploadFile(context, (SyncFile) intent.getSerializableExtra("file"));
            } else {
                this.queueFile((SyncFile) intent.getSerializableExtra("file"));
            }
        }
    }

    private void processPendingUploads(Context context) {
        for (SyncFile syncFile : queueSyncFileList) {
            this.uploadFile(context, syncFile);
        }
    }

    private void uploadFile(Context context, SyncFile syncFile) {
        try {
            DbxAccountManager dbxAccountManager = AppConfiguration.getDropboxAccountManager(context);
            if (dbxAccountManager.hasLinkedAccount()) {
                DbxFileSystem dbxFileSystem = DbxFileSystem.forAccount(dbxAccountManager.getLinkedAccount());
                uploadFile(context, syncFile.getFile(), getDbxFile(dbxFileSystem, syncFile.getFile()));

            }
        } catch (IOException e) {
            ALog.w(TAG, "File upload failed! [%s]", syncFile.getFile().getAbsolutePath());
            Intent intent = new Intent("org.ado.minesync.UPLOAD_FAILED");
            intent.putExtra("file", syncFile.getFile().getAbsolutePath());
            context.sendBroadcast(intent);
        }
    }

    private void queueFile(SyncFile syncFile) {
        queueSyncFileList.add(syncFile);
    }

    private void uploadFile(final Context context, final File localFile, DbxFile dbxFile) throws IOException {
        ALog.d(TAG, "upload local localFile [%s] to Dropbox.", localFile.getAbsolutePath());
        dbxFile.writeFromExistingFile(localFile, false);
        dbxFile.addListener(new DbxFile.Listener() {
            @Override
            public void onFileChange(DbxFile dbxFile) {
                try {
                    if (dbxFile.getSyncStatus().pending.equals(DbxFileStatus.PendingOperation.NONE)) {
                        ALog.i(TAG, "Uploading of localFile successful [%s]", dbxFile.getPath());
                        ALog.d(TAG, "close dbxFile[%s].", dbxFile.getPath().getName());
                        dbxFile.close();
                        Intent intent = new Intent("org.ado.minesync.UPLOAD_FINISHED");
                        intent.putExtra("file", localFile.getAbsolutePath());
                        context.sendBroadcast(intent);
                    }
                } catch (DbxException e) {
                    // ignore
                }
            }
        });
    }

    private DbxFile getDbxFile(DbxFileSystem dbxFileSystem, File localFile) throws DbxException {
        return getDbxFile(dbxFileSystem, new DbxPath(DbxPath.ROOT, localFile.getName()));
    }

    private DbxFile getDbxFile(DbxFileSystem dbxFileSystem, DbxPath dbxFile) throws DbxException {
        if (dbxFileSystem.exists(dbxFile)) {
            return dbxFileSystem.open(dbxFile);
        } else {
            return dbxFileSystem.create(dbxFile);
        }
    }

    private void initializeConnectionStateIfUnknown(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            connectionState = ConnectionTypeEnum.WIFI;
        } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
            connectionState = ConnectionTypeEnum.MOBILE;
        } else {
            connectionState = ConnectionTypeEnum.NO_CONNECTION;
        }
    }
}
