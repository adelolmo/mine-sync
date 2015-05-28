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

package org.ado.minesync.minecraft;

import android.content.Context;
import android.util.Log;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxPath;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.commons.IO;
import org.ado.minesync.commons.ZipArchiver;
import org.ado.minesync.db.*;
import org.ado.minesync.dropbox.*;
import org.ado.minesync.exception.DropboxAccountException;
import org.ado.minesync.exception.DropboxException;
import org.ado.minesync.exception.MineSyncException;
import org.ado.minesync.gui.ExceptionNotifier;
import org.ado.minesync.json.JsonWorldManager;
import org.ado.minesync.service.MineSyncServiceManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Manages the upload and download of minecraft worlds.<br/><br/>
 * Note: These is potentially long run process, so <b>DO NOT</b> run on UI thread!
 *
 * @author andoni
 * @since 1.0.0
 */
public class MinecraftWorldManager {

    private static final String TAG = MinecraftWorldManager.class.getName();
    private static final String CONFLICTED_COPY = "(conflicted copy";
    private static final String ZIP_FILE_EXTENSION = ".zip";

    private DropboxFileManager dropboxFileManager;
    private MineSyncWorldStatus mineSyncWorldStatus;
    private MineSyncDbOpenHelper dbHelper;
    private JsonWorldManager jsonWorldManager;
    private ZipArchiver zipArchiver;
    private MinecraftData minecraftData;
    private File tmpDirectory;
    private Context context;

    public MinecraftWorldManager() {
    }

    public MinecraftWorldManager(DbxAccountManager dbxAccountManager, Context context) {
        notNull(dbxAccountManager, "dbxAccountManager cannot be null");
        notNull(context, "context cannot be null");

        dropboxFileManager = new DropboxFileManager(dbxAccountManager, context.getCacheDir());
        mineSyncWorldStatus = new MineSyncWorldStatus(context.getApplicationContext());
        dbHelper = MineSyncDbOpenHelper.getInstance(context);
        jsonWorldManager = new JsonWorldManager(context);
        zipArchiver = new ZipArchiver();
        minecraftData = new MinecraftData();
        tmpDirectory = context.getCacheDir();
        this.context = context;
    }

    public int getDropboxNumberOfFiles() throws DbxException, DropboxAccountException {
        return dropboxFileManager.getDropboxNumberOfFiles();
    }

    public void uploadAll(final MinecraftWorldListener minecraftWorldListener) {
        notNull(minecraftWorldListener, "minecraftWorldListener cannot be null");

        List<MinecraftWorld> minecraftWorlds = minecraftData.getWorlds();
        final List<String> pendingFiles = getPendingFiles(minecraftWorlds);
        ALog.d(TAG, "pending files to upload: " + pendingFiles);
        for (MinecraftWorld minecraftWorld : minecraftWorlds) {
            try {
                File zipWorld = compressAndSaveWorldStatus(minecraftWorld);
                minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.ZIP, zipWorld);
                dropboxFileManager.uploadFile(zipWorld, new DropboxOperationListener() {
                    @Override
                    public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) {
                        if (DropboxOperationEnum.FILE_UPLOAD_FINISHED.equals(dropboxOperation)) {
                            ALog.d(TAG, "file uploaded: " + file.getAbsolutePath());
                            pendingFiles.remove(file.getName());
                            minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.NETWORK, file);
                            ALog.d(TAG, "pending files to upload: " + pendingFiles);
                            if (pendingFiles.isEmpty()) {
                                minecraftWorldListener.operationFinished();
                            }
                            FileUtils.deleteQuietly(file);
                        }
                    }
                });
            } catch (Exception e) {
                ALog.e(TAG, e, "Cannot upload world \"" + minecraftWorld.getName() + "\".");
            }
        }
    }

    public void uploadWorld(String worldName, SyncTypeEnum syncType, final MinecraftWorldListener minecraftWorldListener)
            throws MineSyncException {
        notNull(worldName, "worldName cannot be null");
        notNull(syncType, "syncType cannot be null");

        uploadWorld(minecraftData.getWorld(worldName),
                minecraftWorldListener,
                SyncTypeEnum.AUTO == syncType);
    }


    public void uploadWorld(String worldName, final MinecraftWorldListener minecraftWorldListener, boolean toSyncDirectory)
            throws MineSyncException, DropboxException {
        uploadWorld(minecraftData.getWorld(worldName),
                minecraftWorldListener,
                toSyncDirectory);
    }

    public void uploadWorld(final MinecraftWorld minecraftWorld, final MinecraftWorldListener minecraftWorldListener,
                            boolean toSyncDirectory)
            throws MineSyncException {
        notNull(minecraftWorld, "minecraftWorld cannot be null");
        notNull(minecraftWorldListener, "minecraftWorldListener cannot be null");

        try {
            MineSyncServiceManager.stopWorldSync(context);
            File zipWorld = compressWorld(minecraftWorld);
            minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.ZIP, zipWorld);
            dropboxFileManager.uploadFile(zipWorld, new DropboxOperationListener() {
                @Override
                public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) {
                    if (DropboxOperationEnum.FILE_UPLOAD_FINISHED.equals(dropboxOperation)
                            && StringUtils.equals(minecraftWorld.getName(), MinecraftUtils.getWorldName(file))) {

                        updateWorldStatus(file, HistoryActionEnum.UPLOAD, toSyncDirectory ? SyncTypeEnum.AUTO : SyncTypeEnum.MANUAL);
                        minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.NETWORK, file);
                        FileUtils.deleteQuietly(file);
                        MineSyncServiceManager.startWorldSync(context);
                    }
                }
            }, toSyncDirectory);
        } catch (DropboxException e) {
            MineSyncServiceManager.startWorldSync(context);
            throw new MineSyncException("Could not upload local world \"" + minecraftWorld.getName() + "\".", e);

        } catch (IOException e) {
            MineSyncServiceManager.startWorldSync(context);
            throw new MineSyncException("Could not compress local world \"" + minecraftWorld.getName() + "\".", e);
        }
    }

    public void downloadAll(final MinecraftWorldListener minecraftWorldListener) throws DropboxAccountException {
        notNull(minecraftWorldListener, "minecraftWorldListener cannot be null");

        for (FileInfo fileInfo : dropboxFileManager.getFolderInfo(DropboxUtils.NO_SYNC_DROPBOX_PATH)) {
            updateWorldStatus(fileInfo, HistoryActionEnum.DOWNLOAD, SyncTypeEnum.MANUAL);

        }

        dropboxFileManager.downloadAll(DbxPath.ROOT, new DropboxOperationListener() {
            @Override
            public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) {
                if (DropboxOperationEnum.FILE_DOWNLOAD_FINISHED.equals(dropboxOperation)) {
                    try {
                        extractAndReplaceLocalWorld(file, SyncTypeEnum.AUTO);
                        minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.NETWORK, file);

                    } catch (MineSyncException e) {
                        ALog.w(TAG, e, String.format("Could not replace local world \"%s\". It will be ignored.", file.getName()));
                    }
                }
                if (DropboxOperationEnum.ALL_DOWNLOADS_FINISHED.equals(dropboxOperation)) {
                    minecraftWorldListener.operationFinished();
                }
            }
        });
    }

    public void downloadWorld(String worldName, final SyncTypeEnum syncType, final MinecraftWorldListener minecraftWorldListener) throws MineSyncException, DropboxAccountException {
        notNull(worldName, "worldName cannot be null");
        notNull(syncType, "syncType cannot be null");
        notNull(minecraftWorldListener, "minecraftWorldListener cannot be null");

        dropboxFileManager.downloadFile(worldName + ".zip", syncType, new DropboxOperationListener() {
            @Override
            public void operationFinished(DropboxOperationEnum dropboxOperation, File file, boolean toSyncDirectory) throws MineSyncException {
                if (DropboxOperationEnum.FILE_DOWNLOAD_FINISHED.equals(dropboxOperation)) {
                    extractAndReplaceLocalWorld(file, syncType);
                    minecraftWorldListener.operationFinished(MinecraftWorldActionEnum.NETWORK, file);
                }
            }
        });
    }

    /**
     * Updates the content of the local world which name matches <code>filename</code> without extension
     * with the content of the compress zip world in <code>inputStream</code>.
     *
     * @param filename    the filename.
     * @param inputStream the zip file stream.
     * @throws org.ado.minesync.exception.MineSyncException if the zip file contains sync conflicts.
     * @since 1.2.0
     */
    public void update(String filename, InputStream inputStream) throws MineSyncException {
        notEmpty(filename, "filename cannot be null.");
        notNull(inputStream, "inputStream cannot be null.");

        ALog.i(TAG, "updating world [" + filename + "]...");
        File zipWorld = null;
        try {
            zipWorld = IO.getFile(filename, inputStream, context.getCacheDir());
            MinecraftUtils.validateNotConflictedZipWorld(zipWorld);
            if (MinecraftUtils.isMinecraftInstalled()) {
                File worldDirectory = MinecraftUtils.getWorldDirectory(filename);
                if (zipWorld.length() == 0) {
                    ALog.w(TAG, "Zip world file is empty");
                } else {
                    zipArchiver.unpackZip(zipWorld, worldDirectory);
                    updateWorldStatus(zipWorld, worldDirectory, HistoryActionEnum.DOWNLOAD);
                }
            } else {
                ALog.w(TAG, "Minecraft home directory not found");
            }

        } catch (IOException e) {
            ALog.e(TAG, e, "Cannot update local world \"%s\".", MinecraftUtils.getDirectoryName(filename));
        } finally {
            IOUtils.closeQuietly(inputStream);
            FileUtils.deleteQuietly(zipWorld);
        }
    }

    public void changeSyncType(String worldName, SyncTypeEnum syncType, Date date, long size) {
        notNull(worldName, "worldName cannot be null");
        notNull(syncType, "syncType cannot be null");

        WorldEntity worldEntity = dbHelper.getWorldByName(worldName);
        if (worldEntity == null) {
            worldEntity = dbHelper.save(new WorldEntity(worldName, date, size));
        }
        changeSyncType(worldEntity, syncType);
    }

    public void changeSyncType(WorldEntity worldEntity, SyncTypeEnum syncType) {
        notNull(worldEntity, "worldEntity cannot be null");
        notNull(syncType, "syncType cannot be null");

        dbHelper.updateWorldSyncType(worldEntity.getName(), syncType);
        jsonWorldManager.updateJsonWorldsFile(dbHelper.getWorldAll());

        try {
            switch (syncType) {
                case AUTO:
                    dropboxFileManager
                            .moveToSyncDirectory(MinecraftUtils.getWorldFilename(worldEntity.getName()));
                    break;
                case MANUAL:
                    dropboxFileManager
                            .moveToNoSyncDirectory(MinecraftUtils.getWorldFilename(worldEntity.getName()));
                    break;
            }
        } catch (DropboxAccountException e) {
            ExceptionNotifier.notifyException(context, e);
        }
    }

    private File compressAndSaveWorldStatus(MinecraftWorld minecraftWorld) throws IOException {
        File zipFile = compressWorld(minecraftWorld);
        updateWorldStatus(zipFile, HistoryActionEnum.UPLOAD);
        return zipFile;
    }

    private File compressWorld(MinecraftWorld minecraftWorld) throws IOException {
        File zipFile = new File(tmpDirectory, minecraftWorld.getName().concat(ZIP_FILE_EXTENSION));
        zipArchiver.zip(minecraftWorld.getContentList(), zipFile);
        return zipFile;
    }

    private List<String> getPendingFiles(List<MinecraftWorld> minecraftWorlds) {
        List<String> pendingUpload = new ArrayList<String>();
        for (MinecraftWorld minecraftWorld : minecraftWorlds) {
            pendingUpload.add(minecraftWorld.getName().concat(ZIP_FILE_EXTENSION));
        }
        return pendingUpload;
    }

    private void extractAndReplaceLocalWorld(File zippedWorldFile, SyncTypeEnum syncType) throws MineSyncException {
        ALog.d(TAG, "extract world from file [%s]", zippedWorldFile.getAbsolutePath());
        checkConflictedZipWorld(zippedWorldFile);
        if (MinecraftUtils.isMinecraftInstalled()) {
            try {
                if (zippedWorldFile.length() == 0) {
                    ALog.w(TAG, "Zip world file is empty");
                } else {
                    File outputDir = getWorldDirectory(zippedWorldFile.getName());
                    zipArchiver.unpackZip(zippedWorldFile, outputDir);
                    updateWorldStatus(outputDir, HistoryActionEnum.DOWNLOAD, syncType);
                    FileUtils.deleteQuietly(zippedWorldFile);
                }
            } catch (IOException e) {
                throw new MineSyncException("Impossible to extract zip world \""
                        + zippedWorldFile.getAbsolutePath() + "\".", e);
            }
        } else {
            Log.w(TAG, "Minecraft home directory not found");
        }
    }

    private void updateWorldStatus(File zipFile, File outputDir, HistoryActionEnum historyActionEnum) {
        updateWorldStatus(zipFile, outputDir.lastModified(), historyActionEnum);
    }

    private void updateWorldStatus(File zipFile, HistoryActionEnum historyActionEnum) {
        updateWorldStatus(zipFile, zipFile.lastModified(), historyActionEnum);
    }

    private void updateWorldStatus(File zipFile, HistoryActionEnum historyActionEnum, SyncTypeEnum syncType) {
        mineSyncWorldStatus
                .updateWorld(new WorldEntity(MinecraftUtils.getWorldName(zipFile),
                                new Date(zipFile.lastModified()),
                                zipFile.length(),
                                syncType),
                        historyActionEnum);
    }

    private void updateWorldStatus(FileInfo fileInfo, HistoryActionEnum historyActionEnum, SyncTypeEnum syncType) {
        mineSyncWorldStatus
                .updateWorld(new WorldEntity(MinecraftUtils.getWorldName(fileInfo.getPath()),
                                fileInfo.getModifiedTime(),
                                fileInfo.getSize(),
                                syncType),
                        historyActionEnum);
    }

    private void updateWorldStatus(File zipFile, long modificationDate, HistoryActionEnum historyActionEnum, SyncTypeEnum syncType) {
        mineSyncWorldStatus
                .updateWorld(new WorldEntity(MinecraftUtils.getWorldName(zipFile),
                                new Date(modificationDate),
                                zipFile.length(),
                                syncType),
                        historyActionEnum);
    }

    private void updateWorldStatus(File zipFile, long modificationDate, HistoryActionEnum historyActionEnum) {
        mineSyncWorldStatus
                .updateWorld(new WorldEntity(MinecraftUtils.getWorldName(zipFile),
                                new Date(modificationDate),
                                zipFile.length()),
                        historyActionEnum);
    }

    private void checkConflictedZipWorld(File zipWorld) throws MineSyncException {
        if (zipWorld.getName().contains(CONFLICTED_COPY)) {
            throw new MineSyncException("Conflicted copy of world \""
                    + getDirectoryName(zipWorld.getName()) + "\".");
        }
    }

    private File getWorldDirectory(String filename) throws MineSyncException {
        try {
            File outputDir = new File(MINECRAFT_WORLDS, getDirectoryName(filename));
            if (outputDir.exists()) {
                FileUtils.cleanDirectory(outputDir);
            } else {
                ALog.i(TAG, "Create new world directory [%s].", outputDir.getName());
                FileUtils.forceMkdir(outputDir);
            }
            return outputDir;
        } catch (IOException e) {
            throw new MineSyncException("Unable to retrieve local world directory for file \""
                    + filename + "\".", e);
        }
    }

    private String getDirectoryName(String filename) {
        try {
            return filename.substring(0, filename.lastIndexOf("."));
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong filename \"" + filename + "\".");
        }
    }
}
