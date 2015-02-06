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

package org.ado.github.minesync;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import org.ado.github.minesync.commons.DateUtils;
import org.ado.github.minesync.db.*;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;

import java.io.File;
import java.util.Date;
import java.util.List;

@Ignore
public class MineSyncDbOpenHelperTest extends AndroidTestCase {

    private static final Date TEST_DATE = DateUtils.parse("06.05.1981 00:40:01");
    private static final WorldEntity WORLD_ENTITY =
            new WorldEntity(1, "world", TEST_DATE, 22, SyncTypeEnum.AUTO);

    private RenamingDelegatingContext context;
    private SQLiteDatabase database;

    private MineSyncDbOpenHelper unitUnderTest;

    public void setUp() throws Exception {
        context = new RenamingDelegatingContext(getContext(), "test_");
        database = SQLiteDatabase.openOrCreateDatabase(getDatabasePath().getPath(), null);
        unitUnderTest = MineSyncDbOpenHelper.getInstance(context);
    }

    @Override
    protected void tearDown() throws Exception {
        deleteDatabase();
    }

    public void testUpgrade_v1_to_v2() {
        unitUnderTest.onUpgrade(database, 1, 2);
    }

    public void testGetHistoryAll_empty() throws Exception {
        assertTrue("no history records", unitUnderTest.getHistoryAll().isEmpty());
    }

    public void testGetHistoryAll_oneRecord() throws Exception {
        unitUnderTest.insertWorldHistory(WORLD_ENTITY, 1, HistoryActionEnum.DOWNLOAD);

        List<HistoryEntity> historyAll = unitUnderTest.getHistoryAll();

        assertEquals("1 record found", 1, historyAll.size());
        assertEquals("world found", WORLD_ENTITY.getId(), historyAll.get(0).getWorldId());
        assertEquals("action", HistoryActionEnum.DOWNLOAD, historyAll.get(0).getHistoryActionEnum());
//        assertTrue("date", testBegging.before(historyAll.get(0).getDate()));
    }

    private void deleteDatabase() {
        FileUtils.deleteQuietly(getDatabasePath());
        FileUtils.deleteQuietly(getContext().getDatabasePath("test_minesync.db-journal"));
    }

    private File getDatabasePath() {
        return getContext().getDatabasePath("test_minesync.db");
    }
}