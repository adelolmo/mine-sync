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

package org.ado.minesync.commons;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class description here.
 *
 * @author andoni
 * @since 10.01.2014
 */
public class DateUtils {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final SimpleDateFormat SQLITE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date) {
        return formatDate(date, SIMPLE_DATE_FORMAT);
    }

    public static String formatSqlLiteDate(Date date) {
        return formatDate(date, SQLITE_DATE_FORMAT);
    }

    public static String formatDate(Date date, DateFormat dateFormat) {
        return dateFormat.format(date);
    }

    public static Date parse(String stringDate) {
        return parse(stringDate, SIMPLE_DATE_FORMAT);
    }

    public static Date parseSqlLiteDate(String stringDate) {
        return parse(stringDate, SQLITE_DATE_FORMAT);
    }

    public static Date parse(String stringDate, SimpleDateFormat dateFormat) {
        try {
            return dateFormat.parse(stringDate);
        } catch (ParseException e) {
            throw new IllegalStateException("Cannot parse date string \"" + stringDate
                    + "\" with format \"" + dateFormat.getDateFormatSymbols() + "\".", e);
        }
    }
}
