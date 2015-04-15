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

package org.ado.minesync.dropbox;

import android.content.Context;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileStatus;
import org.ado.minesync.commons.ALog;

import java.io.IOException;

import static org.ado.minesync.dropbox.DbxFilePendingStatus.getNewSyncStatus;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Listener to control changes in a dropbox file.
 *
 * @author andoni
 * @since 1.0.0
 */
public class DownloadFileListener implements DbxFile.Listener {

    private static final String TAG = DownloadFileListener.class.getName();

    private Context context;
    private AbstractDropboxFileHandler[] dropboxFileHandlers;

    public DownloadFileListener(Context context) {
        notNull(context, "context cannot be null");

        this.context = context;
        dropboxFileHandlers = new AbstractDropboxFileHandler[]{new WorldFileHandler(context), new JsonFileHandler()};
    }

    @Override
    public void onFileChange(DbxFile dbxFile) {
        notNull(dbxFile, "dbxFile cannot be null");

        if (DbxFileQueue.getInstance().contains(dbxFile)) {

            ALog.v(TAG, "file [" + dbxFile.getPath()
                    + "] sync status [" + getNewSyncStatus(dbxFile) + "].");

            if (getNewSyncStatus(dbxFile).equals(DbxFileStatus.PendingOperation.NONE)) {

                for (AbstractDropboxFileHandler dropboxFileHandler : dropboxFileHandlers) {
                    try {
                        if (dropboxFileHandler.canHandleFile(dbxFile)) {
                            dropboxFileHandler.handleFile(dbxFile, context);
                        }
                    } catch (IOException e) {
                        ALog.e(TAG, e, "Unable to handle file \"" + dbxFile.getPath().getName() + "\".");
                    }
                }
            }
        }
    }
}