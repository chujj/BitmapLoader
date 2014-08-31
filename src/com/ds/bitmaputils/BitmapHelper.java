package com.ds.bitmaputils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.ds.bitmaputils.Cbitmap.CustomBuildAtomBitmapFactory;
import com.ds.theard.WorkThread;

import android.content.Context;
import android.graphics.Bitmap;

public class BitmapHelper {
	public static int THUMBNAIL_WIDTH = 300;
	public static int THUMBNAIL_HEIGHT = 300;
	
	public static int FIT_SCREEN_WIDTH = 768;
	public static int FIT_SCREEN_HEIGHT = 1280;
	
	public static enum LEVEL {
		THUMBNAIL,
		FITSCREEN,
		ORIGIN,
	}
	
	protected static BitmapHelper sInstance;
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
		Cbitmap c = getCbitmap(path, false, null, null);

		return c.accessBitmap(level);
	}
	
	public AtomBitmap getBitmap(String path, LEVEL level, boolean custombuild, CustomBuildAtomBitmapFactory factory, Object userData) {
		Cbitmap c = getCbitmap(path, custombuild, factory, userData);

		return c.accessBitmap(level);
	}

	private Cbitmap getCbitmap(String path, boolean custombuild, CustomBuildAtomBitmapFactory factory, Object userData) {
		Cbitmap c = mCbitmapMap.get(path);
		if (c == null) {
			c = new Cbitmap(this, path, custombuild, factory, userData);
			mCbitmapMap.put(path, c);
		}	
		return c;
	}
	public int getSize() {
		return 0;
	}
	
	public void dumpAllCBitmaps() {
		
	}
	
	private static final long SIZE_LIMIT = 2000 * 2000;
	public void recycleBitmaps() {
		Collections.sort(mAtomBitmaps);
		int total_size = 0;
		long this_size = 0;
		for (int i = 0; i < mAtomBitmaps.size(); i++) {
			if (total_size > SIZE_LIMIT) {
				mAtomBitmaps.get(i).recycle();
				continue;
			}
			total_size += mAtomBitmaps.get(i).getSize();
		}
	}
	
	public String dumpAllAtomBitmaps() {
		StringBuilder sb = new StringBuilder();
		Collections.sort(mAtomBitmaps);
//		Collections.sort(mAtomBitmaps, new Comparator<AtomBitmap>() {
//
//			@Override
//			public int compare(AtomBitmap arg0, AtomBitmap arg1) {
//				int c = 0;
//				c = arg0.lastAccessTime
//				return 0;
//			}
//		});
		
		for (int i = 0; i < mAtomBitmaps.size(); i++) {
			sb.append("<tr>\n");
			sb.append(mAtomBitmaps.get(i).dump());
			sb.append("</tr>\n");
			
		}
		return sb.toString();
	}
}
