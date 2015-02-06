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

import java.util.Date;

/**
 * Database History entity.
 *
 * @author andoni
 * @since 1.2.0
 */
public class HistoryEntity extends GeneralEntity {

    private long worldId;
    private Date date;
    private HistoryActionEnum historyActionEnum;
    private long size;

    public HistoryEntity(long worldId, Date date, HistoryActionEnum historyActionEnum) {
        this(-1, worldId, date, historyActionEnum, 0);
    }

    public HistoryEntity(long id, long worldId, Date date, HistoryActionEnum historyActionEnum, long size) {
        super(id);
        this.worldId = worldId;
        this.date = date;
        this.historyActionEnum = historyActionEnum;
    }

    public long getWorldId() {
        return worldId;
    }

    public Date getDate() {
        return date;
    }

    public HistoryActionEnum getHistoryActionEnum() {
        return historyActionEnum;
    }

    public long getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryEntity that = (HistoryEntity) o;

        if (size != that.size) return false;
        if (worldId != that.worldId) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (historyActionEnum != that.historyActionEnum) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (worldId ^ (worldId >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (historyActionEnum != null ? historyActionEnum.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HistoryEntity{");
        sb.append("worldId=").append(worldId);
        sb.append(", date=").append(date);
        sb.append(", historyActionEnum=").append(historyActionEnum);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}