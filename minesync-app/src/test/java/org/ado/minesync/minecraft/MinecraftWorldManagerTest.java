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

import com.dropbox.sync.android.DbxAccountManager;
import org.ado.atf.AndroidTestFramework;
import org.ado.minesync.github.AndroidTestCase;
import org.ado.minesync.github.WorldEntityMatcher;
import org.ado.minesync.commons.ZipArchiver;
import org.ado.minesync.db.HistoryActionEnum;
import org.ado.minesync.db.MineSyncWorldStatus;
import org.ado.minesync.exception.MineSyncException;
import org.ado.minesync.github.mock.MockMinecraftWorldFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_HOME;
import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class MinecraftWorldManagerTest extends AndroidTestCase<MinecraftWorldManager> {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ZipArchiver zipArchiverMock;
    @Mock
    private FileInputStream inputStreamMock;
    @Mock
    private MineSyncWorldStatus mineSyncWorldStatusMock;
    @Mock
    private DbxAccountManager dbxAccountManagerMock;

    @Before
    public void setUp() throws Exception {
        addMock(zipArchiverMock);
        addMock(mineSyncWorldStatusMock);
        addMock(contextMock);
        createUnitUnderTest();

        AndroidTestFramework.init();
        MINECRAFT_HOME.mkdirs();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_nullFilename() throws Exception {
        unitUnderTest.update(null, this.inputStreamMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_nullStream() throws Exception {
        unitUnderTest.update("filename", null);
    }

    @Test
    public void testUpdate_minecraftDirectoryNotFound() throws Exception {
        FileUtils.deleteDirectory(MINECRAFT_HOME);
        String filename = "world.zip";

        unitUnderTest.update(filename, getWorldStream("src/test/resources/test.zip"));

        verifyZeroInteractions(mineSyncWorldStatusMock);
        verifyZeroInteractions(zipArchiverMock);
    }

    @Test
    public void testUpdate_localWorldNotFound() throws Exception {
        String filename = "world.zip";
        File worldZipFile = MockMinecraftWorldFactory.getMockZippedWorld(filename);

        unitUnderTest.update(filename, getWorldStream("src/test/resources/test.zip"));

        File worldDirectory = new File(MINECRAFT_WORLDS, "world");
        verify(mineSyncWorldStatusMock)
                .updateWorld(argThat(new WorldEntityMatcher("world")), eq(HistoryActionEnum.DOWNLOAD));
        verify(this.zipArchiverMock).unpackZip(worldZipFile, worldDirectory);
    }

    @Test
    public void testUpdate_worldFound_zipIsEmpty() throws Exception {
        MockMinecraftWorldFactory.createWorld("world");
        String filename = "world.zip";
        File zippedWorldFile = new File(filename);
        File worldDirectory = MockMinecraftWorldFactory.createWorld("world");

        unitUnderTest.update(filename, getWorldStream("src/test/resources/empty.zip"));

        verifyZeroInteractions(mineSyncWorldStatusMock);
        verifyZeroInteractions(this.zipArchiverMock);
    }

    @Test
    public void testUpdate_worldFound() throws Exception {
        String filename = "world.zip";
        File worldDirectory = MockMinecraftWorldFactory.createWorld("world");
        File worldZipFile = MockMinecraftWorldFactory.getMockZippedWorld(filename);

        unitUnderTest.update(filename, getWorldStream("src/test/resources/test.zip"));

        verify(mineSyncWorldStatusMock)
                .updateWorld(argThat(new WorldEntityMatcher("world")), eq(HistoryActionEnum.DOWNLOAD));
        verify(zipArchiverMock).unpackZip(worldZipFile, worldDirectory);
    }

    @Test(expected = MineSyncException.class)
    public void testUpdate_conflictedWorldFound() throws Exception {
        String filename = "world (conflicted copy 2).zip";
        File worldDirectory = MockMinecraftWorldFactory.createWorld("world");
        File worldZipFile = MockMinecraftWorldFactory.getMockZippedWorld(filename);

        unitUnderTest.update(filename, getWorldStream("src/test/resources/test.zip"));

        verifyZeroInteractions(mineSyncWorldStatusMock);
        verifyZeroInteractions(zipArchiverMock);
    }

    private FileInputStream getWorldStream(String worldPath) throws IOException {
        return FileUtils.openInputStream(new File(worldPath));
    }
}