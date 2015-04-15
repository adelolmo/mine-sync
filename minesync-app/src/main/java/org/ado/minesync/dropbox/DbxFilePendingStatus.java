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

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileStatus;
import org.ado.minesync.commons.ALog;

/**
 * Class with util methods to get the sync status of a dropbox file.
 *
 * @author andoni
 * @since 1.2.0
 */
public class DbxFilePendingStatus {

    private static final String TAG = DbxFilePendingStatus.class.getName();

    public static DbxFileStatus.PendingOperation getNewSyncStatus(DbxFile dbxFile) {
        try {
            ALog.d(TAG, "file [" + dbxFile.getPath()
                    + "] dbxFile.getSyncStatus()? [" + dbxFile.getSyncStatus()
                    + "] dbxFile.getSyncStatus().pending [" + getPending(dbxFile.getSyncStatus())
                    + "] dbxFile.getNewerStatus() [" + dbxFile.getNewerStatus()
                    + "] dbxFile.getNewerStatus().pending [ " + getPending(dbxFile.getNewerStatus()) + "]");
            return dbxFile.getNewerStatus() != null ?
                    dbxFile.getNewerStatus().pending :
                    dbxFile.getSyncStatus().pending;
        } catch (DbxException e) {
            return DbxFileStatus.PendingOperation.NONE;
        }
    }

    private static DbxFileStatus.PendingOperation getPending(DbxFileStatus syncStatus) {
        return syncStatus != null ? syncStatus.pending : null;
    }
}
