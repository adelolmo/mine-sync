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

package org.ado.minesync.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.commons.DateUtils;
import org.ado.minesync.db.upgrade.DatabaseUpgradeManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ado.minesync.db.GeneralTableColumns.KEY_ID;
import static org.ado.minesync.db.TableHistoryColumns.*;
import static org.ado.minesync.db.TableHistoryColumns.KEY_ID_INDEX;
import static org.ado.minesync.db.TableWorldColumns.*;

/**
 * Class to provide access to db.
 *
 * @author andoni
 * @since 1.0.0
 */
public class MineSyncDbOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "minesync.db";
    private static final String TAG = MineSyncDbOpenHelper.class.getName();
    private static final int DATABASE_VERSION = 2;
    private static MineSyncDbOpenHelper instance = null;
    private DatabaseUpgradeManager databaseUpgradeManager;
    private WorldCursorQueries worldCursorQueries;

    private MineSyncDbOpenHelper(Context context) {
        this(context, DATABASE_VERSION);
    }

    private MineSyncDbOpenHelper(Context context, int databaseVersion) {
        super(context, DATABASE_NAME, null, databaseVersion);
        databaseUpgradeManager = new DatabaseUpgradeManager();
        worldCursorQueries = new WorldCursorQueries(getReadableDatabase());
    }

    public static MineSyncDbOpenHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new MineSyncDbOpenHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        databaseUpgradeManager.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        databaseUpgradeManager.upgrade(db, oldVersion, newVersion);
    }

    public WorldEntity save(WorldEntity worldEntity) {
        ALog.d(TAG, "insert world [%s]", worldEntity);
        ContentValues cv = new ContentValues();
        cv.put(WORLD_NAME_COLUMN, worldEntity.getName());
        cv.put(WORLD_MODIFICATION_DATE_COLUMN, DateUtils.formatSqlLiteDate(worldEntity.getModificationDate()));
        cv.put(WORLD_SIZE_COLUMN, worldEntity.getSize());
        cv.put(WORLD_SYNC_TYPE_COLUMN, SyncTypeEnum.AUTO.getSyncType());
        final long id = getWritableDatabase().insert(WORLD_TABLE, KEY_ID, cv);
        return new WorldEntity(id, worldEntity.getName(), worldEntity.getModificationDate(), worldEntity.getSize(), worldEntity.getSyncType());
    }

    public WorldEntity getWorldByName(String worldName) {
        WorldEntity worldEntity = null;
        Cursor worldCursor = getWorldCursorByName(worldName);
        if (worldCursor != null && worldCursor.moveToNext()) {
            worldEntity = WorldEntityFactory.getWorldEntity(worldCursor);
        }
        return worldEntity;
    }

    public void updateWorldSyncType(String worldName, SyncTypeEnum syncType) {
        ALog.d(TAG, "update world's [%s] syncType [%s].", worldName, syncType);
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(WORLD_SYNC_TYPE_COLUMN, syncType.getSyncType());
        database.update(WORLD_TABLE, cv, WORLD_NAME_COLUMN + "=?", new String[]{worldName});
    }

    public void insertWorldHistory(WorldEntity worldEntity, long worldId, HistoryActionEnum historyActionEnum) {
        ALog.d(TAG, "insert history [%s]", worldEntity);
        ContentValues cv = new ContentValues();
        cv.put(HISTORY_WORLD_ID, worldId);
        cv.put(HISTORY_DATE, DateUtils.formatSqlLiteDate(new Date()));
        cv.put(HISTORY_ACTION, historyActionEnum.getActionId());
        cv.put(HISTORY_SIZE, worldEntity.getSize());
        getWritableDatabase().insert(HISTORY_TABLE, KEY_ID, cv);
    }

    public List<WorldEntity> getWorldAll() {
        ArrayList<WorldEntity> worldEntities = new ArrayList<WorldEntity>();
        Cursor cursor = getWorldCursorAll();
        while (cursor != null && cursor.moveToNext()) {
            worldEntities.add(WorldEntityFactory.getWorldEntity(cursor));
        }
        return worldEntities;
    }

    public List<HistoryEntity> getHistoryAll() {
        List<HistoryEntity> historyEntities = new ArrayList<HistoryEntity>();
        Cursor cursor = getHistoryCursorAll();
        while (cursor != null && cursor.moveToNext()) {
            historyEntities.add(getHistoryEntity(cursor));
        }
        return historyEntities;
    }

    public List<HistoryView> getHistoryViewAll(int limit) {
        final List<HistoryView> historyViewList = new ArrayList<HistoryView>();
        final Cursor cursor = getHistoryViewCursorAll(limit);
        while (cursor != null && cursor.moveToNext()) {
            historyViewList.add(getHistoryView(cursor));
        }
        return historyViewList;
    }

    public Cursor getWorldCursorByName(String worldName) {
        String[] resultColumns = new String[]{KEY_ID,
                WORLD_NAME_COLUMN,
                WORLD_MODIFICATION_DATE_COLUMN,
                WORLD_SIZE_COLUMN,
                WORLD_SYNC_TYPE_COLUMN};
        SQLiteDatabase database = getReadableDatabase();
        ALog.d(TAG, "get world all. database path [%s] version [%d].", database.getPath(), database.getVersion());
        return database
                .query(WORLD_TABLE,
                        resultColumns,
                        WORLD_NAME_COLUMN + "=?",
                        new String[]{worldName},
                        null,
                        null,
                        WORLD_NAME_COLUMN);
    }

    public Cursor getWorldCursorAll() {
        return worldCursorQueries.getWorldCursorAll();
    }

    public Cursor getHistoryCursorAll() {
        String[] resultColumns = new String[]{KEY_ID, HISTORY_WORLD_ID, HISTORY_DATE, HISTORY_ACTION, HISTORY_SIZE};
        SQLiteDatabase database = getReadableDatabase();
        ALog.d(TAG, "get history all. database path [%s] version [%d].", database.getPath(), database.getVersion());
        return database
                .query(HISTORY_TABLE,
                        resultColumns,
                        null,
                        null,
                        null,
                        null,
                        KEY_ID);
    }

    public Cursor getHistoryViewCursorAll() {
        return getHistoryViewCursorAll(-1);
    }

    public Cursor getHistoryViewCursorAll(int limit) {
        StringBuilder query = new StringBuilder("select ");
        query.append(HISTORY_TABLE).append(".").append(TableWorldColumns.KEY_ID).append(","); // 0
        query.append(HISTORY_WORLD_ID).append(",");                                           // 1
        query.append(WORLD_NAME_COLUMN).append(",");                                          // 2
        query.append(HISTORY_DATE).append(",");                                               // 3
        query.append(HISTORY_ACTION).append(",");                                             // 4
        query.append(HISTORY_SIZE);                                                           // 5
        query.append(" from ").append(HISTORY_TABLE).append(" inner join ").append(WORLD_TABLE)
                .append(" on ").append(HISTORY_WORLD_ID).append("=").append(WORLD_TABLE).append(".").append(TableWorldColumns.KEY_ID);
        query.append(" order by ").append(HISTORY_DATE).append(" desc");
        if (limit > 0) {
            query.append(" limit ").append(limit);
        }
        return getReadableDatabase().rawQuery(query.toString(), null);
    }

    private HistoryEntity getHistoryEntity(Cursor historyCursor) {
        return new HistoryEntity(
                historyCursor.getLong(KEY_ID_INDEX),
                historyCursor.getLong(HISTORY_WORLD_ID_INDEX),
                DateUtils.parseSqlLiteDate(historyCursor.getString(HISTORY_DATE_INDEX)),
                HistoryActionEnum.find(historyCursor.getInt(HISTORY_ACTION_INDEX)),
                historyCursor.getLong(HISTORY_SIZE_INDEX));
    }

    private HistoryView getHistoryView(Cursor historyViewCursor) {
        return new HistoryView(historyViewCursor.getString(2),
                HistoryActionEnum.find(historyViewCursor.getInt(4)),
                DateUtils.parseSqlLiteDate(historyViewCursor.getString(3)),
                historyViewCursor.getLong(5));
    }
}