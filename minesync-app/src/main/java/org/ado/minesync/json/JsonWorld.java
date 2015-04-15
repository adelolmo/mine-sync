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

package org.ado.minesync.json;

import org.ado.minesync.db.SyncTypeEnum;

/**
 * Represents a world once parse from json.
 *
 * @author andoni
 * @since 1.2.0
 */
public class JsonWorld {

    private String name;
    private SyncTypeEnum syncType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SyncTypeEnum getSyncType() {
        return syncType;
    }

    public void setSyncType(SyncTypeEnum syncType) {
        this.syncType = syncType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonWorld jsonWorld = (JsonWorld) o;

        if (name != null ? !name.equals(jsonWorld.name) : jsonWorld.name != null) return false;
        if (syncType != jsonWorld.syncType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (syncType != null ? syncType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JsonWorld{");
        sb.append("name='").append(name).append('\'');
        sb.append(", syncType=").append(syncType);
        sb.append('}');
        return sb.toString();
    }
}
