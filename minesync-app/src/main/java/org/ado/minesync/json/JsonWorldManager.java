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

package org.ado.minesync.json;

import android.content.Context;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.config.AppConfiguration;
import org.ado.minesync.db.MineSyncDbOpenHelper;
import org.ado.minesync.db.WorldEntity;
import org.ado.minesync.dropbox.DropboxFileManager;
import org.ado.minesync.dropbox.DropboxOperationEnum;
import org.ado.minesync.dropbox.DropboxOperationListener;
import org.ado.minesync.exception.DropboxAccountException;
import org.ado.minesync.exception.DropboxException;
import org.ado.minesync.exception.MineSyncException;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ado.minesync.config.AppConstants.WORLDS_JSON_FILENAME;

/**
 * Manages the read and update actions of the json world list file.
 *
 * @author andoni
 * @since 1.2.0
 */
public class JsonWorldManager {

    private static final String TAG = JsonWorldManager.class.getName();

    private DropboxFileManager dropboxFileManager;
    private JsonWorldsAdapter jsonWorldsAdapter;
    private MineSyncDbOpenHelper dbHelper;

    public JsonWorldManager(Context context) {
        dropboxFileManager =
                new DropboxFileManager(AppConfiguration.getDropboxAccountManager(context), context.getCacheDir());
        dbHelper = MineSyncDbOpenHelper.getInstance(context);
        jsonWorldsAdapter = new JsonWorldsAdapter();
    }

    public List<JsonWorld> getJsonWorlds() throws MineSyncException {
        final List<JsonWorld> jsonWorldList = new ArrayList<JsonWorld>();
        try {
            dropboxFileManager.downloadFile(WORLDS_JSON_FILENAME, new DropboxOperationListener() {
                @Override
                public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) throws MineSyncException {
                    jsonWorldList.addAll(jsonWorldsAdapter.getWorldList(file));

                }
            });
        } catch (DropboxAccountException e) {
            return Collections.emptyList();
        }
        return jsonWorldList;
    }

    public void updateJsonWorldsFile() {
        try {
            dropboxFileManager.downloadFile(WORLDS_JSON_FILENAME, new DropboxOperationListener() {
                @Override
                public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) throws MineSyncException {
                    updateDatabaseWorlds(file);

                }
            });
        } catch (DropboxAccountException e) {
            e.printStackTrace();
        } catch (MineSyncException e) {
            e.printStackTrace();
        }
    }

    public void updateJsonWorldsFile(List<WorldEntity> worldEntityList) {
        try {
            JSONObject worldListToJson = jsonWorldsAdapter.getWorldListToJson(worldEntityList);
            File tempFile = new File(FileUtils.getTempDirectory(), WORLDS_JSON_FILENAME);
            FileUtils.writeStringToFile(tempFile, worldListToJson.toJSONString());
            dropboxFileManager.uploadFile(tempFile, new DropboxOperationListener() {
                @Override
                public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) throws MineSyncException {
                    FileUtils.deleteQuietly(file);
                }
            });
        } catch (DropboxException e) {
            ALog.e(TAG, e, "Unable to upload json world list");
        } catch (IOException e) {
            ALog.e(TAG, e, "Unable to create temporary json world list file");
        }
    }

    public void updateDatabaseWorlds(File jsonFile) {
        for (JsonWorld jsonWorld : jsonWorldsAdapter.getWorldList(jsonFile)) {
            dbHelper.updateWorldSyncType(jsonWorld.getName(), jsonWorld.getSyncType());
        }
    }
}