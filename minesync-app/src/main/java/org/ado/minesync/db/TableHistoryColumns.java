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

/**
 * Column names and indexes for the database table <code>History</code>.
 *
 * @author andoni
 * @since 1.2.0
 */
public class TableHistoryColumns extends GeneralTableColumns {

    public static final String HISTORY_TABLE = "history";

    public static final String HISTORY_WORLD_ID = "WORLD_ID_COLUMN";
    public static final int HISTORY_WORLD_ID_INDEX = 1;
    public static final String HISTORY_DATE = "DATE_COLUMN";
    public static final int HISTORY_DATE_INDEX = 2;
    public static final String HISTORY_ACTION = "ACTION_COLUMN";
    public static final int HISTORY_ACTION_INDEX = 3;
    public static final String HISTORY_SIZE= "SIZE";
    public static final int HISTORY_SIZE_INDEX = 4;

}