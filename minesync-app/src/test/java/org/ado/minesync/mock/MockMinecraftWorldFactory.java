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

package org.ado.minesync.mock;

import org.ado.minesync.TestFileUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.ado.atf.Config.APPLICATION_CACHE_DIR;
import static org.ado.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 20/10/13
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class MockMinecraftWorldFactory extends TestFileUtils {

    public static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    public static File createWorld(String name) throws IOException {
        File worldDirectory = new File(MINECRAFT_WORLDS, name);
        FileUtils.forceMkdir(worldDirectory);
        addFile(worldDirectory, "mockworld/chunks.dat");
        addFile(worldDirectory, "mockworld/entities.dat");
        addFile(worldDirectory, "mockworld/level.dat");
        addFile(worldDirectory, "mockworld/level.dat_old");
        addFile(worldDirectory, "mockworld/players/197445472.dat");
        return worldDirectory;
    }

    public static File getMockZippedWorld(String filename) throws IOException {
//        File mockWorld = File.createTempFile("mock", "world");
//        mockWorld.createNewFile();
        File destination = new File(APPLICATION_CACHE_DIR, filename);
        FileUtils.copyURLToFile(getResource("bbbb-see-view.zip"), destination);
        return destination;
    }

}
