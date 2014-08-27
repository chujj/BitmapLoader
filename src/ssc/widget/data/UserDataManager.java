package ssc.widget.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;

public class UserDataManager {
	final static int userID = 13189445;
	final static String boardsUrl = "http://api.huaban.com/users/" + userID + "/boards";
	
	private static UserDataManager sInstance;

	public synchronized static void init(Context context) {
		if (sInstance != null) throw new RuntimeException("multi init");
		
		sInstance = new UserDataManager(context);
	}
	
	public static UserDataManager getInstance() {
		return sInstance;
	}
	
	
	private UserDataManager(Context context) {
		UserDataGroup localcache_result = loadLocalUserBoardsCache(context); // boards

		UserDataGroup net_result = queryNetUserBoards(context);
		if (net_result.newerThan(localcache_result)) {
			localcache_result.mergeNetData(net_result);
			mDataController = localcache_result;
			writeToLocalCache(context, localcache_result);
		} else {
			mDataController = localcache_result; // notheng
		}
	}
	

	private UserDataGroup queryNetUserBoards(Context listActivity) {
		String data = queryNet(boardsUrl);
		DataParser praser = DataProviderFactor.getParser();
		return praser.parser(data);
	}

	private UserDataGroup loadLocalUserBoardsCache(Context context) {
		File cache = getCacheFile(context);
		DataParser dp = DataProviderFactor.getParser();
		return dp.parser(cache);
	}

	private void writeToLocalCache(Context context, UserDataGroup localcache_result) {
		File cache = getCacheFile(context);
		localcache_result.writeToFile(cache);
	}

	private File getCacheFile(Context context) {
		String localfile = "cache";
		File cache = new File(context.getFilesDir().getAbsolutePath() + File.separator + localfile);
		return cache;
	}

	public void queryOlderResource() {

	}

	public void queryNewerResource() {

	}

	private static String queryNet(String aUrl) {
		ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
		int BUFFER_LENGTH = 500;
		byte[] buffer = new byte[BUFFER_LENGTH];

		String retval = null;
		try {
			mBuffer.reset();
			URL test = new URL(aUrl);
			InputStream is = test.openStream ();

			int read;
			while ((read = is.read(buffer, 0, BUFFER_LENGTH)) != -1) {
				mBuffer.write(buffer, 0, read);
			}

			mBuffer.flush();
			is.close ();

			retval = mBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retval;
	}	
}
