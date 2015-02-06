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

package org.ado.github.minesync.minecraft;

import org.ado.github.minesync.commons.ALog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static org.ado.github.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

public class MinecraftData {

    private static final String TAG = MinecraftData.class.getName();
    private static final String FILE_LEVEL_DAT = "level.dat";
    private static final String UNDERSCORE_CHAR = "_";
    private static final String DOT = ".";

    public List<MinecraftWorld> getWorlds() {
        List<MinecraftWorld> minecraftWorldList = new ArrayList<MinecraftWorld>();
        File[] worlds = MINECRAFT_WORLDS.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory()
                        && !pathname.getName().startsWith(UNDERSCORE_CHAR)
                        && !pathname.getName().startsWith(DOT);
            }
        });
        if (worlds != null) {
            for (File worldDirectory : worlds) {
                minecraftWorldList.add(getWorld(worldDirectory));
            }
        } else {
            ALog.i(TAG, "No world found under \"%s\".", MINECRAFT_WORLDS.getAbsolutePath());
        }
        Collections.sort(minecraftWorldList, new Comparator<MinecraftWorld>() {
            @Override
            public int compare(MinecraftWorld lhs, MinecraftWorld rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        return minecraftWorldList;
    }

    /**
     * Returns a <code>MinecraftWorld</code> given the Minecraft directory.
     *
     * @param worldDirectory the world's directory.
     * @return the <code>MinecraftWorld</code> object.
     * @since 1.2.0
     */
    public MinecraftWorld getWorld(File worldDirectory) {
        notNull(worldDirectory, "worldDirectory cannot be null");

        return getWorld(worldDirectory.getName());
    }

    /**
     * Returns a <code>MinecraftWorld</code> given the Minecraft directory name.<br/>
     * The <code>worldName</code> must be a valid directory under games/com.mojang/minecraftpe/minecraftWorlds.
     *
     * @param worldName the world's name.
     * @return the <code>MinecraftWorld</code> if found, otherwise <code>null</code>.
     * @since 1.2.0
     */
    public MinecraftWorld getWorld(String worldName) {
        notEmpty(worldName, "worldName cannot be empty");

        File worldDirectory = new File(MINECRAFT_WORLDS, worldName);
        if (worldDirectory.isDirectory() && worldDirectory.exists()) {
            Date modificationDate = new Date();
            modificationDate.setTime(getWorldsModificationDate(worldDirectory));
            return new MinecraftWorld(worldDirectory.getName(),
                    worldDirectory,
                    getRecursiveFilesInDirectory(worldDirectory),
                    modificationDate);
        }
        return null;
    }

    private long getWorldsModificationDate(File worldDirectory) {
        Collection<File> files =
                FileUtils.listFiles(worldDirectory,
                        new NameFileFilter(FILE_LEVEL_DAT),
                        FalseFileFilter.FALSE);
        if (files != null && files.size() == 1) {
            ALog.v(TAG, "using modification date from file " + FILE_LEVEL_DAT);
            return files.iterator().next().lastModified();
        } else {
            ALog.v(TAG, "using modification date from world directory");
            return worldDirectory.lastModified();
        }
    }

    private List<File> getRecursiveFilesInDirectory(File aStartingDir) {
        List<File> result = getRecursiveFilesInDirectoryNoSort(aStartingDir);
        Collections.sort(result);
        return result;
    }

    private List<File> getRecursiveFilesInDirectoryNoSort(File aStartingDir) {
        List<File> result = new ArrayList<File>();
        File[] filesAndDirs = aStartingDir.listFiles();
        if (filesAndDirs != null) {
            List<File> filesDirs = Arrays.asList(filesAndDirs);
            for (File file : filesDirs) {
                result.add(file); //always add, even if directory
                if (!file.isFile()) {
                    //must be a directory
                    //recursive call!
//                    List<File> deeperList =
                    getRecursiveFilesInDirectoryNoSort(file);
//                result.addAll(deeperList);
                }
            }
        }
        return result;
    }
}