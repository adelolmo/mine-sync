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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.ado.minesync.config.AppConstants.L;
import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.lang.Validate.notNull;

public class ZipArchiver {

    private static final String TAG = ZipArchiver.class.getName();
    private static final int BUFFER = 2048;

    public ZipArchiver() {
        super();
    }

    public void zip(List<File> fileList, File zipFile) throws IOException {
        notNull(fileList, "fileList cannot be null");
        notNull(zipFile, "zipFile cannot be null");

        ALog.d(TAG, "Zip into [" + zipFile.getAbsolutePath() + "] elements [" + fileList + "].");
        FileOutputStream dest = null;
        ZipOutputStream zout = null;
        Date startDate = new Date();
        try {
            dest = new FileOutputStream(zipFile);
            zout = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER];

            for (File file : fileList) {
                if (L) Log.v(TAG, "Adding: " + file);

                if (file.isDirectory()) {
                    for (File fileInDir : getSafeFiles(file)) {
                        zipFile(zout, data, fileInDir, file.getName());
                    }
                } else {
                    zipFile(zout, data, file, null);
                }
            }
            ALog.d(TAG, "Compression of [" + zipFile.getName()
                    + "] took [" + (new Date().getTime() - startDate.getTime()) / 1000 + "]s");
        } finally {
            IOUtils.closeQuietly(zout);
            IOUtils.closeQuietly(dest);
        }
    }

    public void unpackZip(String path, String zipname) throws IOException {
        this.unpackZip(new FileInputStream(path + zipname), null);
    }

    public void unpackZip(File zipFile, File outputDir) throws IOException {
        ALog.d(TAG, "unzip file [" + zipFile.getName() + "] to [" + outputDir.getAbsolutePath() + "]");
        this.unpackZip(new FileInputStream(zipFile), outputDir);
        if (!zipFile.setLastModified(outputDir.lastModified())) {
            Log.w(TAG, "Unable to change modification date to cached zip file [" + zipFile.getName() + "].");
        }
    }

    public void unpackZip(FileInputStream inputStream, File outputDir) throws IOException {
        notNull(inputStream, "inputStream cannot be null");
        notNull(outputDir, "outputDir cannot be null");

        ALog.d(TAG, "unzip stream to [" + outputDir.getAbsolutePath() + "]");
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            String filename;
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;
            byte[] buffer = new byte[BUFFER];
            int count;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                filename = zipEntry.getName();
                ALog.v(TAG, "creating file [" + filename + "].");

                if (isZipEntryInDirectory(zipEntry)) {
                    File directory = new File(outputDir, getDirectoryName(filename));
                    forceMkdir(directory);
                }

                File file = new File(outputDir, filename);
                fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, count);
                }
                zipInputStream.closeEntry();

            }

        } finally {
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(zipInputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    private File[] getSafeFiles(File file) {
        File[] files = file.listFiles();
        if (files == null) {
            return new File[]{};
        }
        return files;
    }

    private boolean isZipEntryInDirectory(ZipEntry zipEntry) {
        return zipEntry.getName().contains(File.separator);
    }

    private String getDirectoryName(String filename) {
        return filename.substring(0, filename.indexOf(File.separator));
    }

    private void zipFile(ZipOutputStream zout, byte[] data, File file, String directoryName) throws IOException {
        BufferedInputStream origin = null;
        FileInputStream fi = null;
        try {
            if (file.exists()) {
                fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getFilePath(file, directoryName));
                zout.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    zout.write(data, 0, count);
                }
            } else {
                ALog.w(TAG, "file \"%s\" does not exist.", file.getName());
            }
        } finally {
            IOUtils.closeQuietly(fi);
            IOUtils.closeQuietly(origin);
        }
    }

    private String getFilePath(File file, String directoryName) {
        if (StringUtils.isNotEmpty(directoryName)) {
            return file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(directoryName), file.getAbsolutePath().length());
        } else {
            return file.getName().substring(file.getName().lastIndexOf("/") + 1);
        }
    }
}