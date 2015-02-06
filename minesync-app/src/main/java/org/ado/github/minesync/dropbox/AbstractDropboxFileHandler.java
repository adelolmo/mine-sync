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
import com.dropbox.sync.android.DbxFile;
import org.ado.github.minesync.exception.DbxQueueException;

import java.io.IOException;

/**
 * Abstract class for the user of <code>DropboxFileHandler</code> and hides dropbox objects.
 *
 * @author andoni
 * @since 1.2.0
 */
public abstract class AbstractDropboxFileHandler implements DropboxFileHandler {

    public boolean canHandleFile(DbxFile dbxFile) {
        try {
            return canHandle(new FileInfo(dbxFile.getPath().getName(),
                    dbxFile.getInfo().isFolder,
                    dbxFile.getInfo().size,
                    dbxFile.getInfo().modifiedTime));
        } catch (Exception e) {
            return false;
        }
    }

    public void handleFile(DbxFile dbxFile, Context context) throws IOException {
        try {
            dbxFile.update();
            handle(dbxFile.getPath().getName(), dbxFile.getReadStream(), context);

        } finally {
            try {
                DbxFileQueue.getInstance().remove(dbxFile.getPath().getName());
            } catch (DbxQueueException e) {
                // ignore
            }
            DropboxUtils.closeFileQuietly(dbxFile);
        }
    }
}