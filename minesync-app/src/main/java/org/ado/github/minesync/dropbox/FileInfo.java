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

import java.util.Date;

/**
 * Class description here.
 *
 * @author andoni
 * @since 1.2.0
 */
public class FileInfo {

    private String path;
    private boolean isFolder;
    private long size;
    private Date modifiedTime;

    public FileInfo(String path, boolean isFolder, long size, Date modifiedTime) {
        this.path = path;
        this.isFolder = isFolder;
        this.size = size;
        this.modifiedTime = modifiedTime;
    }

    public String getPath() {
        return path;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public long getSize() {
        return size;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (isFolder != fileInfo.isFolder) return false;
        if (size != fileInfo.size) return false;
        if (modifiedTime != null ? !modifiedTime.equals(fileInfo.modifiedTime) : fileInfo.modifiedTime != null)
            return false;
        if (path != null ? !path.equals(fileInfo.path) : fileInfo.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (isFolder ? 1 : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (modifiedTime != null ? modifiedTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileInfo{");
        sb.append("path='").append(path).append('\'');
        sb.append(", isFolder=").append(isFolder);
        sb.append(", size=").append(size);
        sb.append(", modifiedTime=").append(modifiedTime);
        sb.append('}');
        return sb.toString();
    }
}
