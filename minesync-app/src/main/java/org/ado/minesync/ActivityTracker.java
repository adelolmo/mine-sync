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

package org.ado.minesync;

import android.content.Context;
import android.content.SharedPreferences;
import org.ado.minesync.commons.ALog;

import static org.ado.minesync.config.AppConstants.*;

/**
 * This class keeps control of the activities done by the application.
 *
 * @author andoni
 * @since 04.01.2014
 */
public class ActivityTracker {

    private static final String TAG = ActivityTracker.class.getName();

    private Context context;
    private SharedPreferences sharedPreferences;

    public ActivityTracker(Context context) {
        this.context = context;
        this.sharedPreferences = this.context
                .getSharedPreferences(MINECRAFT_SYNC_SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void setConfigurationProcess(boolean finished) {
        ALog.d(TAG, "setConfigurationProcess [%s]", finished);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHARE_CONFIGURATION_PROCESS_FINISHED, finished);
        editor.commit();
    }

    public boolean isConfigurationProcessFinished() {
        boolean configurationProcessFinished = this.sharedPreferences.getBoolean(SHARE_CONFIGURATION_PROCESS_FINISHED, false);
        ALog.d(TAG, "isConfigurationProcessFinished? [%s]", configurationProcessFinished);
        return configurationProcessFinished;
    }

    public void setNeedToShowConfigurationProcessFinishedDialog(boolean needToShowDialog) {
        ALog.d(TAG, "setNeedToShowConfigurationProcessFinishedDialog [%s]", needToShowDialog);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NEED_TO_SHOW_CONFIGURATION_PROCESS_FINISHED_DIALOG, needToShowDialog);
        editor.commit();
    }

    public boolean isShowConfigurationProcessFinishedDialogNeeded() {
        return this.sharedPreferences.getBoolean(NEED_TO_SHOW_CONFIGURATION_PROCESS_FINISHED_DIALOG, false);
    }

    private SharedPreferences.Editor getEditor() {
        return this.sharedPreferences.edit();
    }
}
