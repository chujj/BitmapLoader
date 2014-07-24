package com.ds.bitmaputils;

import java.util.HashMap;

import com.ds.theard.WorkThread;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapHelper {
	public static int THUMBNAIL_WIDTH = 100;
	public static int THUMBNAIL_HEIGHT = 100;
	
	public static int FIT_SCREEN_WIDTH = 768;
	public static int FIT_SCREEN_HEIGHT = 1280;
	
	private static BitmapHelper sInstance;
	public static synchronized BitmapHelper getInstance(Context context) {
		if (null == sInstance) {
			sInstance  = new BitmapHelper(context);
		}
		
		return sInstance;
	}

	private BitmapHelper(Context context) {
		WorkThread.init();
		if (BitmapNetGetter.sCacheDirPath != null) {
			BitmapNetGetter.setCacheFileDir(context.getFilesDir().getAbsolutePath());
		}
		mCbitmapMap = new HashMap<String, Cbitmap>();
	}
	
	private HashMap<String , Cbitmap> mCbitmapMap;

	
	/** String as key, ui thread only
	 * @param path
	 */
	public AtomBitmap getBitmap(String path) {
		Cbitmap c = mCbitmapMap.get(path);
		if (c == null) {
			c = new Cbitmap(path);
			mCbitmapMap.put(path, c);
		}

		return c.accessBitmap();
	}
	
	
	
	public int getSize() {
		return 0;
	}
	
	public void dumpAllCBitmaps() {
		
	}
	
	public void dumpAllAtomBitmaps() {
		
	}
}
