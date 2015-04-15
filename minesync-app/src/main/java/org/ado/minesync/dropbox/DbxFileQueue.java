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

import android.util.Log;
import com.dropbox.sync.android.DbxFile;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.exception.DbxQueueException;

import java.util.HashMap;
import java.util.Map;

import static org.ado.minesync.config.AppConstants.L;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 22/09/13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class DbxFileQueue {

    private static final String TAG = DbxFileQueue.class.getName();

    private static DbxFileQueue instance;
    private static Map<String, DbxFileElement> queue;

    private DbxFileQueue() {
        queue = new HashMap<String, DbxFileElement>();
    }

    public static DbxFileQueue getInstance() {
        if (instance == null) {
            instance = new DbxFileQueue();
        }
        return instance;
    }

    public boolean contains(String filename) {
        boolean contains = queue.containsKey(filename);
        if(L) Log.v(TAG, "contains [" + filename + "]? " + contains);
        return contains;
    }

    public boolean contains(DbxFile dbxFile) {
        for (Map.Entry<String, DbxFileElement> entry : queue.entrySet()) {
            if (entry.getValue().getFile().equals(dbxFile)) {
                return true;
            }
        }
        return false;
    }

    public void add(String filename, DbxFileElement fileElement) {
        if(L) Log.v(TAG, "add [" + filename + "] fileElement[" + fileElement + "]");
        queue.put(filename, fileElement);
    }

    public boolean remove(String filename) throws DbxQueueException {
        ALog.d(TAG, "remove [" + filename + "].");
        removeFileListener(filename);
        return queue.remove(filename) != null;
    }

    public void removeAll() throws DbxQueueException {
        ALog.d(TAG, "removeAll [" + queue.keySet() + "].");
        for (String filename : queue.keySet()) {
            removeFileListener(filename);
        }
    }

    private void removeFileListener(String filename) throws DbxQueueException {
        if(L) Log.v(TAG, "remove file listener for file [" + filename + "].");
        DbxFileElement dbxFileElement = queue.get(filename);
        if (dbxFileElement == null) {
            throw new DbxQueueException("DbxFileElement \"" + filename + "\" not found.");
        }
        if(L) Log.v(TAG, "dbxFileElement [" + dbxFileElement + "].");
        dbxFileElement.getFile().removeListener(dbxFileElement.getListener());
    }
}
