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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.commons.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static org.ado.github.minesync.db.TableWorldColumns.*;
import static org.ado.github.minesync.db.WorldEntityFactory.getWorldEntity;

/**
 * Manages the sync status of the local worlds.
 *
 * @author andoni
 * @since 1.0.0
 */
public class MineSyncWorldStatus {

    private static final String TAG = MineSyncWorldStatus.class.getName();

    private MineSyncDbOpenHelper dbHelper;

    public MineSyncWorldStatus(Context context) {
        dbHelper = MineSyncDbOpenHelper.getInstance(context);
    }

    public List<WorldEntity> getWorlds() {
        List<WorldEntity> minecraftWorldList = new ArrayList<WorldEntity>();
        String[] resultColumns = new String[]{KEY_ID, WORLD_NAME_COLUMN, WORLD_MODIFICATION_DATE_COLUMN, WORLD_SIZE_COLUMN};
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            Cursor cursor = database.query(WORLD_TABLE, resultColumns, null, null, null, null, WORLD_NAME_COLUMN);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    minecraftWorldList.add(getWorldEntity(cursor));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot get world from table \"" + WORLD_TABLE + "\".", e);
        }
        return minecraftWorldList;
    }

    public WorldEntity getWorld(String name) {
        ALog.d(TAG, "get world. name [" + name + "]");
        WorldEntity worldEntity = dbHelper.getWorldByName(name);
        if (worldEntity != null) {
            ALog.d(TAG, "world found [" + worldEntity + "]");
        } else {
            ALog.d(TAG, "world [" + name + "] not found in database.");
        }
        return worldEntity;
    }

    public void updateWorld(WorldEntity worldEntity, HistoryActionEnum historyActionEnum) {
        SQLiteDatabase database = null;
        long worldId;
        try {
            database = dbHelper.getWritableDatabase();
            WorldEntity persistedWorld = dbHelper.getWorldByName(worldEntity.getName());
            if (persistedWorld != null) {
                ALog.d(TAG, "update world [" + worldEntity + "]");
                ContentValues cv = new ContentValues();
                cv.put(WORLD_MODIFICATION_DATE_COLUMN, DateUtils.formatSqlLiteDate(worldEntity.getModificationDate()));
                cv.put(WORLD_SIZE_COLUMN, worldEntity.getSize());
                cv.put(WORLD_SYNC_TYPE_COLUMN, worldEntity.getSyncType().getSyncType());
                database.update(WORLD_TABLE, cv, WORLD_NAME_COLUMN + "=?", new String[]{worldEntity.getName()});
                worldId = persistedWorld.getId();
            } else {
                ALog.d(TAG, "insert world [" + worldEntity + "]");
                ContentValues cv = new ContentValues();
                cv.put(WORLD_NAME_COLUMN, worldEntity.getName());
                cv.put(WORLD_MODIFICATION_DATE_COLUMN, DateUtils.formatSqlLiteDate(worldEntity.getModificationDate()));
                cv.put(WORLD_SIZE_COLUMN, worldEntity.getSize());
                cv.put(WORLD_SYNC_TYPE_COLUMN, worldEntity.getSyncType().getSyncType());
                worldId = database.insert(WORLD_TABLE, KEY_ID, cv);
            }
            dbHelper.insertWorldHistory(worldEntity, worldId, historyActionEnum);
        } catch (Exception e) {
            ALog.e(TAG, e, "Cannot update table \"" + WORLD_TABLE + "\".");
        }
    }

    public List<HistoryView> getHistoryAll() {
        List<HistoryView> historyViewList = new ArrayList<HistoryView>();
        List<HistoryEntity> historyAll = dbHelper.getHistoryAll();
        HistoryView historyView;
        for (HistoryEntity historyEntity : historyAll) {
            historyView = new HistoryView();
            historyView.setWorldName("");
            historyView.setHistoryActionEnum(historyEntity.getHistoryActionEnum());
            historyView.setDate(historyEntity.getDate());
            historyViewList.add(historyView);
        }
        return historyViewList;
    }
}