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

package org.ado.minesync.gui;

import android.app.Activity;
import com.dropbox.sync.android.DbxAccountManager;
import org.ado.minesync.commons.ALog;
import org.ado.minesync.config.AppConfiguration;
import org.ado.minesync.dropbox.DropboxFileManager;
import org.ado.minesync.minecraft.MinecraftData;

/**
 * Class description here.
 *
 * @author andoni
 * @since 09.03.2014
 */
public class MinecraftDropboxStatus {

    private static final String TAG = MinecraftDropboxStatus.class.getName();

    private MinecraftData minecraftData;
    private DropboxFileManager dropboxFileManager;

    public MinecraftDropboxStatus(Activity activity) {
        this.minecraftData = new MinecraftData();
        this.dropboxFileManager = new DropboxFileManager(getDropboxAccountManager(activity), activity.getCacheDir());
    }

    public MinecraftDropboxStatusEnum getStatus() {
        ALog.i(TAG, "Initialize app... isDropboxEmpty [%s] isMinecraftEmpty [%s].",
                this.dropboxFileManager.isDropboxEmpty(), this.minecraftData.getWorlds().isEmpty());
        if (!this.dropboxFileManager.isDropboxEmpty() && !this.minecraftData.getWorlds().isEmpty()) {
            return MinecraftDropboxStatusEnum.LOCAL_AND_DROPBOX;

        } else if (!this.dropboxFileManager.isDropboxEmpty() && this.minecraftData.getWorlds().isEmpty()) {
            return MinecraftDropboxStatusEnum.DROPBOX_ONLY;

        } else if (this.dropboxFileManager.isDropboxEmpty() && !this.minecraftData.getWorlds().isEmpty()) {
            return MinecraftDropboxStatusEnum.LOCAL_ONLY;

        } else {
            ALog.d(TAG, "no initialization needed.");
            return MinecraftDropboxStatusEnum.NONE;
        }
    }

    private DbxAccountManager getDropboxAccountManager(Activity activity) {
        return AppConfiguration.getDropboxAccountManager(activity.getApplicationContext());
    }
}
