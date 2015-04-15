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

import android.os.Environment;
import org.ado.atf.AndroidTestFramework;
import org.ado.minesync.github.AndroidTestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_HOME;
import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;
import static org.ado.minesync.github.mock.MockMinecraftWorldFactory.createWorld;
import static org.junit.Assert.*;

/**
 * Class description here.
 *
 * @author andoni
 * @since 06.12.2013
 */
public class MinecraftDataTest extends AndroidTestCase<MinecraftData> {

    @Before
    public void setUp() throws Exception {
        AndroidTestFramework.init();
        MINECRAFT_HOME.mkdirs();
    }

    @Test
    public void testGetWorlds_minecraftNotInstalled() throws Exception {
        FileUtils.deleteDirectory(MINECRAFT_HOME);

        assertTrue("no worlds", unitUnderTest.getWorlds().isEmpty());
    }

    @Test
    public void testGetWorlds_noWorlds() throws Exception {
        assertTrue("no worlds", unitUnderTest.getWorlds().isEmpty());
    }

    @Test
    public void testGetWorlds_oneEmptyWorld() throws Exception {
        List<MinecraftWorld> worldList = unitUnderTest.getWorlds();

        assertEquals("one world", 0, worldList.size());
    }

    @Test
    public void testGetWorlds_oneWorld() throws Exception {
        File world = createWorld("world");

        List<MinecraftWorld> worldList = unitUnderTest.getWorlds();

        assertEquals("one world", 1, worldList.size());
        MinecraftWorld minecraftWorld = worldList.get(0);
        assertEquals("world name", "world", minecraftWorld.getName());
        assertEquals("world name", getWorldDirectory(), minecraftWorld.getWorldDirectory().getAbsolutePath());
        assertEquals("content size", 5, minecraftWorld.getContentList().size());
        assertContainsFile("file chunks.dat", "chunks.dat", minecraftWorld.getContentList());
        assertContainsFile("file entities.dat", "entities.dat", minecraftWorld.getContentList());
        assertContainsFile("file level.dat", "level.dat", minecraftWorld.getContentList());
        assertContainsFile("file level.dat_old", "level.dat_old", minecraftWorld.getContentList());
        assertContainsFile("197445472.dat in directory players", "players/197445472.dat", minecraftWorld.getContentList());
    }

    @Test
    public void testGetWorlds_oneWorld_noFiles() throws Exception {
        File worldDirectory = new File(MINECRAFT_WORLDS, "world");
        FileUtils.forceMkdir(worldDirectory);

        List<MinecraftWorld> worldList = unitUnderTest.getWorlds();

        assertEquals("one world", 1, worldList.size());
        MinecraftWorld minecraftWorld = worldList.get(0);
        assertEquals("world name", "world", minecraftWorld.getName());
        assertEquals("world name", getWorldDirectory(), minecraftWorld.getWorldDirectory().getAbsolutePath());
        assertEquals("content size", 0, minecraftWorld.getContentList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void tesGetWorld_emptyWorldName() throws Exception {
        unitUnderTest.getWorld((File) null);
    }

    @Test
    public void tesGetWorld_notFound() throws Exception {
        assertNull("world not found", unitUnderTest.getWorld("something"));
    }

    @Test
    public void tesGetWorld() throws Exception {
        File world = createWorld("world");

        MinecraftWorld minecraftWorld = unitUnderTest.getWorld("world");

        assertNotNull("world found", minecraftWorld);
        assertEquals("world name", "world", minecraftWorld.getName());
        assertEquals("world name", getWorldDirectory(), minecraftWorld.getWorldDirectory().getAbsolutePath());
        assertEquals("content size", 5, minecraftWorld.getContentList().size());
        assertContainsFile("file chunks.dat", "chunks.dat", minecraftWorld.getContentList());
        assertContainsFile("file entities.dat", "entities.dat", minecraftWorld.getContentList());
        assertContainsFile("file level.dat", "level.dat", minecraftWorld.getContentList());
        assertContainsFile("file level.dat_old", "level.dat_old", minecraftWorld.getContentList());
        assertContainsFile("197445472.dat in directory players", "players/197445472.dat", minecraftWorld.getContentList());
    }

    private void assertContainsFile(String comment, String filename, List<File> fileList) {
        boolean found = findFile(filename, fileList);
        if (!found) {
            fail(comment.concat(" not found"));
        }
    }

    private boolean findFile(String filename, List<File> fileList) {
        boolean found = false;

        for (File file : fileList) {
            if (file.isDirectory()) {
                return findFile(filename, Arrays.asList(file.listFiles()));
            } else {
                if (getFilename(filename).equals(file.getName())) {
                    return true;
                }
            }
        }
        return found;
    }

    private String getFilename(String filePath) {
        int start = filePath.indexOf(File.separator) != -1 ? filePath.indexOf(File.separator) + 1 : 0;
        return filePath.substring(start, filePath.length());
    }

    private String getWorldDirectory() {
        return Environment.getExternalStorageDirectory() + "/games/com.mojang/minecraftWorlds/world";
    }
}
