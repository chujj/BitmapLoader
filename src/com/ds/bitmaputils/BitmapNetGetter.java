package com.ds.bitmaputils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LogPrinter;
import android.util.Printer;

import com.ds.io.DsLog;
import com.ds.theard.WorkThread;

public class BitmapNetGetter {
	private static final boolean DEBUG_PERFORMANCE = false;
	private static BitmapNetGetter sInstance;
	protected static String sCacheDirPath;
	
	private WorkHandler mWorkHandler;
	private UIHandler mUIHandler;
	
	private HashMap<Object, Bitmap> mBitmapCache;
	private HashMap<Object, BitmapGotCallBack> mFetchTask;
	private static synchronized BitmapNetGetter getInstance() {
		if (sInstance == null) {
			sInstance = new BitmapNetGetter();
		}
		return sInstance;
	}
	
	private Printer mTheradprint;
	private BitmapNetGetter() {
		mWorkHandler = new WorkHandler();
		mUIHandler = new UIHandler();
		mBitmapCache = new HashMap<Object, Bitmap>();
		mFetchTask = new HashMap<Object, BitmapGotCallBack>();
		mTheradprint = new LogPrinter(android.util.Log.ERROR, "count-dump");
	}

	/** Call set the cache path. Makesure call at first of all!
	 * @param aDirPath
	 */
	public static void setCacheFileDir(String aDirPath) {
		sCacheDirPath = aDirPath;
	}
	
	/**
	 * Release the BitmapTask, all the member of that BitmapTask will released,
	 * should not use the BitmapTask's memeber anymore
	 * 
	 * @param aTask
	 */
	public static void releaseBitmap(BitmapTask aTask) {
		BitmapNetGetter instance = getInstance();
		Bitmap release = instance.mBitmapCache.get(aTask.getTaskKey());
		if (release != null) {
			release.recycle();
			instance.mBitmapCache.remove(aTask.getTaskKey());
		}
		getInstance().mFetchTask.remove(aTask.getTaskKey());
		instance.releasPending(aTask);
	}
	
	final private void releasPending(BitmapTask aTask) {
		mWorkHandler.removeCallbacksAndMessages(aTask);
	}
	
	/**
	 * if bitmap cached, return cached bitmap; or fetch it through network(will
	 * cache into local filesystem), the delay callback will called then
	 * 
	 * @param aTask
	 * @param aCallback
	 * @return the cached Bitmap
	 */
	public static Bitmap tryGetBitmapFromUrlOrCallback(BitmapTask aTask, BitmapGotCallBack aCallback) {
		
		
		Bitmap retval = getInstance().getCachedBitmap(aTask.getTaskKey());
		if (retval == null) {
			if (getInstance().mFetchTask.containsKey(aTask.getTaskKey())) {
				getInstance().mFetchTask.put(aTask.getTaskKey(), aCallback);
			} else {
				if (DEBUG_PERFORMANCE) {
					DsLog.e("zhujj: " + aTask.getTaskKey());
				}
				getInstance().fetchBitmapOnNet(aTask, aCallback);
			}
		}
		if (DEBUG_PERFORMANCE) {
			DsLog.e(">>>>>>>>>>>>>>>>>>>>>");
			getInstance().mWorkHandler.dump(getInstance().mTheradprint, "count-dump");
			DsLog.e("<<<<<<<<<<<<<<<<<<<<<");
		}
		return retval;
	}
	
	private Bitmap getCachedBitmap(Object aKey) {
		return mBitmapCache.get(aKey);
	}
	
	private void fetchBitmapOnNet(BitmapTask aTask, BitmapGotCallBack aCallBack) {
		mFetchTask.put(aTask.getTaskKey(), aCallBack);
		Message.obtain(getInstance().mWorkHandler,
				WorkHandler.MSG_FETCH_BITMAP, aTask).sendToTarget();
	}
	
	private static class WorkHandler extends Handler {
		public final static int MSG_FETCH_BITMAP = 0;
		public WorkHandler() {
			super(WorkThread.getsWorkLooper());
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FETCH_BITMAP:
				BitmapTask task = (BitmapTask) msg.obj;
				Bitmap bitmap = null;
				
				// read local
				bitmap = getBitmapFromFile(task);
				
				// LOG_OUT file or net
//				if (bitmap != null) {
//					DsLog.e("get FileCached " + task.getNetUrl());
//				} else {
//					DsLog.e("need Access Net " + task.getNetUrl());
//				}
				
				if (bitmap == null) {
					//remote
					bitmap = getBitmapFromNet(task.getNetUrl());
					if (bitmap != null) {
						String path2File = saveBitmapToFile(task, bitmap);
						task.saveFileSystemPath(path2File);
					}
				}
				getInstance().mBitmapCache.put(task.getTaskKey(), bitmap);
				Message.obtain(getInstance().mUIHandler, UIHandler.MSG_FETCH_BITMAP_DONE, task).sendToTarget();
				break;

			default:
				break;
			}
		}
		
		private String saveBitmapToFile(BitmapTask aTask, Bitmap aBitmap) {
			if (aBitmap == null) {
				return null;
			}
			
			return saveBitmap2Sdcard(sCacheDirPath, aBitmap);
		}
		private Bitmap getBitmapFromFile(BitmapTask aTask) {
			Bitmap retval = null;
			try {
				if (aTask.getFileSystemPath() != null) {
					retval = BitmapFactory.decodeFile(aTask.getFileSystemPath());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return retval;
		}
		
		private Bitmap getBitmapFromNet(String aUrl) {
			Bitmap retval = null;
			InputStream is = null;
			try {
				URL url = new URL(aUrl);
				is =  url.openStream();
				retval = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return retval;
		}
		
	}

	private static class UIHandler extends Handler {
		public final static int MSG_FETCH_BITMAP_DONE = 0;
		public UIHandler() {
			super(Looper.getMainLooper());
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FETCH_BITMAP_DONE:
				BitmapTask task = (BitmapTask) msg.obj;
				if (getInstance().mFetchTask.containsKey(task.getTaskKey())) {
					BitmapGotCallBack run = getInstance().mFetchTask.get(task.getTaskKey());
					getInstance().mFetchTask.remove(task.getTaskKey());
					if (run != null)
						run.onBitmapGot(getInstance().mBitmapCache.get(task.getTaskKey()));
				}
				break;

			default:
				break;
			}
		}
	}

	public interface BitmapGotCallBack {
		/** Call after we got the Bitmap
		 * @param aBitmap
		 */
		public void onBitmapGot(Bitmap aBitmap);
	}
	
	public interface BitmapTask {
		/**
		 * The uniq key, which should always same the different sessions. An
		 * immutable string recommended, or you should serilize the memory
		 * object
		 * 
		 * @return
		 */
		public Object getTaskKey();

		/**
		 * get URI as string
		 * 
		 * @return
		 */
		public String getNetUrl();

		/**
		 * the abs path, tell by last call of @saveFileSystemPath, or null
		 * 
		 * @return
		 */
		public String getFileSystemPath();

		/**
		 * maybe 403, but now is meanless
		 * 
		 * @param aUrl
		 */
		public void saveNetUrl(String aUrl);

		/**
		 * call then cached in local fs, the fs path given
		 * 
		 * @param aPath
		 */
		public void saveFileSystemPath(String aPath);
	}
	
	private static String saveBitmap2Sdcard(String aDirPath, Bitmap bitmap) {
		String filePath = null;
		if (aDirPath == null) {
			return filePath;
		}
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();

			long time = System.currentTimeMillis();
			filePath = aDirPath + "/" + time;
			FileOutputStream fos = new FileOutputStream(filePath);
			fos.write(out.toByteArray());
			fos.flush();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
			return filePath;
		}
		return filePath;
	}

}
