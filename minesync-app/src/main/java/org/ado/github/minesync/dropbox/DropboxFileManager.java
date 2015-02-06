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
import org.ado.github.minesync.exception.DbxQueueException;
import org.ado.github.minesync.exception.DropboxAccountException;
import org.ado.github.minesync.exception.DropboxException;
import org.ado.github.minesync.exception.MineSyncException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.ado.github.minesync.config.AppConstants.WORLDS_JSON_FILENAME;
import static org.apache.commons.lang.Validate.notEmpty;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 22/11/13
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public class DropboxFileManager {

    private static final String TAG = DropboxFileManager.class.getName();

    private List<String> uploadingFiles;
    private List<String> downloadingFiles;
    private DbxAccountManager dbxAccountManager;

    private File tmpDirectory;

    public DropboxFileManager(DbxAccountManager dbxAccountManager, File tmpDirectory) {
        uploadingFiles = new ArrayList<String>();
        downloadingFiles = new ArrayList<String>();
        this.dbxAccountManager = dbxAccountManager;
        this.tmpDirectory = tmpDirectory;
    }

    public boolean isDropboxEmpty() {
        if (dbxAccountManager.hasLinkedAccount()) {
            try {
                DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
                return dbxFileSystem.listFolder(DbxPath.ROOT).isEmpty();

//            } catch (DbxException.Unauthorized unauthorized) {
//                ALog.w(TAG, "Unable to connect to Dropbox. " + unauthorized.getMessage());
            } catch (DbxException e) {
                ALog.e(TAG, e, "Cannot retrieve list of files from Dropbox.");
            } catch (DropboxAccountException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public int getDropboxNumberOfFiles() throws DbxException, DropboxAccountException {
        DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();

        // TODO filter folders
        return dbxFileSystem.listFolder(DbxPath.ROOT).size();
    }

    public List<FileInfo> getFolderInfo(DbxPath dropboxFolder) throws DropboxAccountException {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFileSystem.awaitFirstSync();

            List<DbxFileInfo> dbxFileInfoList = dbxFileSystem.listFolder(dropboxFolder);
            ALog.d(TAG, String.format("Files available in Dropbox folder [%s]: [%s]", dropboxFolder, dbxFileInfoList));
            for (DbxFileInfo dbxFileInfo : dbxFileInfoList) {
                if (isWorldFile(dbxFileInfo)) {
                    fileInfoList.add(new FileInfo(dbxFileInfo.path.getName(),
                            dbxFileInfo.isFolder,
                            dbxFileInfo.size,
                            dbxFileInfo.modifiedTime));
                }
            }
        } catch (DbxException e) {
            ALog.e(TAG, e, String.format("Cannot get file info form Dropbox folder \"%s\".", dropboxFolder));
        }
        return fileInfoList;
    }

    public void downloadAll(DbxPath dropboxFolder, DropboxOperationListener dropboxOperationListener) throws DropboxAccountException {
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFileSystem.awaitFirstSync();
            List<DbxFileInfo> dbxFileInfoList = dbxFileSystem.listFolder(dropboxFolder);
            ALog.d(TAG, "Files available in Dropbox [" + dbxFileInfoList + "]");
            for (DbxFileInfo dbxFileInfo : dbxFileInfoList) {
                if (isWorldFile(dbxFileInfo)) {
                    downloadSingleFile(dropboxOperationListener, dbxFileSystem, dbxFileInfo);
                }
            }
            if (downloadingFiles.isEmpty()) {
                try {
                    dropboxOperationListener.operationFinished(DropboxOperationEnum.ALL_DOWNLOADS_FINISHED, null, true);
                } catch (MineSyncException e) {
                    // ignore
                }
            }
        } catch (DbxException e) {
            ALog.e(TAG, e, "Cannot download all files form Dropbox");
        }
    }

    public void downloadFile(String filename, DropboxOperationListener dropboxOperationListener) throws DropboxAccountException, MineSyncException {
        downloadFile(filename, SyncTypeEnum.AUTO, dropboxOperationListener);
    }

    public void downloadFile(String worldName, SyncTypeEnum syncType, DropboxOperationListener dropboxOperationListener)
            throws MineSyncException, DropboxAccountException {

        final DbxPath dbxPath = DropboxUtils.getDbxFile(worldName, syncType);
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFileSystem.awaitFirstSync();
            DbxFile dbxFile = dbxFileSystem.open(dbxPath);
            try {
                dropboxOperationListener
                        .operationFinished(DropboxOperationEnum.FILE_DOWNLOAD_FINISHED, getFile(dbxFile), syncType == SyncTypeEnum.AUTO);
            } catch (MineSyncException e) {
                // ignore
            } catch (IOException e) {
                throw new MineSyncException("Cannot open remote file \"" + dbxFile.getPath() + "\".", e);

            } finally {
                DropboxUtils.closeFileQuietly(dbxFile);
            }
        } catch (DbxException e) {
            throw new MineSyncException("Cannot retrieve remote world form Dropbox \"" + dbxPath.getName() + "\".", e);
        }
    }

    public void uploadFiles(final List<File> fileList, final DropboxOperationListener dropboxOperationListener, final boolean toSyncDirectory) throws DropboxAccountException {
        DbxFile dbxFile = null;
        for (final File file : fileList) {
            uploadingFiles.add(file.getName());
            try {
                DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
                dbxFile = getDbxFile(file, dbxFileSystem, toSyncDirectory);
                dbxFile.writeFromExistingFile(file, false);
                dbxFile.addListener(new DbxFile.Listener() {
                    @Override
                    public void onFileChange(DbxFile dbxFile) {
                        try {
                            if (dbxFile.getSyncStatus().pending.equals(DbxFileStatus.PendingOperation.NONE)) {
                                ALog.d(TAG, "File upload finished [" + file.getName() + "].");
                                try {
                                    dropboxOperationListener.operationFinished(DropboxOperationEnum.FILE_UPLOAD_FINISHED, file, toSyncDirectory);
                                } catch (MineSyncException e) {
                                    // ignore
                                }
                                uploadingFiles.remove(dbxFile.getPath().getName());
                                dbxFile.close();
                                if (uploadingFiles.isEmpty()) {
                                    ALog.d(TAG, "All file upload finished [" + fileList + "].");
                                    try {
                                        dropboxOperationListener.operationFinished(DropboxOperationEnum.ALL_UPLOADS_FINISHED, null, toSyncDirectory);
                                    } catch (MineSyncException e) {
                                        // ignore
                                    }
                                }
                            }
                        } catch (DbxException e) {
                            ALog.e(TAG, e, "Cannot access Dropbox file \"" + dbxFile.getPath().getName() + "\".");
                        }
                    }
                });
            } catch (DbxException e) {
                ALog.e(TAG, e, "Cannot access Dropbox");
            } catch (IOException e) {
                ALog.e(TAG, e, "Cannot write on Dropbox file \"" + dbxFile.getPath().getName() + "\".");
            }
        }
    }

    public void uploadFile(final File file, final DropboxOperationListener dropboxOperationListener) throws DropboxException {
        uploadFile(file, dropboxOperationListener, true);
    }

    public void uploadFile(final File file, final DropboxOperationListener dropboxOperationListener, boolean toSyncDirectory)
            throws DropboxException {
        DbxFile dbxFile = null;
        DbxFile.Listener fileListener;
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFile = getDbxFile(file, dbxFileSystem, toSyncDirectory);
            dbxFile.writeFromExistingFile(file, false);
            fileListener = getFileListener(file, dropboxOperationListener, toSyncDirectory);
            DbxFileQueue.getInstance().add(file.getName(), new DbxFileElement(dbxFile, fileListener));
            dbxFile.addListener(fileListener);

        } catch (DbxException.Unauthorized e) {
            throw new DropboxAccountException("Unauthorized linked account");

        } catch (IOException e) {
            DropboxUtils.closeFileQuietly(dbxFile);
            throw new DropboxException("Cannot upload file \"" + file.getName() + "\" to Dropbox", e);
        }
    }

    private DbxFileSystem getLinkedAccountFileSystem() throws DropboxAccountException {
        DbxAccount linkedAccount = dbxAccountManager.getLinkedAccount();
        DbxFileSystem dbxFileSystem;
        if (linkedAccount == null) {
            throw new DropboxAccountException("No linked account");
        }
        try {
            dbxFileSystem = DbxFileSystem.forAccount(linkedAccount);

        } catch (DbxException.Unauthorized unauthorized) {
            throw new DropboxAccountException("Unauthorized linked account");
        }
        return dbxFileSystem;
    }

    private boolean isWorldFile(DbxFileInfo dbxFileInfo) {
        return !dbxFileInfo.isFolder
                && !WORLDS_JSON_FILENAME.equals(dbxFileInfo.path.getName());
    }

    public void moveToNoSyncDirectory(String filename) throws DropboxAccountException {
        notEmpty(filename, "filename cannot be empty");
        ALog.d(TAG, "move [" + filename + "] to no_sync directory");
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFileSystem.move(DropboxUtils.getRootPath(filename), DropboxUtils.getNoSyncPath(filename));

        } catch (DbxException e) {
            ALog.e(TAG, e, "Cannot move file \"" + filename + "\" from ROOT to no_sync directory.");
        }
    }

    public void moveToSyncDirectory(String filename) throws DropboxAccountException {
        notEmpty(filename, "filename cannot be empty");
        ALog.d(TAG, "move [" + filename + "] to ROOT directory");
        try {
            DbxFileSystem dbxFileSystem = getLinkedAccountFileSystem();
            dbxFileSystem.move(DropboxUtils.getNoSyncPath(filename), DropboxUtils.getRootPath(filename));

        } catch (DbxException e) {
            ALog.e(TAG, e, "Cannot move file \"" + filename + "\" from no_sync to ROOT directory.");
        }
    }

    private void downloadSingleFile(DropboxOperationListener dropboxOperationListener, DbxFileSystem dbxFileSystem,
                                    DbxFileInfo dbxFileInfo) {
        DbxFile dbxFile = null;
        try {
            dbxFile = DropboxUtils.openFile(TAG, dbxFileSystem, dbxFileInfo);
            ALog.d(TAG, "\"" + dbxFile.getPath().getName()
                    + "\" is cached? [" + dbxFile.getSyncStatus().isCached
                    + "] pending status [" + dbxFile.getSyncStatus().pending + "]");
            downloadingFiles.add(dbxFile.getPath().getName());
            ALog.i(TAG, "Pending downloading of file [" + dbxFile.getPath() + "]");
            try {
                dropboxOperationListener
                        .operationFinished(DropboxOperationEnum.FILE_DOWNLOAD_FINISHED, getFile(dbxFile), DropboxUtils.isSyncDirectory(dbxFileInfo));
            } catch (MineSyncException e) {
                // ignore
            }
            downloadingFiles.remove(dbxFile.getPath().getName());

        } catch (IOException e) {
            ALog.e(TAG, e, "Cannot read stream for file \"" + dbxFileInfo.path.getName() + "\".");

        } finally {
            DropboxUtils.closeFileQuietly(dbxFile);
        }
    }

    private DbxFile.Listener getFileListener(final File file, final DropboxOperationListener dropboxOperationListener, final boolean toSyncDirectory) {
        return new DbxFile.Listener() {
            @Override
            public void onFileChange(DbxFile dbxFile) {
                try {
                    if (dbxFile.getSyncStatus().pending.equals(DbxFileStatus.PendingOperation.NONE)) {
                        ALog.d(TAG, "File upload finished [%s] to sync dir [%s].", file.getName(), toSyncDirectory);
                        try {
                            dropboxOperationListener.operationFinished(DropboxOperationEnum.FILE_UPLOAD_FINISHED, file, toSyncDirectory);
                        } catch (MineSyncException e) {
                            // ignore
                        }
                        DbxFileQueue.getInstance().remove(file.getName());
                        DropboxUtils.closeFileQuietly(dbxFile);
                    }
                } catch (DbxQueueException e) {
                    ALog.e(TAG, e, "Cannot remove file \"" + file.getName() + "\" from DbxFileQueue.");
                } catch (Exception e) {
                    ALog.e(TAG, e, "Cannot access Dropbox file \"" + dbxFile.getPath().getName() + "\".");
                }
            }
        };
    }

    private File getFile(DbxFile dbxFile) throws IOException {
        File file = new File(tmpDirectory, dbxFile.getPath().getName());
        FileUtils.copyInputStreamToFile(dbxFile.getReadStream(), file);
        return file;
    }

    private DbxFile getDbxFile(File file, DbxFileSystem dbxFileSystem, boolean toSyncDirectory) throws DbxException {
        if (toSyncDirectory) {
            return DropboxUtils.createOrOpenFile(dbxFileSystem, file);
        } else {
            return DropboxUtils.createOrOpenNoSyncFile(dbxFileSystem, file);
        }
    }
}