package com.ds.bitmaputils;

import android.graphics.Bitmap;

public class Cbitmap {
	public static enum LEVEL {
		THUMBNAIL,
		FITSCREEN,
		ORIGIN,
	}
	
	private String key;
	
	private AtomBitmap mThumbNailAtomBitmap;
	
	public Cbitmap(String path) {
		key = path; 
	}

	public AtomBitmap accessBitmap() {
		return accessBitmap(LEVEL.THUMBNAIL);
	}
	
	public AtomBitmap accessBitmap(LEVEL level) {
		if (mThumbNailAtomBitmap == null) {
			mThumbNailAtomBitmap = new AtomBitmap(key, BitmapHelper.THUMBNAIL_WIDTH, BitmapHelper.THUMBNAIL_HEIGHT);
		}
		return mThumbNailAtomBitmap;
	}
	
//	private AtomBitmap fallbackFindAtomBitmap(LEVEL level) {
//		
//	}
}
