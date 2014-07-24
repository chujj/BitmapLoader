package com.ds.bitmaputils;

import com.ds.theard.WorkThread;

import android.content.Context;

public class BitmapHelper {
	private static BitmapHelper sInstance;
	public static synchronized BitmapHelper getInstance(Context context) {
		if (null == sInstance) {
			WorkThread.init();
			if (BitmapGetter.sCacheDirPath != null) {
				BitmapGetter.setCacheFileDir(context.getFilesDir().getAbsolutePath());
			}
			sInstance  = new BitmapHelper();
		}
		
		return sInstance;
	}

	/** String as key
	 * @param path
	 */
	public void loadBitmap(String path) {
		
	}
	
	public int getSize() {
		return 0;
	}
	
	public void dumpAllCBitmaps() {
		
	}
	
	public void dumpAllAtomBitmaps() {
		
	}
}
