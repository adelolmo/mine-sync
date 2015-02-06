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

package org.ado.github.minesync.minecraft;

import org.ado.github.minesync.commons.DateUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Represents a Minecraft world as it is created in the sdcard.
 *
 * @author andoni
 * @since 1.0.0
 */
public class MinecraftWorld {

    private String name;
    private File worldDirectory;
    private List<File> contentList;
    private Date modificationDate;

    public MinecraftWorld(String name, File worldDirectory, List<File> contentList, Date modificationDate) {
        this.name = name;
        this.worldDirectory = worldDirectory;
        this.contentList = contentList;
        this.modificationDate = modificationDate;
    }

    /**
     * Returns the world's name based on the directory name.
     *
     * @return world's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the directory where the world's files are stored.
     *
     * @return directory where the world is located.
     */
    public File getWorldDirectory() {
        return worldDirectory;
    }

    /**
     * Returns the list of files inside of the world's directory.
     *
     * @return list of files.
     */
    public List<File> getContentList() {
        return contentList;
    }

    public long getSize() {
        long totalSize = 0;
        for (File file : contentList) {
            totalSize += getSize(file);
        }
        return totalSize;
    }

    private long getSize(File file) {
        if (!file.isDirectory()) {
            return file.length();
        } else {
            long size = 0;
            for (File file1 : file.listFiles()) {
                size += getSize(file1);
            }
            return size;
        }
    }

    /**
     * Returns the world's modification date.
     *
     * @return modification date.
     */
    public Date getModificationDate() {
        return modificationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinecraftWorld that = (MinecraftWorld) o;

        if (contentList != null ? !contentList.equals(that.contentList) : that.contentList != null) return false;
        if (modificationDate != null ? !modificationDate.equals(that.modificationDate) : that.modificationDate != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (contentList != null ? contentList.hashCode() : 0);
        result = 31 * result + (modificationDate != null ? modificationDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MinecraftWorld{");
        sb.append("name[").append(name).append("]");
        sb.append(" worldDirectory[").append(worldDirectory).append("]");
        sb.append(" contentList[").append(contentList).append("]");
        sb.append(" modificationDate[")
                .append(modificationDate != null ? DateUtils.formatDate(modificationDate) : "-")
                .append("]");
        sb.append('}');
        return sb.toString();
    }
}