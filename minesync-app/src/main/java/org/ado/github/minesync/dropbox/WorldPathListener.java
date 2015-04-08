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

package org.ado.github.minesync.dropbox;

import android.content.Context;
import com.dropbox.sync.android.*;
import org.ado.github.minesync.commons.ALog;

import static org.ado.github.minesync.dropbox.DbxFilePendingStatus.getNewSyncStatus;

/**
 * Dropbox path listener.
 *
 * @author andoni
 * @since 1.0.0
 */
public class WorldPathListener implements DbxFileSystem.PathListener {

    private final String TAG = WorldPathListener.class.getName();

    private Context context;
    private DbxPath dbxPath;
    private boolean active;

    public WorldPathListener(Context context, DbxPath dbxPath) {
        ALog.i(TAG, "WorldPathListener created for [" + dbxPath + "].");
        this.context = context;
        this.dbxPath = dbxPath;
        active = true;
    }

    public void pause() {
        ALog.d(TAG, "pause sync of path [" + dbxPath + "]");
        active = false;
    }

    public void resume() {
        ALog.d(TAG, "resume sync of path [" + dbxPath + "]");
        active = true;
    }

    @Override
    public void onPathChange(DbxFileSystem dbxFileSystem, DbxPath dbxPath, Mode mode) {
        if (active) {
            try {
                for (DbxFileInfo dbxFileInfo : dbxFileSystem.listFolder(dbxPath)) {
                    if (!dbxFileInfo.isFolder) {
                        if (!DbxFileQueue.getInstance().contains(dbxFileInfo.path.getName())) {
                            addFileListener(dbxFileSystem, dbxFileInfo);
                        } else {
                            ALog.d(TAG, "File [" + dbxFileInfo.path + "] was already processed.");
                        }
                    }
                }
            } catch (DbxException e) {
                ALog.e(TAG, e, "Unable to retrieve list of files from \"" + dbxPath.getName() + "\".");
            }
        }
    }

    private void addFileListener(DbxFileSystem dbxFileSystem, DbxFileInfo dbxFileInfo) {
        try {
            DbxFile dbxFile = DropboxUtils.openFile(TAG, dbxFileSystem, dbxFileInfo);
            ALog.v(TAG, "file [" + dbxFileInfo.path.getName()
                    + "] sync status [" + dbxFile.getSyncStatus().pending.name()
                    + "] new sync status [" + getNewSyncStatus(dbxFile).name() + "].");

            if (DbxFilePendingStatus.getNewSyncStatus(dbxFile).equals(DbxFileStatus.PendingOperation.DOWNLOAD)) {
                ALog.i(TAG, "Downloading pending file [" + dbxFile.getPath() + "]");
                DownloadFileListener fileListener = new DownloadFileListener(context);
                DbxFileQueue.getInstance().add(dbxFileInfo.path.getName(), new DbxFileElement(dbxFile, fileListener));
                dbxFile.addListener(fileListener);

            } else {
                DropboxUtils.closeFile(TAG, dbxFile);
            }
        } catch (DbxException e) {
            ALog.e(TAG, e, "Cannot open remote file \"" + dbxFileInfo.path + "\".");
        }
    }
}
