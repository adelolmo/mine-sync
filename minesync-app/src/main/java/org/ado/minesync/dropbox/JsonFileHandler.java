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

package org.ado.minesync.dropbox;

import android.content.Context;
import android.content.Intent;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.commons.IO;
import org.ado.minesync.gui.receiver.JsonUpdateReceiver;
import org.ado.minesync.json.JsonWorldManager;

import java.io.IOException;
import java.io.InputStream;

import static org.ado.minesync.config.AppConstants.WORLDS_JSON_FILENAME;

/**
 * Handles the world's list json file.
 *
 * @author andoni
 * @since 1.2.0
 */
public class JsonFileHandler extends AbstractDropboxFileHandler {

    private static final String TAG = JsonFileHandler.class.getName();

    @Override
    public boolean canHandle(FileInfo fileInfo) {
        return WORLDS_JSON_FILENAME.equals(fileInfo.getPath());
    }

    @Override
    public void handle(String filename, InputStream inputStream, Context context) {
        try {
            JsonWorldManager jsonWorldManager = new JsonWorldManager(context);
            jsonWorldManager.updateDatabaseWorlds(IO.getFile(filename, inputStream));

            Intent intent = new Intent(JsonUpdateReceiver.JSON_UPDATE);
            context.sendBroadcast(intent);

        } catch (IOException e) {
            ALog.e(TAG, e, "Unable to read file content \"" + filename + "\".");
        }
    }
}