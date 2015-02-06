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

import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileStatus;
import org.ado.github.minesync.github.MockitoTestCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@Ignore
public class DbxFilePendingStatusTest extends MockitoTestCase {

    @Mock
    private DbxFile dbxFileMock;
    @Mock
    private DbxFileStatus statusDownload;
    @Mock
    private DbxFileStatus statusUpload;
    @Mock
    private DbxFileStatus statusNone;

    @Before
    public void setUp() {
        statusDownload = getDownloadStatus();
        when(statusDownload.pending).thenReturn(DbxFileStatus.PendingOperation.DOWNLOAD);
        when(statusUpload.pending).thenReturn(DbxFileStatus.PendingOperation.UPLOAD);
        when(statusNone.pending).thenReturn(DbxFileStatus.PendingOperation.NONE);
    }

    private DbxFileStatus getDownloadStatus() {
//        return new DbxFileStatus(true, true, DbxFileStatus.PendingOperation.DOWNLOAD_ALL, 0, 0);
        return null;
    }

    @Test
    public void testGetNewSyncStatus_newFile() throws Exception {
        when(dbxFileMock.getNewerStatus()).thenReturn(null);
        when(dbxFileMock.getSyncStatus()).thenReturn(statusDownload);

        DbxFileStatus.PendingOperation status = DbxFilePendingStatus.getNewSyncStatus(dbxFileMock);

        assertEquals("download", DbxFileStatus.PendingOperation.DOWNLOAD, status);
    }

    @Test
    public void testGetNewSyncStatus_updateFile() throws Exception {
        when(dbxFileMock.getNewerStatus()).thenReturn(statusDownload);
        when(dbxFileMock.getSyncStatus()).thenReturn(statusNone);

        DbxFileStatus.PendingOperation status = DbxFilePendingStatus.getNewSyncStatus(dbxFileMock);

        assertEquals("download", DbxFileStatus.PendingOperation.DOWNLOAD, status);
    }

    @Test
    public void testGetNewSyncStatus_noChange() throws Exception {
        when(dbxFileMock.getNewerStatus()).thenReturn(null);
        when(dbxFileMock.getSyncStatus()).thenReturn(statusNone);

        DbxFileStatus.PendingOperation status = DbxFilePendingStatus.getNewSyncStatus(dbxFileMock);

        assertEquals("download", DbxFileStatus.PendingOperation.NONE, status);
    }
}