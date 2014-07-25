package com.ds.bitmaputils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AtomBitmap implements BitmapTask {
	private int max_width, max_height;
	private int width, height;
	private int size;
	private Bitmap mBitmap;
	private boolean isRecycled;
	private long lastAccessTime;
	private long accessCount;
	private String mFilePath;
	private boolean isDecodeError;
	private ArrayList<BitmapGotCallBack> mRegistedCallback;

	public AtomBitmap(String abspath2File, int m_width, int m_height) {
		mFilePath = abspath2File;
		max_width = m_width;
		max_height = m_height;
		isRecycled = false;
		isDecodeError = false;
		mRegistedCallback = new ArrayList<BitmapGotCallBack>();
	}
	
	public int getSize() {
		return size;
	}
	
	/** mark time , increase timestamp
	 * @return
	 */
	public Bitmap getBitmap() { // ZHUJJ distingursh between file read fail and not load yet 
		if (mBitmap == null) {
			mBitmap = BitmapNetGetter.tryGetBitmapFromUrlOrCallback(this, mInsideCallback);
		}
		return mBitmap;
	}
	
	public Bitmap getBitmap(BitmapGotCallBack callback) {
		return null;
	}

	private BitmapGotCallBack mInsideCallback = new BitmapGotCallBack() {
		
		@Override
		public void onBitmapGot(Bitmap aBitmap) {
			mBitmap = aBitmap;
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
		return UniqueKey(mFilePath);
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
}
