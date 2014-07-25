package com.ds.bitmaputils;

import java.util.ArrayList;

import com.ds.bitmaputils.BitmapHelper.LEVEL;

import android.graphics.Bitmap;

public class AtomBitmap implements BitmapTask , Comparable<AtomBitmap> {
	private int max_width, max_height;
	private int width, height;
	private long size;
	private Bitmap mBitmap;
	private boolean isRecycled;
	private long lastAccessTime;
	private long accessCount;
	private String mFilePath;
	private boolean isDecodeError;
	private ArrayList<BitmapGotCallBack> mRegistedCallback;
	private LEVEL mLevel;
	private Object unique_key;

	public AtomBitmap(BitmapHelper aHalper, LEVEL level, String abspath2File, int m_width, int m_height) {
		mFilePath = abspath2File;
		max_width = m_width;
		max_height = m_height;
		isRecycled = false;
		isDecodeError = false;
		mRegistedCallback = new ArrayList<BitmapGotCallBack>();
		aHalper.registeAtomBitmap(this);
		size = -1;
		mLevel = level;
		
		unique_key = UniqueKey(mFilePath + level.name());
	}
	
	public long getSize() {
		return size;
	}
	
	/** mark time , increase timestamp
	 * @return
	 */
	public Bitmap getBitmap() { // ZHUJJ distingursh between file read fail and not load yet 
		if (mBitmap == null) {
			mBitmap = BitmapNetGetter.tryGetBitmapFromUrlOrCallback(this, mInsideCallback);
		}
		markRead();
		return mBitmap;
	}
	
	public Bitmap getBitmap(BitmapGotCallBack callback) {
		return null;
	}

	final private void markRead() {
		lastAccessTime = System.currentTimeMillis();
		accessCount ++ ;
	}
	
	private BitmapGotCallBack mInsideCallback = new BitmapGotCallBack() {
		
		@Override
		public void onBitmapGot(Bitmap aBitmap) {
			mBitmap = aBitmap;
			width = mBitmap.getWidth();
			height = mBitmap.getHeight();
			size = width * height;
		}
	};

	public boolean isRecycled() {
		return isRecycled;
	}
	
	private static Object UniqueKey(String str) {
		return Integer.valueOf(str.hashCode());
	}

	//////////////////////////////// Task implemention ////////////////////////////////
	@Override
	public Object getTaskKey() {
		return unique_key;
	}

	@Override
	public String getNetUrl() {
		// ZHUJJ Auto-generated method stub
		return null;
	}

	@Override
	public String getFileSystemPath() {
		return mFilePath;
	}

	@Override
	public void saveNetUrl(String aUrl) {
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public void saveFileSystemPath(String aPath) {
		// ZHUJJ Auto-generated method stub
	}

	@Override
	public int getBitmapMaxWidth() {
		return max_width;
	}

	@Override
	public int getBitmapMaxHeight() {
		return max_height;
	}

	@Override
	public int compareTo(AtomBitmap arg0) {
		return (int) (size - arg0.size);
	}

	public String dump() {
		return mLevel.name() + " : " + Long.toString(size) + " : " + Long.toString(lastAccessTime) + " : " + Long.toString(accessCount) + " : "+  mFilePath;
	}
}
