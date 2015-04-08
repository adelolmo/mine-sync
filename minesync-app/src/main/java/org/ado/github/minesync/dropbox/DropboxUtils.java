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

import com.dropbox.sync.android.*;
import org.ado.github.minesync.commons.ALog;
import org.ado.github.minesync.db.SyncTypeEnum;
import org.ado.github.minesync.db.WorldEntity;

import java.io.File;

/**
 * Class with convenient methods for Dropbox.
 *
 * @author andoni
 * @since 1.0.0
 */
public class DropboxUtils {

    private static final String NO_SYNC_DIRECTORY = "no_sync";
    public static final DbxPath NO_SYNC_DROPBOX_PATH = new DbxPath(NO_SYNC_DIRECTORY);
    private static final String ZIP_EXTENSION = ".zip";

    public static DbxFile createFile(String tag, DbxFileSystem dbxFileSystem, DbxPath dbxPath) throws DbxException {
        ALog.v(tag, "create file [" + dbxPath.getName() + "]");
        return dbxFileSystem.create(dbxPath);
    }

    /**
     * Opens the remote file with the same name as the given <code>file</code>, if the remote file it doesn't exist it will be created.
     *
     * @param dbxFileSystem the dropbox file system
     * @param file          the local file
     * @return the dropbox file
     * @throws DbxException
     * @since 1.2.0
     */
    public static DbxFile createOrOpenFile(DbxFileSystem dbxFileSystem, File file) throws DbxException {
        try {
            return dbxFileSystem.create(new DbxPath(file.getName()));
        } catch (DbxException e) {
            return dbxFileSystem.open(new DbxPath(file.getName()));
        }
    }

    /**
     * Returns the <code>DbxFile</code> opened or created.
     *
     * @param dbxFileSystem the dropbox file system
     * @param file          the local file
     * @return the dropbox file
     * @throws DbxException
     * @since 1.2.0
     */
    public static DbxFile createOrOpenNoSyncFile(DbxFileSystem dbxFileSystem, File file) throws DbxException {
        try {
            return createNoSyncFile(dbxFileSystem, file);
        } catch (DbxException e) {
            return openNoSyncFile(dbxFileSystem, file);
        }
    }

    /**
     * Returns the <code>DbxFile</code> created if possible.
     *
     * @param dbxFileSystem
     * @param file
     * @return
     * @since 1.2.0
     */
    public static DbxFile createNoSyncFile(DbxFileSystem dbxFileSystem, File file) throws DbxException {
        return dbxFileSystem.create(NO_SYNC_DROPBOX_PATH.getChild(file.getName()));
    }

    /**
     * @param dbxFileSystem
     * @param file
     * @return
     * @throws DbxException
     * @since 1.2.0
     */
    private static DbxFile openNoSyncFile(DbxFileSystem dbxFileSystem, File file) throws DbxException {
        return dbxFileSystem.open(NO_SYNC_DROPBOX_PATH.getChild(file.getName()));
    }

    public static DbxFile openFile(String tag, DbxFileSystem dbxFileSystem, DbxFileInfo dbxFileInfo) throws DbxException {
        return openFile(tag, dbxFileSystem, dbxFileInfo.path);
    }

    public static DbxFile openFile(String tag, DbxFileSystem dbxFileSystem, DbxPath dbxPath) throws DbxException {
        ALog.v(tag, "open file [" + dbxPath.getName() + "]");
        return dbxFileSystem.open(dbxPath);
    }

    public static boolean isSyncDirectory(DbxFileInfo dbxFileInfo) {
        return DbxPath.ROOT.equals(dbxFileInfo.path);
    }

    /**
     * Returns the <code>DbxPath</code> for the given <code>filename</code> in the root directory.
     *
     * @param filename the filename.
     * @return file's path in dropbox.
     * @since 1.2.0
     */
    public static DbxPath getRootPath(String filename) {
        return DbxPath.ROOT.getChild(filename);
    }

    /**
     * Returns the <code>DbxPath</code> for the given <code>filename</code> in the internal directory "no_sync".
     *
     * @param filename the filename.
     * @return file's path in dropbox.
     * @since 1.2.0
     */
    public static DbxPath getNoSyncPath(String filename) {
        return NO_SYNC_DROPBOX_PATH.getChild(filename);
    }

    public static void closeFile(String tag, DbxFile dbxFile) {
        if (dbxFile != null) {
            ALog.v(tag, "close file [" + dbxFile.getPath().getName() + "]");
            dbxFile.close();

        } else {
            ALog.w(tag, "Unable to close null file.");
        }
    }

    /**
     * Closes the remote dropbox file and does not notify if the <code>dbxFile</code> is <code>null</code>.
     *
     * @param dbxFile the dropbox file.
     * @since 1.2.0
     */
    public static void closeFileQuietly(DbxFile dbxFile) {
        if (dbxFile != null) {
            dbxFile.close();
        }
    }

    /**
     * Returns the dropbox path for the given <code>worldEntity</code>.
     *
     * @param worldEntity the world entity.
     * @return the remote dropbox path of <code>null</code> if unknown worlds sync type.
     * @see org.ado.github.minesync.db.SyncTypeEnum
     * @since 1.2.0
     */
    public static DbxPath getDbxFile(WorldEntity worldEntity) {
        return getWorldDbxFile(worldEntity.getName(), worldEntity.getSyncType());
    }

    public static DbxPath getDbxFile(String worldName, SyncTypeEnum syncType) {
        switch (syncType) {
            case AUTO:
                return getRootPath(worldName);
            case MANUAL:
                return getNoSyncPath(worldName);
        }
        return null;
    }

    public static DbxPath getWorldDbxFile(String worldName, SyncTypeEnum syncType) {
        switch (syncType) {
            case AUTO:
                return getRootPath(worldName + ZIP_EXTENSION);
            case MANUAL:
                return getNoSyncPath(worldName + ZIP_EXTENSION);
        }
        return null;
    }
}
