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

package org.ado.github.minesync.commons;

import android.util.Log;

import static org.ado.github.minesync.config.AppConstants.L;

/**
 * Wrapper for the default Android log system.
 *
 * @author andoni
 * @since 1.2.0
 */
public class ALog {

    public static void i(String tag, String msg) {
        if (L) Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Object... args) {
        if (L) Log.i(tag, String.format(msg, args));
    }

    public static void d(String tag, String msg) {
        if (L) Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Object... args) {
        if (L) Log.d(tag, String.format(msg, args));
    }

    public static void v(String tag, String msg) {
        if (L) Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Object... args) {
        if (L) Log.v(tag, String.format(msg, args));
    }

    public static void w(String tag, String msg) {
        if (L) Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Object... args) {
        if (L) Log.w(tag, String.format(msg, args));
    }

    public static void w(String tag, Exception e, String msg, Object... args) {
        if (L) Log.w(tag, String.format(msg, args), e);
    }

    public static void e(String tag, Exception e, String msg) {
        Log.e(tag, msg, e);
    }

    public static void e(String tag, Exception e, String msg, Object... args) {
        Log.e(tag, String.format(msg, args), e);
    }
}
