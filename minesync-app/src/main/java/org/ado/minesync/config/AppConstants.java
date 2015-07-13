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

package org.ado.minesync.config;

public class AppConstants {

    public static final boolean L = true;

    public static final String MINECRAFT_SYNC_SHARE_PREFERENCES_NAME = "MinecraftSync";
    public static final String SHARE_CONFIGURATION_PROCESS_FINISHED = "configFinished";
    public static final String NEED_TO_SHOW_CONFIGURATION_PROCESS_FINISHED_DIALOG = "showConfigFinishedDialog";

    public static final String MINECRAFT_APP_PACKAGE = "com.mojang.minecraftpe";

    public static final String INTENT_DROPBOX_ACCOUNT = "org.ado.minesync.DROPBOX_ACCOUNT";
    public static final String INTENT_FOREGROUND_APP = "org.ado.minesync.FOREGROUND_APP";

    public static final String INTENT_PARAMETER_FOREGROUND_APP = "foreground_app";
    public static final String INTENT_PARAMETER_ACCOUNT_STATUS = "account_status";
    public static final String INTENT_PARAMETER_FOREGROUND_WATCHER_ENABLE = "foreground_watcher_enable";
    public static final String INTENT_PARAMETER_MINESYNC_UPLOADING_WORLD = "org.ado.minesync.uploading_world";

    public static final String INTENT_PARAMETER_VALUE_LINKED = "linked";
    public static final String INTENT_PARAMETER_VALUE_UNLINKED = "unlinked";

    // Notifications
    public static final int NOTIFICATION_SYNC = 1;
    public static final int NOTIFICATION_CONFIGURATION = 2;

    public static final String WORLDS_JSON_FILENAME = ".worlds.json";
}