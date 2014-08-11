/**
 * 
 */
package ds.android.utils;

import java.io.File;

/**
 * 常量
 * @author Tiger
 *
 */
public interface Constants {
	public static final String NAME = "yinote";
	
	public static final int NOTE_HIGHLIGHT_COLOR = 0x99FFFF00;
	public static final String NOTE_MONOSPACE_TYPEFACE = "monospace";
	public static final float NOTE_SIZE_SMALL_FACTOR = 0.8f;
	public static final float NOTE_SIZE_LARGE_FACTOR = 1.5f;
	public static final float NOTE_SIZE_HUGE_FACTOR = 1.8f;

	public static final String CONFIGURE_FILE = "config.properties";
	public static final String NEW_PACKAGE_SAVE_NAME = "yinote.apk";

    public static final String NOTE_DIR = "yinotes";
    public static final String DB_DIR = "db";
    public static final String RESOURCE_DIR = "temp" + File.separator + "res";
    public static final String IMAGE_DIR = RESOURCE_DIR;
    public static final String SOUND_DIR = RESOURCE_DIR;

    // 资源类型
    public static final int RES_TYPE_UNKNOW = 0;
    public static final int RES_TYPE_IMAGE = 1;
    public static final int RES_TYPE_SOUND = 2;
    
    // 更新方式
	public static final int NETWORK_CONFIRM = 1;
	public static final int UPGRADE_CONFIRM = 2;
	public static final int FORCED_UPGRADE = 3;

	public static final int IMAGE_SELECTED_RESULT = 100;
	public static final int CAMERA_SELECTED_RESULT = 101;
	public static final int RECORDER_SELECTED_RESULT = 102;
	public static final int INFO_RESULT = 103;
	public static final int IMAGE_EDIT_RESULT = 104;
	public static final int MAP_IMAGE_RESULT = 105;
	public static final int WEB_IMAGE_RESULT = 106;
	public static final int RECORDER_VIEW_RESULT = 107;
	public static final int HYPERLINK_RESULT = 108;
	public static final int DETAIL_MAP = 109;

	public static final String EXTRA_ID_URL = "EXTRA_ID_URL";
	public static final String EXTRA_ID_NEW_TAB = "EXTRA_ID_NEW_TAB";
	/**
	 * Specials urls.
	 */
	public static final String URL_ABOUT_BLANK = "about:blank";
	public static final String URL_ABOUT_START = "about:start";
	public static final String URL_ACTION_SEARCH = "action:search?q=";
	public static final String URL_GOOGLE_MOBILE_VIEW = "http://www.google.com/gwt/x?u=%s";
	public static final String URL_GOOGLE_MOBILE_VIEW_NO_FORMAT = "http://www.google.com/gwt/x?u=";
	
	/**
	 * Search urls.
	 */	
	public static String URL_SEARCH_GOOGLE = "http://www.google.com/search?ie=UTF-8&sourceid=navclient&gfns=1&q=%s";
	public static String URL_SEARCH_WIKIPEDIA = "http://en.wikipedia.org/w/index.php?search=%s&go=Go";
}
