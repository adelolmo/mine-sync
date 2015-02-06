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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.ado.github.minesync.commons.ALog;

import static org.ado.github.minesync.db.GeneralTableColumns.KEY_ID;
import static org.ado.github.minesync.db.TableWorldColumns.*;

/**
 * Database cursor queries for world table.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldCursorQueries {

    private static final String TAG = WorldCursorQueries.class.getName();

    private SQLiteDatabase database;

    public WorldCursorQueries(SQLiteDatabase readableDatabase) {
        database = readableDatabase;
    }

    public Cursor getWorldCursorAll() {
        String[] resultColumns = new String[]{KEY_ID,
                WORLD_NAME_COLUMN,
                WORLD_MODIFICATION_DATE_COLUMN,
                WORLD_SIZE_COLUMN,
                WORLD_SYNC_TYPE_COLUMN};
        ALog.d(TAG, "get worlds all. database path [" + database.getPath() + "] version [" + database.getVersion() + "].");
        return database
                .query(WORLD_TABLE,
                        resultColumns,
                        null,
                        null,
                        null,
                        null,
                        WORLD_NAME_COLUMN.concat(" asc"));
    }
}
