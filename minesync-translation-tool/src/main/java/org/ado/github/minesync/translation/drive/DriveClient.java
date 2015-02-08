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

package org.ado.github.minesync.translation.drive;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class description here.
 *
 * @author andoni
 * @since 07.10.2014
 */
public class DriveClient {

    private static final String FOLDER_NAME = "Mine Sync GitHub";
    private static final String FILENAME_TEMPLATE = "Translations v.%s";
    private static final String MICROSOFT_OFFICE_2007_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private Drive drive;

    public DriveClient() {
        drive = DriveClientFactory.createDrive();
    }

    public InputStream getTranslationFile(String version) throws IOException {
        final HashMap<String, File> translationFiles = getFilesInFolder(drive, FOLDER_NAME);
        final File translationFile = translationFiles.get(String.format(FILENAME_TEMPLATE, version));

        if (translationFile == null) {
            throw new IOException(String.format("No translation file found for version %s.", version));
        }
        System.out.println("Using \"" + translationFile.getTitle() + "\" to update strings.");

        final String exportUrl = translationFile.getExportLinks().get(MICROSOFT_OFFICE_2007_MIME_TYPE);
        final HttpResponse resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(exportUrl)).execute();
        return resp.getContent();
    }

    private HashMap<String, File> getFilesInFolder(Drive service, String folderName)
            throws IOException {
        final HashMap<String, File> map = new HashMap<String, File>();
        final Drive.Children.List request = service.children().list(getFile(service, folderName).getId());

        do {
            try {
                ChildList children = request.execute();

                System.out.println("Loading translation files...");
                for (ChildReference child : children.getItems()) {
                    final File file = service.files().get(child.getId()).execute();
                    System.out.println("* " + file.getTitle());
                    map.put(file.getTitle(), file);
                }
                request.setPageToken(children.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        return map;
    }

    private File getFile(Drive drive, String name) throws IOException {
        List<File> fileList = getAllFiles(drive);
        for (File file : fileList) {
            if (name.equals(file.getTitle())) {
                return file;
            }
        }
        return null;
    }

    private List<File> getAllFiles(Drive service) throws IOException {
        List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list();

        do {
            try {
                FileList files = request.execute();
                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return result;
    }
}