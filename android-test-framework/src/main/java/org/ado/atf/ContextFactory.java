package org.ado.atf;

import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;

import java.io.*;

/**
 * @author Andoni del Olmo
 * @since 31.05.15
 */
public class ContextFactory {

    private static PackageManager packageManager;
    private static DefaultSharedPreferences sharedPreferences = new DefaultSharedPreferences();

    public static Context createContext() {
//        packageManager = new PackageManager();
        packageManager = new MyPackageManager();
        return new MyContext();
    }

    public static void addSharedPreference(String key, Object value) {
        sharedPreferences.put(key, value);
    }

    private static class MyContext extends Context {

        public AssetManager getAssets() {
            return null;
        }


        public Resources getResources() {
            return null;
        }


        public PackageManager getPackageManager() {
            return packageManager;
        }


        public ContentResolver getContentResolver() {
            return null;
        }


        public Looper getMainLooper() {
            return null;
        }


        public Context getApplicationContext() {
            return null;
        }


        public void setTheme(int resid) {

        }


        public Resources.Theme getTheme() {
            return null;
        }


        public ClassLoader getClassLoader() {
            return null;
        }


        public String getPackageName() {
            return null;
        }


        public ApplicationInfo getApplicationInfo() {
            return null;
        }


        public String getPackageResourcePath() {
            return null;
        }


        public String getPackageCodePath() {
            return null;
        }


        public SharedPreferences getSharedPreferences(String name, int mode) {
            return sharedPreferences;
        }


        public FileInputStream openFileInput(String name) throws FileNotFoundException {
            return null;
        }


        public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
            return null;
        }


        public boolean deleteFile(String name) {
            return false;
        }


        public File getFileStreamPath(String name) {
            return null;
        }


        public File getFilesDir() {
            return Config.APPLICATION_FILES_DIR;
        }


        public File getNoBackupFilesDir() {
            return null;
        }


        public File getExternalFilesDir(String type) {
            return null;
        }


        public File[] getExternalFilesDirs(String type) {
            return new File[0];
        }


        public File getObbDir() {
            return null;
        }


        public File[] getObbDirs() {
            return new File[0];
        }


        public File getCacheDir() {
            return Config.APPLICATION_CACHE_DIR;
        }


        public File getCodeCacheDir() {
            return null;
        }


        public File getExternalCacheDir() {
            return null;
        }


        public File[] getExternalCacheDirs() {
            return new File[0];
        }


        public File[] getExternalMediaDirs() {
            return new File[0];
        }


        public String[] fileList() {
            return new String[0];
        }


        public File getDir(String name, int mode) {
            return null;
        }

        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            return null;
        }

        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return null;
        }


        public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
            return null;
        }


        public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return null;
        }


        public boolean deleteDatabase(String name) {
            return false;
        }


        public File getDatabasePath(String name) {
            return null;
        }


        public String[] databaseList() {
            return new String[0];
        }


        public Drawable getWallpaper() {
            return null;
        }


        public Drawable peekWallpaper() {
            return null;
        }


        public int getWallpaperDesiredMinimumWidth() {
            return 0;
        }


        public int getWallpaperDesiredMinimumHeight() {
            return 0;
        }


        public void setWallpaper(Bitmap bitmap) throws IOException {

        }


        public void setWallpaper(InputStream data) throws IOException {

        }


        public void clearWallpaper() throws IOException {

        }


        public void startActivity(Intent intent) {

        }


        public void startActivity(Intent intent, Bundle options) {

        }


        public void startActivities(Intent[] intents) {

        }


        public void startActivities(Intent[] intents, Bundle options) {

        }


        public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {

        }


        public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {

        }


        public void sendBroadcast(Intent intent) {

        }


        public void sendBroadcast(Intent intent, String receiverPermission) {

        }


        public void sendOrderedBroadcast(Intent intent, String receiverPermission) {

        }


        public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {

        }


        public void sendBroadcastAsUser(Intent intent, UserHandle user) {

        }


        public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {

        }


        public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {

        }


        public void sendStickyBroadcast(Intent intent) {

        }


        public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {

        }


        public void removeStickyBroadcast(Intent intent) {

        }


        public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {

        }


        public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {

        }


        public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {

        }


        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
            return null;
        }


        public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
            return null;
        }


        public void unregisterReceiver(BroadcastReceiver receiver) {

        }


        public ComponentName startService(Intent service) {
            return null;
        }


        public boolean stopService(Intent service) {
            return false;
        }


        public boolean bindService(Intent service, ServiceConnection conn, int flags) {
            return false;
        }


        public void unbindService(ServiceConnection conn) {

        }


        public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
            return false;
        }


        public Object getSystemService(String name) {
            return null;
        }


        public int checkPermission(String permission, int pid, int uid) {
            return 0;
        }


        public int checkCallingPermission(String permission) {
            return 0;
        }


        public int checkCallingOrSelfPermission(String permission) {
            return 0;
        }


        public void enforcePermission(String permission, int pid, int uid, String message) {

        }


        public void enforceCallingPermission(String permission, String message) {

        }


        public void enforceCallingOrSelfPermission(String permission, String message) {

        }


        public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {

        }


        public void revokeUriPermission(Uri uri, int modeFlags) {

        }


        public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
            return 0;
        }


        public int checkCallingUriPermission(Uri uri, int modeFlags) {
            return 0;
        }


        public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
            return 0;
        }


        public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags) {
            return 0;
        }


        public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {

        }


        public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {

        }


        public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {

        }


        public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid, int modeFlags, String message) {

        }


        public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
            return null;
        }


        public Context createConfigurationContext(Configuration overrideConfiguration) {
            return null;
        }


        public Context createDisplayContext(Display display) {
            return null;
        }
    }

    private static class MyPackageManager extends PackageManager {
        private PackageInfo packageInfo;

        public MyPackageManager() {
            packageInfo = new PackageInfo();
            packageInfo.packageName = AndroidTestFramework.getAndroidManifest().getPackage();
            packageInfo.versionCode = AndroidTestFramework.getAndroidManifest().getVersionCode();
        }

        public PackageInfo getPackageInfo(String packageName, int flags)
                throws PackageManager.NameNotFoundException {
//        FileUtils.copyInputStreamToFile(PackageManager.class.getResourceAsStream(""));
//        PackageManager.class.
            return packageInfo;
        }

        public PackageInstaller getPackageInstaller() {
            return null;
        }
    }
}