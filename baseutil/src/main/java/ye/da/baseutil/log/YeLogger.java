package ye.da.baseutil.log;

import android.util.Log;

/**
 * @author ChenYe
 */
public class YeLogger {

    /**
     * 设为false关闭日志
     */
    public static boolean LOG_ENABLE = false;

    private YeLogger() {

    }

    public static void i(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.i(tag, msg == null ? "null" : msg);
        }
    }

    public static void v(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.v(tag, msg == null ? "null" : msg);
        }
    }

    public static void d(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.d(tag, msg == null ? "null" : msg);
        }
    }

    public static void w(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.w(tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.e(tag, msg == null ? "null" : msg);
        }
    }
}
