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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.db.WorldCursorQueries;
import org.ado.minesync.db.WorldEntity;

import java.util.List;

import static org.ado.minesync.commons.DateUtils.SQLITE_DATE_FORMAT;
import static org.ado.minesync.commons.DateUtils.formatDate;
import static org.ado.minesync.db.GeneralTableColumns.KEY_ID;
import static org.ado.minesync.db.TableHistoryColumns.*;
import static org.ado.minesync.db.TableWorldColumns.*;
import static org.ado.minesync.db.WorldEntityFactory.getWorldEntityList;

/**
 * Class description here.
 *
 * @author andoni
 * @since 1.2.0
 */
public class Upgrade1To2 implements DatabaseVersion {

    public static final String CREATE_HISTORY_TABLE = "create table " + HISTORY_TABLE
            + " (" + KEY_ID + " integer primary key autoincrement, "
            + HISTORY_WORLD_ID + " integer, "
            + HISTORY_DATE + " timestamp not null, "
            + HISTORY_ACTION + " integer, "
            + HISTORY_SIZE + " long, "
            + "FOREIGN KEY(" + HISTORY_WORLD_ID + ") REFERENCES " + WORLD_TABLE + "(" + KEY_ID + ")"
            + ");";
    private static final String TAG = Upgrade1To2.class.getName();
    private static final String DROP_WORLD_TABLE = "drop table if exists " + WORLD_TABLE + ";";

    @Override
    public void create(SQLiteDatabase database) {
        ALog.i(TAG, "create");
        database.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void upgrade(SQLiteDatabase database) {
        ALog.i(TAG, "upgrade");
        List<WorldEntity> worldEntityList = getWorldEntityList(getCurrentWorlds(database));
        upgradeTables(database);
        restoreWorlds(database, worldEntityList);
    }

    private Cursor getCurrentWorlds(SQLiteDatabase database) {
        WorldCursorQueries worldCursorQueries = new WorldCursorQueries(database);
        return worldCursorQueries.getWorldCursorAll();
    }

    private void upgradeTables(SQLiteDatabase database) {
        ALog.d(TAG, "drop world table");
        database.execSQL(DROP_WORLD_TABLE);
        ALog.d(TAG, "create world table");
        database.execSQL(DatabaseUpgradeManager.CREATE_WORLD_TABLE);
        ALog.d(TAG, "create history table");
        database.execSQL(CREATE_HISTORY_TABLE);
    }

    private void restoreWorlds(SQLiteDatabase database, List<WorldEntity> worldCursorAll) {
        for (WorldEntity worldEntity : worldCursorAll) {
            ALog.i(TAG, "restoring world [" + worldEntity + "]");
            insertWorld(database, worldEntity);
        }
    }

    private void insertWorld(SQLiteDatabase database, WorldEntity worldEntity) {
        ALog.d(TAG, "insert world [" + worldEntity + "]");
        ContentValues cv = new ContentValues();
        cv.put(WORLD_NAME_COLUMN, worldEntity.getName());
        cv.put(WORLD_MODIFICATION_DATE_COLUMN, formatDate(worldEntity.getModificationDate(), SQLITE_DATE_FORMAT));
        cv.put(WORLD_SIZE_COLUMN, worldEntity.getSize());
        database.insert(WORLD_TABLE, KEY_ID, cv);
    }
}