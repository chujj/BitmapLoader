package ds.android.utils;

import android.util.Log;

import static java.text.MessageFormat.format;

public class TLog {

    public static void v(String tag, Throwable t, String msg, Object... args) {
        if (Config.IS_DEBUG) Log.v(tag, fmt(msg, args), t);
    }

    public static void v(String tag, String msg, Object... args) {
    	v(tag, null, msg, args);
    }

    public static void d(String tag, Throwable t, String msg, Object... args) {
        if (Config.IS_DEBUG) Log.i(tag, fmt(msg, args), t);
    }

    public static void d(String tag, String msg, Object... args) {
    	d(tag, null, msg, args);
    }

    public static void i(String tag, Throwable t, String msg, Object... args) {
    	Log.i(tag, fmt(msg, args), t);
    }

    public static void i(String tag, String msg, Object... args) {
        i(tag, null, msg, args);
    }

    public static void w(String tag, Throwable t, String msg, Object... args) {
    	Log.w(tag, fmt(msg, args), t);
    }

    public static void w(String tag, String msg, Object... args) {
        w(tag, null, msg, args);
    }

    public static void e(String tag, Throwable t, String msg, Object... args) {
    	Log.e(tag, fmt(msg, args), t);
    }

    public static void e(String tag, String msg, Object... args) {
    	e(tag, null, msg, args);
    }
    private static String fmt(String msg, Object... args) {
    	return args == null || args.length == 0? msg : format(msg, args);
    }
}
