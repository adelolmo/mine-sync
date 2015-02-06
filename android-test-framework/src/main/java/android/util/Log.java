package android.util;

public class Log {

    public static int v(String tag, String msg) {
        return log(tag, "VERBOSE", msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return log(tag, "VERBOSE", msg, tr);
    }

    public static int d(String tag, String msg) {
        return log(tag, "DEBUG", msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return log(tag, "DEBUG", msg, tr);
    }

    public static int i(String tag, String msg) {
        return log(tag, "INFO", msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return log(tag, "INFO", msg, tr);
    }

    public static int w(String tag, String msg) {
        return log(tag, "WARN", msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return log(tag, "WARN", msg, tr);
    }

    public static int e(String tag, String msg) {
        return log(tag, "ERROR", msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return log(tag, "ERROR", msg, tr);
    }

    private static int log(String tag, String level, String msg) {
        return log(tag, level, msg, null);
    }

    private static int log(String tag, String level, String msg, Throwable tr) {
        System.out.println("[" + tag + "][" + level + "] " + msg);
        if (tr != null) {
            tr.printStackTrace();
        }
        return 0;
    }
}
