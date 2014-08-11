package ds.android.utils;

import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;

public class StringUtils {
	
	/**
     * The empty String {@code ""}.
     * @since 2.0
     */
    public static final String EMPTY = "";
	
	public static String getTruncateEndString(String aSrcString, Paint aPaint, int aWidth) {
		if (aSrcString == null) {
			return "";
		}
		return TextUtils.ellipsize(aSrcString, new TextPaint(aPaint), aWidth, TextUtils.TruncateAt.END)
				.toString();
	}
	
	public static String getBucketPath(String fullPath,String fileName){	
		
		return fullPath.substring(0, fullPath.lastIndexOf(fileName));
		
	}
	
	public static boolean isNull(String str) {
		if (str == null || str.trim().length() == 0 || "null".equals(str)) {
			return true;
		}
		return false;
	}
	
	public static boolean isNotBlank(String str) {
		return !StringUtils.isNull(str) && str.trim().length() > 0;
	}
	
	/**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end position,
     *  {@code null} if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
	
}
