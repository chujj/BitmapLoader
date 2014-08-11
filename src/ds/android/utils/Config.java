/**
 * 
 */
package ds.android.utils;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;

/**
 * @author Tiger
 *
 */
public class Config {
	private static Config config;
	private static Object monitor = new Object();
	
	public static String APK_INFO_URL;
	public static String LOGIN_URL;
	public static String SERVER_URL;
	public static String BMAP_KEY;
	public static String AUTH_KEY;
	
	public static String SYNC_SERVER_IP;
	public static int SYNC_SERVER_PORT;
	
	public static boolean IS_DEBUG = false;
	
	public static String serviceUri(String path) {
		return "http://"+SYNC_SERVER_IP+":"+SYNC_SERVER_PORT+"/"+path;
	}
	
	protected Config(Context context) {
		Properties props = new Properties();
		try {
			props.load(context.getAssets().open(Constants.CONFIGURE_FILE));
			
			SERVER_URL = props.getProperty("SERVER_URL");
			LOGIN_URL = props.getProperty("login_url");
			APK_INFO_URL = props.getProperty("apkinfo_url");
			IS_DEBUG = Boolean.valueOf(props.getProperty("debug", "false"));
			
			SYNC_SERVER_IP = props.getProperty("sync_ip");
			SYNC_SERVER_PORT = Integer.parseInt(props.getProperty("sync_port"));
			
			BMAP_KEY = props.getProperty("baidu_map_key");
			AUTH_KEY = props.getProperty("auth_key");
		} catch (IOException e) {
			throw new RuntimeException("配置文件加载失败...");
		}
	}

	public static void init(Context context) {
		if (config == null) {
			synchronized(monitor) {
				if (config == null) {
					config = new Config(context);
				}
			}
		}
	}
}
