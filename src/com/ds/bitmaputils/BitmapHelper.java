package com.ds.bitmaputils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.ds.theard.WorkThread;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapHelper {
	public static int THUMBNAIL_WIDTH = 100;
	public static int THUMBNAIL_HEIGHT = 100;
	
	public static int FIT_SCREEN_WIDTH = 768;
	public static int FIT_SCREEN_HEIGHT = 1280;
	
	public static enum LEVEL {
		THUMBNAIL,
		FITSCREEN,
		ORIGIN,
	}
	
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
		mAtomBitmaps = new ArrayList<AtomBitmap>();
	}
	
	private HashMap<String , Cbitmap> mCbitmapMap;
	private ArrayList<AtomBitmap> mAtomBitmaps;

	protected void registeAtomBitmap(AtomBitmap abp) {
		mAtomBitmaps.add(abp);
	}
	
	/** String as key, ui thread only
	 * @param path
	 */
	public AtomBitmap getBitmap(String path) {
		return getBitmap(path, LEVEL.THUMBNAIL);
	}
	
	public AtomBitmap getBitmap(String path, LEVEL level) {
		Cbitmap c = mCbitmapMap.get(path);
		if (c == null) {
			c = new Cbitmap(this, path);
			mCbitmapMap.put(path, c);
		}
		
		return c.accessBitmap(level);
		
	}
	
	
	
	public int getSize() {
		return 0;
	}
	
	public void dumpAllCBitmaps() {
		
	}
	
	public String dumpAllAtomBitmaps() {
		StringBuilder sb = new StringBuilder();
		Collections.sort(mAtomBitmaps);
		for (int i = 0; i < mAtomBitmaps.size(); i++) {
			sb.append(mAtomBitmaps.get(i).dump());
			sb.append("\n");
		}
		return sb.toString();
	}
}
