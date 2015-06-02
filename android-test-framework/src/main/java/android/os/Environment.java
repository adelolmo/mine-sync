package android.os;

import static org.ado.atf.Config.ANDROID_TEST_FRAMEWORK_DIR;
import static org.ado.atf.Config.SDCARD_DIR;

public class Environment {

    static {
        ANDROID_TEST_FRAMEWORK_DIR.mkdirs();
        SDCARD_DIR.mkdirs();
    }

    public static java.io.File getExternalStorageDirectory() {
        return SDCARD_DIR;
    }
}
