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

package org.ado.minesync.db.upgrade;

import android.database.sqlite.SQLiteDatabase;
import org.ado.minesync.commons.ALog;

import static org.ado.minesync.db.GeneralTableColumns.KEY_ID;
import static org.ado.minesync.db.TableWorldColumns.*;

/**
 * Manages the upgrade of the database.
 *
 * @author andoni
 * @since 1.2.0
 */
public class DatabaseUpgradeManager {

    public static final String CREATE_WORLD_TABLE = "create table " + WORLD_TABLE
            + " (" + KEY_ID + " integer primary key autoincrement, "
            + WORLD_NAME_COLUMN + " text UNIQUE not null, "
            + WORLD_MODIFICATION_DATE_COLUMN + " timestamp not null, "
            + WORLD_SIZE_COLUMN + " long, "
            + WORLD_SYNC_TYPE_COLUMN + " int);";
    private static final String TAG = DatabaseUpgradeManager.class.getName();
    private DatabaseVersion[] databaseVersionArray;
    private DatabaseVersion databaseVersion1To2;

    public DatabaseUpgradeManager() {
        databaseVersion1To2 = new Upgrade1To2();
        databaseVersionArray = new DatabaseVersion[]{databaseVersion1To2};
    }

    public void create(SQLiteDatabase db) {
        ALog.d(TAG, "onCreate. database [" + db.getPath() + "].");
        db.execSQL(CREATE_WORLD_TABLE);
        for (DatabaseVersion databaseVersion : databaseVersionArray) {
            databaseVersion.create(db);
        }
    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ALog.d(TAG, "onUpgrade. oldVersion [" + oldVersion + "] newVersion [" + newVersion + "].");
        if (oldVersion == 1 && newVersion == 2) {
            databaseVersion1To2.upgrade(db);
        }
    }
}
