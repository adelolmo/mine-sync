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

import android.os.FileObserver;
import android.util.Log;
import org.ado.github.minesync.commons.ALog;
import org.apache.commons.lang.StringUtils;

import java.io.File;

import static org.ado.github.minesync.config.AppConstants.L;
import static org.ado.github.minesync.minecraft.MinecraftConstants.MINECRAFT_HOME;
import static org.ado.github.minesync.minecraft.MinecraftConstants.MINECRAFT_WORLDS_DIR;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 23/09/13
 * Time: 22:29
 * To change this template use File | Settings | File Templates.
 */
public class MinecraftWorldObserver extends FileObserver {

    private static final String TAG = MinecraftWorldObserver.class.getName();

    private String minecraftWorldDirectory;
    private String previousPath = "";
    private int previousEvent = -1;
    private int eventCount = 0;

    public MinecraftWorldObserver(MinecraftWorld minecraftWorld) {
        super(minecraftWorld.getWorldDirectory().getAbsolutePath());
        this.minecraftWorldDirectory = minecraftWorld.getWorldDirectory().getAbsolutePath();
        if(L) Log.i(TAG, "Observing path [" + this.minecraftWorldDirectory + "]");
    }

    private static String getObservedPath() {
        String absolutePath = new File(MINECRAFT_HOME, MINECRAFT_WORLDS_DIR).getAbsolutePath();
        if(L) Log.i(TAG, "Observing path [" + absolutePath + "].");
        return absolutePath;
    }

    @Override
    public void onEvent(int event, String path) {
        if (L)
            Log.v(TAG, "onEvent ** . in [" + this.minecraftWorldDirectory + "] path [" + path + "] event [" + getFileAction(event) + "]");

        if (event != previousEvent
                && StringUtils.equals(path, previousPath)) {
            if (eventCount > 0) {
                ALog.v(TAG, "eventCount [" + eventCount + "]");
                eventCount = 0;
                previousPath = "";
            }
            if (L)
                Log.v(TAG, "onEvent. in [" + this.minecraftWorldDirectory + "] path [" + path + "] event [" + getFileAction(event) + "]");
            previousEvent = event;
            previousPath = path;
        } else {
            eventCount++;
        }
    }

    private String getFileAction(int event) {
        String fileAction = null;
        switch (event) {
            case FileObserver.ACCESS:
                fileAction = "ACCESS";
                break;
            case FileObserver.ALL_EVENTS:
                fileAction = "ALL_EVENTS";
                break;
            case FileObserver.ATTRIB:
                fileAction = "ATTRIB";
                break;
            case FileObserver.CLOSE_NOWRITE:
                fileAction = "CLOSE_NOWRITE";
                break;
            case FileObserver.CLOSE_WRITE:
                fileAction = "CLOSE_WRITE";
                break;
            case FileObserver.CREATE:
                fileAction = "CREATE";
                break;
            case FileObserver.DELETE:
                fileAction = "DELETE";
                break;
            case FileObserver.DELETE_SELF:
                fileAction = "DELETE_SELF";
                break;
            case FileObserver.MODIFY:
                fileAction = "MODIFY";
                break;
            case FileObserver.MOVE_SELF:
                fileAction = "MOVE_SELF";
                break;
            case FileObserver.MOVED_FROM:
                fileAction = "MOVED_FROM";
                break;
            case FileObserver.MOVED_TO:
                fileAction = "MOVED_TO";
                break;
            case FileObserver.OPEN:
                fileAction = "OPEN";
                break;
        }
        return fileAction;
    }
}
