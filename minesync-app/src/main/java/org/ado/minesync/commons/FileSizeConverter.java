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

/*
 *	This file is part of Transdroid <http://www.transdroid.org>
 *	
 *	Transdroid is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	Transdroid is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU General Public License
 *	along with Transdroid.  If not, see <http://www.gnu.org/licenses/>.
 *	
 */
package org.ado.minesync.commons;

/**
 * Quick and dirty file size formatter.
 *
 * @author erickok
 */
public class FileSizeConverter {

    private static final String DECIMAL_FORMATTER = "%.1f";

    /**
     * A quantity in which to express a file size.
     *
     * @author erickok
     */
    public enum SizeUnit {
        B,
        KB,
        MB,
        GB
    }

    private static int INC_SIZE = 1024;

    // Returns a file size given in bytes to a different unit, as a formatted string
    public static String getSize(long from, SizeUnit to) {
        String out;
        switch (to) {
            case B:
                out = String.valueOf(from);
                break;
            case KB:
                out = String.format(DECIMAL_FORMATTER, ((double) from) / 1024);
                break;
            case MB:
                out = String.format(DECIMAL_FORMATTER, ((double) from) / 1024 / 1024);
                break;
            default:
                out = String.format(DECIMAL_FORMATTER, ((double) from) / 1024 / 1024 / 1024);
                break;
        }

        return (out + " " + to.toString());
    }

    // Returns a file size in bytes in a nice readable formatted string
    public static String getSize(long from) {
        return getSize(from, true);
    }

    // Returns a file size in bytes in a nice readable formatted string
    public static String getSize(long from, boolean withUnit) {
        if (from < INC_SIZE) {
            return String.valueOf(from) + (withUnit ? SizeUnit.B.toString() : "");
        } else if (from < (INC_SIZE * INC_SIZE)) {
            return String.format(DECIMAL_FORMATTER, ((double) from) / INC_SIZE) + (withUnit ? SizeUnit.KB.toString() : "");
        } else if (from < (INC_SIZE * INC_SIZE * INC_SIZE)) {
            return String.format(DECIMAL_FORMATTER, ((double) from) / INC_SIZE / INC_SIZE) + (withUnit ? SizeUnit.MB.toString() : "");
        } else {
            return String.format(DECIMAL_FORMATTER, ((double) from) / INC_SIZE / INC_SIZE / INC_SIZE) + (withUnit ? SizeUnit.GB.toString() : "");
        }
    }
}