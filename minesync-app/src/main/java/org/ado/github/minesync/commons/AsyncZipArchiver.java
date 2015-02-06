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

package org.ado.github.minesync.commons;

import android.util.Log;
import org.ado.github.minesync.minecraft.MinecraftWorld;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous thread executor to compress zip files.
 *
 * @author andoni
 * @since 1.1.5
 */
@Deprecated
public class AsyncZipArchiver {

    private static final String TAG = AsyncZipArchiver.class.getName();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ZipArchiver zipArchiver;

    public AsyncZipArchiver() {
        zipArchiver = new ZipArchiver();
    }

    public File zip(File cacheDir, MinecraftWorld world, File zipFile) throws ExecutionException, InterruptedException {
        return executor.submit(new ZipArchiverCallable(cacheDir, world, zipFile)).get();
    }

    class ZipArchiverCallable implements Callable<File> {
        private File tmpDir;
        private MinecraftWorld minecraftWorld;
        private File zipFile;

        ZipArchiverCallable(File tmpDir, MinecraftWorld minecraftWorld, File zipFile) {
            this.tmpDir = tmpDir;
            this.minecraftWorld = minecraftWorld;
            this.zipFile = zipFile;
        }

        @Override
        public File call() throws Exception {
            File zipWorldFile = null;
            try {
                zipWorldFile = new File(tmpDir, getZipFilename(zipFile));
                ALog.v(TAG, "[" + Thread.currentThread().getName()
                        + "] compress world [" + minecraftWorld.getName()
                        + "] to zip [" + zipWorldFile.getAbsolutePath() + "].");
                zipArchiver.zip(minecraftWorld.getContentList(), zipWorldFile);
                return zipWorldFile;
            } catch (Exception e) {
                Log.e(TAG, "[" + Thread.currentThread().getName()
                        + "Cannot compress world \"" + zipFile.getName() + "\". Reason: " + e.getMessage());
                FileUtils.deleteQuietly(zipWorldFile);
                throw e;
            }
        }

        private String getZipFilename(File zipFile) {
            return zipFile.getName().concat(".zip");
        }
    }
}