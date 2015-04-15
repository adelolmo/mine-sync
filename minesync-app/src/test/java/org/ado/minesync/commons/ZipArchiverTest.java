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

import org.ado.minesync.github.ClassTestCase;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.ado.minesync.github.TestFileUtils.addFile;
import static org.ado.minesync.github.TestFileUtils.getResource;
import static org.apache.commons.io.FileUtils.forceMkdir;

/**
 * Class description here.
 *
 * @author andoni
 * @since 07.12.2013
 */
public class ZipArchiverTest extends ClassTestCase<ZipArchiver> {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = IllegalArgumentException.class)
    public void testZip_nullList() throws Exception {
        unitUnderTest.zip(null, File.createTempFile("junit", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZip_nullFile() throws Exception {
        unitUnderTest.zip(new ArrayList<File>(), null);
    }

    @Test
    public void testZip_emptyList() throws Exception {
        File zipFile = this.temporaryFolder.newFile("file.zip");

        unitUnderTest.zip(new ArrayList<File>(), zipFile);

        assertEquals("zip file size", 22, zipFile.length());
    }

    @Test
    public void testZip_onlyFiles() throws Exception {
        File zipFile = this.temporaryFolder.newFile("file.zip");
        addFile(this.temporaryFolder.getRoot(), "mockworld/entities.dat");
        addFile(this.temporaryFolder.getRoot(), "mockworld/level.dat");

        unitUnderTest.zip(Arrays.asList(this.temporaryFolder.getRoot().listFiles()), zipFile);

        assertEquals("zip file size", 2521, zipFile.length());
    }

    @Test
    public void testZip_filesAndDirectories() throws Exception {
        File mainDirectory = this.temporaryFolder.newFolder();
        forceMkdir(mainDirectory);
        File zipFile = this.temporaryFolder.newFile("file.zip");
        addFile(mainDirectory, "mockworld/entities.dat");
        addFile(mainDirectory, "mockworld/level.dat");
        File directory = new File(mainDirectory, "directory");
        forceMkdir(directory);
        addFile(directory, "mockworld/level.dat");

        unitUnderTest.zip(Arrays.asList(mainDirectory.listFiles()), zipFile);

        assertEquals("zip file size", 2944, zipFile.length());
        List<? extends ZipEntry> zipEntryList = getZipEntryList(new ZipFile(zipFile));
        assertContainsFile("entities.dat", "entities.dat", zipEntryList);
        assertContainsFile("level.dat", "entities.dat", zipEntryList);
        assertContainsFile("level.dat in directory", "directory/level.dat", zipEntryList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnpackZip_nullInputStream() throws Exception {
        unitUnderTest.unpackZip((FileInputStream) null, File.createTempFile("junit", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnpackZip_outputDirectory() throws Exception {
        unitUnderTest.unpackZip(new FileInputStream(File.createTempFile("junit", null)), null);
    }

    @Test
    public void testUnpackZip_onlyFiles() throws Exception {
        unitUnderTest.unpackZip(getInputStream("test.zip"), this.temporaryFolder.getRoot());

        List<File> fileList = Arrays.asList(this.temporaryFolder.getRoot().listFiles());
        assertEquals("number of files", 4, fileList.size());
        assertContainsExtractedFile("chunks.dat", "chunks.dat", fileList);
        assertContainsExtractedFile("level.dat", "entities.dat", fileList);
        assertContainsExtractedFile("level.dat_old", "level.dat_old", fileList);
        assertContainsExtractedFile("entities.dat", "entities.dat", fileList);
    }

    @Test
    public void testUnpackZip_filesAndDirectories() throws Exception {
        unitUnderTest.unpackZip(getInputStream("withOneDirectory.zip"), this.temporaryFolder.getRoot());

        List<File> fileList = Arrays.asList(this.temporaryFolder.getRoot().listFiles());
        assertEquals("number of files", 3, fileList.size());
        assertContainsExtractedFile("entities.dat", "entities.dat", fileList);
        assertContainsExtractedFile("level.dat", "entities.dat", fileList);
        assertContainsExtractedFile("level.dat in directory", "directory/level.dat", fileList);
    }

    private void assertContainsFile(String comment, String filename, List<? extends ZipEntry> zipEntryList) {
        boolean found = false;
        for (ZipEntry zipEntry : zipEntryList) {
            if (zipEntry.getName().equals(filename)) {
                found = true;
            }
        }
        if (!found) {
            fail(comment.concat(" not found"));
        }
    }

    private void assertContainsExtractedFile(String comment, String filename, List<File> fileList) {
        boolean found = findFile(filename, fileList);
        if (!found) {
            fail(comment.concat(" not found"));
        }
    }

    private boolean findFile(String filename, List<File> fileList) {
        for (File file : fileList) {
            if (FilenameUtils.getName(filename).equals(file.getName())) {
                return true;
            }
        }
        return false;
    }

    private FileInputStream getInputStream(String filename) throws FileNotFoundException {
        return new FileInputStream(getResource(filename).getPath());
    }

    private List<? extends ZipEntry> getZipEntryList(ZipFile zipFile) {
        List<? extends ZipEntry> list = Collections.list(zipFile.entries());
        System.out.println(list);
        return list;
    }
}
