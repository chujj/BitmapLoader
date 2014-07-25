package com.ds.bitmaputils;

import com.ds.bitmaputils.BitmapHelper.LEVEL;

public class Cbitmap {

	
	private String key;
	
	private AtomBitmap mThumbNailAtomBitmap, mFitScreenAtomBitmap, mOriginAtomBitmap;
	private BitmapHelper mHelper;
	public Cbitmap(BitmapHelper aHalper, String path) {
		key = path; 
		mHelper = aHalper;
	}

	public AtomBitmap accessBitmap(LEVEL level) {
		if (level == LEVEL.THUMBNAIL) {
			if (mThumbNailAtomBitmap == null) {
				mThumbNailAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapHelper.THUMBNAIL_WIDTH, BitmapHelper.THUMBNAIL_HEIGHT);
			}
			return mThumbNailAtomBitmap;
		} else if (level == LEVEL.FITSCREEN) {
			if (mFitScreenAtomBitmap == null) {
				mFitScreenAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapHelper.FIT_SCREEN_WIDTH, BitmapHelper.FIT_SCREEN_HEIGHT);
			}
			return mFitScreenAtomBitmap;
		} else if (level == LEVEL.ORIGIN) {
			if (mOriginAtomBitmap == null) {
				mOriginAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapNetGetter.DECODE_ORIGIN_SIZE, BitmapNetGetter.DECODE_ORIGIN_SIZE);
			}
			return mOriginAtomBitmap;
		} else {
			throw new RuntimeException("wrong level" + level);
		}
	}
	
//	private AtomBitmap fallbackFindAtomBitmap(LEVEL level) {
//		
//	}
}
