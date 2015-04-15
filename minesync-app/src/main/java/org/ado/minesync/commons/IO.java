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

import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * Class with useful IO methods.
 *
 * @author andoni
 * @since 1.2.0
 */
public class IO {

    private static final String TAG = IO.class.getName();
    private static final int BUFFER = 2048;

    public static File getFile(String filename, InputStream inputStream) throws IOException {
        return getFile(filename, inputStream, FileUtils.getTempDirectory());
    }

    public static File getFile(String filename, InputStream inputStream, File tempDirectory) throws IOException {
        OutputStream outputStream = null;
        File file;
        try {
            file = new File(tempDirectory, filename);
            if (file.exists()) {
                if (!file.delete()) {
                    Log.w(TAG, "Unable to delete file [" + file.getName() + "]");
                }
            }
            outputStream = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[BUFFER];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return file;

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }
    }
}
