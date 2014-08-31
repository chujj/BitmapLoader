package com.ds.bitmaputils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	public static int DECODE_ORIGIN_SIZE = -1;
	private static BitmapNetGetter sInstance;
	protected static String sCacheDirPath;
	
	
	private WorkHandler mWorkHandler;
	private UIHandler mUIHandler;
	
	private HashMap<Object, Bitmap> mBitmapCache;
	private HashMap<Object, BitmapGotCallBack> mFetchTask;
	private Bitmap mError;
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
		mError = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		mError.eraseColor(0xff0000ff);
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
		if (release != null && release != instance.mError) {
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
	
	public static void tryCancelTask(BitmapTask aTask) {
		getInstance().mWorkHandler.removeMessages(WorkHandler.MSG_FETCH_BITMAP, aTask);
		getInstance().mFetchTask.remove(aTask.getTaskKey());
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
				if (task.useLocalStreamCache()) {
					bitmap = task.decodeFromLocalStream();
				} else {
					bitmap = getBitmapFromFile(task);
				}
				
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
						if (task.saveBitmapByTaskself()) {
							task.saveBitmap(bitmap);
						} else {
							String path2File = saveBitmapToFile(task, bitmap);
							task.saveFileSystemPath(path2File);
						}
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
					if (aTask.getBitmapMaxWidth() != DECODE_ORIGIN_SIZE && 
							aTask.getBitmapMaxHeight() != DECODE_ORIGIN_SIZE)
					{
						try {
							retval = decodeFileWithMaxSize(
									aTask.getFileSystemPath(),
									aTask.getBitmapMaxWidth(),
									aTask.getBitmapMaxHeight());
							if (retval == null) {
								retval = sInstance.mError;
							}
						} catch (OutOfMemoryError e) {
							BitmapHelper.getInstance(null).recycleBitmaps();
							retval = null;
						}
					} else { // origin
						retval = BitmapFactory.decodeFile(aTask.getFileSystemPath());
					}
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
	
	private static Bitmap decodeFileWithMaxSize(String filepath, int max_width, int max_height) {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inScaled = false;
		op.inJustDecodeBounds = true;
		Bitmap retval = null;
		try {
			InputStream is = new FileInputStream(filepath);
			BitmapFactory.decodeStream(is, null, op);
			is.close();

			int sample = Math.max(1, (op.outWidth + op.outHeight ) / (max_width + max_height));
			op.inSampleSize = sample;
			op.inJustDecodeBounds = false;
			retval = BitmapFactory.decodeStream(new FileInputStream(filepath), null, op);
		} catch (OutOfMemoryError e) {
			throw e;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		catch (Exception e) {
//			e.printStackTrace();
//			// ZHUJJ-TODO handle exception, should call task error occur
//		}
		return retval;
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
				BitmapGotCallBack run = getInstance().mFetchTask.remove(task.getTaskKey());
				if (run != null) {
						run.onBitmapGot(getInstance().mBitmapCache.get(task.getTaskKey()));
				}
				break;

			default:
				break;
			}
		}
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
