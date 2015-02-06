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

package org.ado.github.minesync.gui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import org.ado.github.minesync.commons.ALog;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.ado.github.minesync.config.AppConstants.MINECRAFT_SYNC_SHARE_PREFERENCES_NAME;

/**
 * Class for the management of app upgrades.
 *
 * @author andoni
 * @since 1.1.0
 */
public class UpgradeManager {

    private static final String TAG = UpgradeManager.class.getName();

    private Context context;
    private SharedPreferences sharedPreferences;

    public UpgradeManager(Context context) {
        this.context = context;
        this.sharedPreferences = context
                .getSharedPreferences(MINECRAFT_SYNC_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void upgradeIfNeeded() {
        try {
            int oldVersion = sharedPreferences.getInt("current.version", 3);
            int newVersion = context.getPackageManager().getPackageInfo("org.ado.minesync", 0).versionCode;
            ALog.i(TAG, "old version [" + oldVersion + "] new version [" + newVersion + "]");
            upgrade(oldVersion, newVersion);
            setCurrentVersion(newVersion);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot upgrade app.", e);
        }
    }

    private void upgrade(int oldVersion, int newVersion) {
        if (oldVersion == 3 && newVersion == 4) {
            try {
                File filesDir = context.getFilesDir();
                File[] cachedFileArray = filesDir.listFiles();
                if (cachedFileArray != null) {
                    for (File file : cachedFileArray) {
                        FileUtils.deleteQuietly(file);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Cannot upgrade from version 3 to version 4", e);
            }
        }
    }

    private void setCurrentVersion(int newVersion) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("current.version", newVersion);
        edit.commit();
    }
}