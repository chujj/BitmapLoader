package com.ds.bitmaputils;

import com.ds.bitmaputils.BitmapHelper.LEVEL;

public class Cbitmap {

	
	private String key;
	
	private AtomBitmap mThumbNailAtomBitmap, mFitScreenAtomBitmap, mOriginAtomBitmap;
	private BitmapHelper mHelper;
	private boolean mIsCustomBuild;
	private CustomBuildAtomBitmapFactory mABitmapFactory;
	private Object mUserData;
	public Cbitmap(BitmapHelper aHalper, String path) {
		this(aHalper, path, false, null, null);
	}
	
	public Cbitmap(BitmapHelper aHalper, String path, boolean custombuild, CustomBuildAtomBitmapFactory factory, Object userData) {
		mIsCustomBuild = custombuild;
		mABitmapFactory = factory;
		key = path;
		mHelper = aHalper;
		mUserData = userData;
	}

	public AtomBitmap accessBitmap(LEVEL level) {
		if (level == LEVEL.THUMBNAIL) {
			if (mThumbNailAtomBitmap == null) {
				if (mIsCustomBuild) {
					mThumbNailAtomBitmap = mABitmapFactory.buildAtomBitmap(level, mUserData);
				} else {
					mThumbNailAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapHelper.THUMBNAIL_WIDTH, BitmapHelper.THUMBNAIL_HEIGHT);
				}
			}
			return mThumbNailAtomBitmap;
		} else if (level == LEVEL.FITSCREEN) {
			if (mFitScreenAtomBitmap == null) {
				if (mIsCustomBuild) {
					mFitScreenAtomBitmap = mABitmapFactory.buildAtomBitmap(level, mUserData);
				} else {
					mFitScreenAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapHelper.FIT_SCREEN_WIDTH, BitmapHelper.FIT_SCREEN_HEIGHT);
				}
			}
			return mFitScreenAtomBitmap;
		} else if (level == LEVEL.ORIGIN) {
			if (mOriginAtomBitmap == null) {
				if (mIsCustomBuild) {
					mOriginAtomBitmap = mABitmapFactory.buildAtomBitmap(level, mUserData);
				} else {
					mOriginAtomBitmap = new AtomBitmap(mHelper, level, key, BitmapNetGetter.DECODE_ORIGIN_SIZE, BitmapNetGetter.DECODE_ORIGIN_SIZE);
				}
			}
			return mOriginAtomBitmap;
		} else {
			throw new RuntimeException("wrong level" + level);
		}
	}
	
	public static interface CustomBuildAtomBitmapFactory {
		public AtomBitmap buildAtomBitmap(LEVEL level, Object userData);
	}
}
