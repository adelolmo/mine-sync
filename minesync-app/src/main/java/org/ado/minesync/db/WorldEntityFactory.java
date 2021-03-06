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

import android.database.Cursor;
import org.ado.minesync.commons.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static org.ado.minesync.db.GeneralTableColumns.KEY_ID_INDEX;
import static org.ado.minesync.db.TableWorldColumns.*;

/**
 * Class description here.
 *
 * @author andoni
 * @since 29.07.2014
 */
public class WorldEntityFactory {

    public static WorldEntity getWorldEntity(Cursor worldCursor) {
        return new WorldEntity(
                worldCursor.getLong(KEY_ID_INDEX),
                worldCursor.getString(WORLD_NAME_COLUMN_INDEX),
                DateUtils.parseSqlLiteDate(worldCursor.getString(WORLD_MODIFICATION_DATE_COLUMN_INDEX)),
                worldCursor.getLong(WORLD_SIZE_COLUMN_INDEX),
                SyncTypeEnum.find(worldCursor.getInt(WORLD_SYNC_TYPE_COLUMN_INDEX)));
    }

    public static List<WorldEntity> getWorldEntityList(Cursor cursor) {
        List<WorldEntity> worldEntityList = new ArrayList<WorldEntity>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                worldEntityList.add(getWorldEntity(cursor));
            }
        }
        return worldEntityList;
    }
}
