package org.ado.atf;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: andoni
 * Date: 20/10/13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class Config {

    public static final File ANDROID_TEST_FRAMEWORD_DIR = new File(new File(System.getProperty("java.io.tmpdir")), "android-test-framework");
    public static final File SDCARD_DIR = new File(ANDROID_TEST_FRAMEWORD_DIR, "sdcard");
    public static final File APPLICATION_DIR = new File(ANDROID_TEST_FRAMEWORD_DIR, "data/data/application");
    public static final File APPLICATION_FILES_DIR = new File(APPLICATION_DIR, "files");
    public static final File APPLICATION_CACHE_DIR = new File(APPLICATION_DIR, "cache");
}
