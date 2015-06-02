package org.ado.atf;

import android.content.Context;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 20/10/13
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class AndroidTestFramework {

    private static AndroidManifest androidManifest;
    private static Context context;

    public static void clear() {
        try {
            FileUtils.cleanDirectory(Config.ANDROID_TEST_FRAMEWORK_DIR);
        } catch (Exception e) {
            System.out.println(Config.ANDROID_TEST_FRAMEWORK_DIR + " is already empty.");
        }
    }

    public static void init() throws IOException {
        clear();
        FileUtils.forceMkdir(Config.APPLICATION_FILES_DIR);
        FileUtils.forceMkdir(Config.APPLICATION_CACHE_DIR);
        FileUtils.forceMkdir(Config.SDCARD_DIR);
//        context = new Context(new PackageManager());
//        context= new MockContext();
        context = ContextFactory.createContext();
    }

    public static void setAndroidManifestFile(File manifestFile) {
        androidManifest = new AndroidManifest(manifestFile);
    }

    public static void addSharedPreference(String key, Object value) {
//        Context.addSharedPreference(key, value);
    }

    public static AndroidManifest getAndroidManifest() {
        return androidManifest;
    }

    public static Context getContext() {
        return context;
    }
}