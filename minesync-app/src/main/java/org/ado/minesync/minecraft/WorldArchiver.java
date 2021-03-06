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
import org.ado.minesync.commons.ALog;
import org.ado.minesync.commons.ZipArchiver;

import java.io.File;
import java.io.IOException;

/**
 * Handles the compression of Minecraft worlds.
 *
 * @author andoni
 * @since 1.2.0
 */
public class WorldArchiver {

    private static final String TAG = WorldArchiver.class.getName();
    private static final String FILE_ZIP_EXTENSION = ".zip";

    private ZipArchiver zipArchiver;

    public WorldArchiver() {
        zipArchiver = new ZipArchiver();
    }

    public File getZippedWorld(Context context, MinecraftWorld world) throws IOException {
        File zipWorldFile = new File(context.getCacheDir(), getZipFilename(world));
        ALog.v(TAG, "compress world [%s] to zip [%s].",world.getName(), zipWorldFile.getAbsolutePath());
        zipArchiver.zip(world.getContentList(), zipWorldFile);
        return zipWorldFile;
    }

    private String getZipFilename(MinecraftWorld world) {
        return world.getName().concat(FILE_ZIP_EXTENSION);
    }
}