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

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 22/09/13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class DbxFileElement {

    private DbxFile file;
    private DbxFile.Listener listener;

    public DbxFileElement(DbxFile file, DbxFile.Listener listener) {
        this.file = file;
        this.listener = listener;
    }

    public DbxFile getFile() {
        return file;
    }

    public void setFile(DbxFile file) {
        this.file = file;
    }

    public DbxFile.Listener getListener() {
        return listener;
    }

    public void setListener(DbxFile.Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbxFileElement that = (DbxFileElement) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (listener != null ? !listener.equals(that.listener) : that.listener != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (listener != null ? listener.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbxFileElement{" +
                "file=" + file.getPath() +
                ", listener=" + listener +
                '}';
    }
}
