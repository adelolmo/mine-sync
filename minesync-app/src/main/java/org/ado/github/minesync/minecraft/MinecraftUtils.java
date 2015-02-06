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
import org.ado.github.minesync.commons.DateUtils;
import org.ado.github.minesync.db.WorldEntity;
import org.ado.github.minesync.exception.MineSyncException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.ado.github.minesync.minecraft.MinecraftConstants.MINECRAFT_HOME;
import static org.ado.github.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS;
import static org.apache.commons.lang.Validate.*;

/**
 * Common utils for the Minecraft's content.
 *
 * @since 1.0.0
 */
public class MinecraftUtils {

    private static final String TAG = MinecraftUtils.class.getName();
    private static final String FILE_ZIP_EXTENSION = ".zip";

    public static boolean isWorldChanged(MinecraftWorld world, WorldEntity worldEntity) {
        notNull(world, "world cannot be null");
        notNull(worldEntity, "worldEntity cannot be null");

        boolean isChanged = worldEntity.getModificationDate().before(world.getModificationDate());
        ALog.d(TAG, "world [" + world.getName() + "] changed?: "
                + "[" + isChanged + "]. worldEntity ["
                + DateUtils.formatDate(worldEntity.getModificationDate())
                + "] world [" + DateUtils.formatDate(world.getModificationDate())
                + "].");
        return isChanged;
    }

    public static String getWorldName(File worldFile) {
        notNull(worldFile, "worldFile cannot be null");
        return getWorldName(worldFile.getName());
    }

    public static String getWorldName(String worldFilename) {
        notEmpty(worldFilename, "worldFilename cannot be empty");
        return worldFilename.lastIndexOf(".") != -1 ?
                worldFilename.substring(0, worldFilename.lastIndexOf("."))
                : worldFilename;
    }

    public static String getWorldFilename(String worldName) {
        notEmpty(worldName, "worldName cannot be empty");
        return worldName.concat(FILE_ZIP_EXTENSION);
    }

    /**
     * Retrieves the local world directory for the given <code>zipFilename</code>.
     *
     * @param zipFilename the zip file's name.
     * @return the world's directory.
     * @throws java.io.IOException if the directory can't be clear or created.
     * @since 1.2.0
     */
    public static File getWorldDirectory(String zipFilename) throws IOException {
        File outputDir = new File(MINECRAFT_WORLDS, getDirectoryName(zipFilename));
        if (outputDir.exists()) {
            FileUtils.cleanDirectory(outputDir);
        } else {
            ALog.i(TAG, "Create new world directory [" + outputDir.getName() + "].");
            FileUtils.forceMkdir(outputDir);
        }
        return outputDir;
    }

    /**
     * Returns the filename (without extension) for the given <code>zipFilename</code>.
     *
     * @param zipFilename the zip file with extension.
     * @return the filename without extension.
     * @since 1.2.0
     */
    public static String getDirectoryName(String zipFilename) {
        notEmpty(zipFilename, "zipFilename cannot be empty");
        isTrue(zipFilename.contains("."), "zipFilename must contain extension");
        try {
            return zipFilename.substring(0, zipFilename.lastIndexOf("."));
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong filename \"" + zipFilename + "\".");
        }
    }

    public static boolean isMinecraftInstalled() {
        return MINECRAFT_HOME.exists() && MINECRAFT_HOME.isDirectory();
    }

    /**
     * Validates that the given <code>zipWorld</code> is not conflicted.
     *
     * @param zipWorld the world's zip file.
     * @throws MineSyncException if the zipped world is conflicted.
     * @since 1.2.0
     */
    public static void validateNotConflictedZipWorld(File zipWorld) throws MineSyncException {
        if (zipWorld.getName().contains("(conflicted copy")) {
            throw new MineSyncException("Conflicted copy of world \""
                    + MinecraftUtils.getDirectoryName(zipWorld.getName()) + "\".");
        }
    }

    public static boolean worldExist(String worldName) {
        File worldDirectory = new File(MINECRAFT_WORLDS, worldName);
        return worldDirectory.isDirectory() && worldDirectory.exists();
    }
}
