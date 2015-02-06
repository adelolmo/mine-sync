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

/**
 * Enum for the world's synchronization types.
 *
 * @author andoni
 * @since 1.2.0
 */
public enum SyncTypeEnum {

    MANUAL(0),
    AUTO(1);

    private int syncType;

    private SyncTypeEnum(int syncType) {
        this.syncType = syncType;
    }

    public int getSyncType() {
        return syncType;
    }

    public static SyncTypeEnum find(int syncType) {
        for (SyncTypeEnum syncTypeEnum : SyncTypeEnum.values()) {
            if (syncTypeEnum.getSyncType() == syncType) {
                return syncTypeEnum;
            }
        }
        throw new IllegalArgumentException("No sync type found for syncType \"" + syncType + "\".");
    }
}
