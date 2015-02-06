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

package org.ado.github.minesync.db;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import static org.ado.github.minesync.minecraft.MinecraftUtils.getWorldName;

/**
 * World entity in database.
 *
 * @author andoni
 * @since 1.1.0
 */
public class WorldEntity extends GeneralEntity implements Serializable {

    private String name;
    private Date modificationDate;
    private long size;
    private SyncTypeEnum syncType;

    public WorldEntity(File worldFile) {
        this(getWorldName(worldFile.getName()), new Date(worldFile.lastModified()), worldFile.length());
    }

    public WorldEntity(String name, Date modificationDate, long size) {
        this(-1, name, modificationDate, size, SyncTypeEnum.AUTO);
    }

    public WorldEntity(String name, Date modificationDate, long size, SyncTypeEnum syncTypeEnum) {
        this(-1, name, modificationDate, size, syncTypeEnum);
    }

    public WorldEntity(long id, String name, Date modificationDate, long size, SyncTypeEnum syncTypeEnum) {
        super(id);
        this.name = name;
        this.modificationDate = modificationDate;
        this.size = size;
        syncType = syncTypeEnum;
    }

    public String getName() {
        return name;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public long getSize() {
        return size;
    }

    public void setSyncType(SyncTypeEnum syncType) {
        this.syncType = syncType;
    }

    public SyncTypeEnum getSyncType() {
        return syncType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldEntity that = (WorldEntity) o;

        if (size != that.size) return false;
        if (modificationDate != null ? !modificationDate.equals(that.modificationDate) : that.modificationDate != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (syncType != that.syncType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (modificationDate != null ? modificationDate.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (syncType != null ? syncType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("WorldEntity{");
        sb.append("name='").append(name).append('\'');
        sb.append(", modificationDate=").append(modificationDate);
        sb.append(", size=").append(size);
        sb.append(", syncType=").append(syncType);
        sb.append('}');
        return sb.toString();
    }
}