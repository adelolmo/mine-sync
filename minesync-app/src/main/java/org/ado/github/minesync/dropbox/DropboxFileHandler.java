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

package org.ado.github.minesync.dropbox;

import android.content.Context;

import java.io.InputStream;

/**
 * Interface to define management of dropbox files.
 *
 * @author andoni
 * @since 1.2.0
 */
public interface DropboxFileHandler {

    /**
     * Returns whether or not the given <code>dbxFile</code> can be handled.
     *
     * @param fileInfo
     * @return <code>true</code> if the dropbox file can be handle, <code>false</code> otherwise.
     */
    boolean canHandle(FileInfo fileInfo);

    /**
     * Handles the given <code>dbxFile</code>.
     *
     * @param filename    the filename.
     * @param inputStream the file content.
     * @param context     the context.
     */
    void handle(String filename, InputStream inputStream, Context context);
}
