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

package android.content;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import org.ado.atf.Config;

import java.io.File;

/**
 * Class description here.
 *
 * @author andoni
 * @since 14.03.2014
 */
public class Context {

    private PackageManager packageManager;
    private static DefaultSharedPreferences sharedPreferences = new DefaultSharedPreferences();

    public Context(PackageManager packageManager) {
        this.packageManager = new PackageManager();
    }

    public static void addSharedPreference(String key, Object value) {
        sharedPreferences.put(key, value);
    }

    public File getFilesDir() {
        return Config.APPLICATION_FILES_DIR;
    }

    public File getCacheDir(){
        return Config.APPLICATION_CACHE_DIR;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }

    public SharedPreferences getSharedPreferences(String name, int mode) {
        return sharedPreferences;
    }

    public Context getApplicationContext() {
        return this;
    }

    public ComponentName startService(Intent service) {
        return null;
    }

    public boolean stopService(Intent service) {
        return true;
    }

    public Resources getResources() {
        return null;
    }

    public void startActivity(Intent intent){

    }
    public void sendBroadcast(Intent intent){

    }

    public Object getSystemService(String name){
        return null;
    }

    public static final int MODE_PRIVATE = 0;
    public static final int MODE_WORLD_READABLE = 1;
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final int MODE_APPEND = 32768;
    public static final int MODE_MULTI_PROCESS = 4;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8;
    public static final int BIND_AUTO_CREATE = 1;
    public static final int BIND_DEBUG_UNBIND = 2;
    public static final int BIND_NOT_FOREGROUND = 4;
    public static final int BIND_ABOVE_CLIENT = 8;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 16;
    public static final int BIND_WAIVE_PRIORITY = 32;
    public static final int BIND_IMPORTANT = 64;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 128;
    public static final String POWER_SERVICE = "power";
    public static final String WINDOW_SERVICE = "window";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String ACCOUNT_SERVICE = "account";
    public static final String ACTIVITY_SERVICE = "activity";
    public static final String ALARM_SERVICE = "alarm";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String ACCESSIBILITY_SERVICE = "accessibility";
    public static final String KEYGUARD_SERVICE = "keyguard";
    public static final String LOCATION_SERVICE = "location";
    public static final String SEARCH_SERVICE = "search";
    public static final String SENSOR_SERVICE = "sensor";
    public static final String STORAGE_SERVICE = "storage";
    public static final String WALLPAPER_SERVICE = "wallpaper";
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String WIFI_SERVICE = "wifi";
    public static final String WIFI_P2P_SERVICE = "wifip2p";
    public static final String NSD_SERVICE = "servicediscovery";
    public static final String AUDIO_SERVICE = "audio";
    public static final String MEDIA_ROUTER_SERVICE = "media_router";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String CLIPBOARD_SERVICE = "clipboard";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final String DROPBOX_SERVICE = "dropbox";
    public static final String DEVICE_POLICY_SERVICE = "device_policy";
    public static final String UI_MODE_SERVICE = "uimode";
    public static final String DOWNLOAD_SERVICE = "download";
    public static final String NFC_SERVICE = "nfc";
    public static final String USB_SERVICE = "usb";
    public static final String INPUT_SERVICE = "input";
    public static final int CONTEXT_INCLUDE_CODE = 1;
    public static final int CONTEXT_IGNORE_SECURITY = 2;
    public static final int CONTEXT_RESTRICTED = 4;
}