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
 * Class description here.
 *
 * @author andoni
 * @since 30.06.2014
 */
public class HistoryView {

    private String worldName;
    private HistoryActionEnum historyActionEnum;
    private Date date;
    private long size;

    public HistoryView() {
    }

    public HistoryView(String worldName, HistoryActionEnum historyActionEnum, Date date, long size) {
        this();
        this.worldName = worldName;
        this.historyActionEnum = historyActionEnum;
        this.date = date;
        this.size = size;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public HistoryActionEnum getHistoryActionEnum() {
        return historyActionEnum;
    }

    public void setHistoryActionEnum(HistoryActionEnum historyActionEnum) {
        this.historyActionEnum = historyActionEnum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryView that = (HistoryView) o;

        if (size != that.size) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (historyActionEnum != that.historyActionEnum) return false;
        if (worldName != null ? !worldName.equals(that.worldName) : that.worldName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = worldName != null ? worldName.hashCode() : 0;
        result = 31 * result + (historyActionEnum != null ? historyActionEnum.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HistoryView{");
        sb.append("worldName='").append(worldName).append('\'');
        sb.append(", historyActionEnum=").append(historyActionEnum);
        sb.append(", date=").append(date);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }
}
