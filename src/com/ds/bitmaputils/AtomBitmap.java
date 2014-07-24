package com.ds.bitmaputils;

import java.io.FileInputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AtomBitmap {
	private int max_width, max_height;
	private int width, height;
	private int size;
	private Bitmap mBitmap;
	private boolean isRecycled;
	private long lastAccessTime;
	private long accessCount;
	private String mFilePath;
	private boolean isDecodeError;

	public AtomBitmap(String abspath2File, int m_width, int m_height) {
		mFilePath = abspath2File;
		max_width = m_width;
		max_height = m_height;
		isRecycled = false;
		isDecodeError = false;
	}
	
	public int getSize() {
		return size;
	}
	
	/** mark time , increase timestamp
	 * @return
	 */
	public Bitmap getBitmap() { // ZHUJJ distingursh between file read fail and not load yet 
		if (mBitmap == null) {
			loadBitmap();
		}
		return mBitmap;
	}
	
	private void loadBitmap() {
		BitmapFactory.Options op = new BitmapFactory.Options();
		op.inScaled = false;
		op.inJustDecodeBounds = true;
		
		try {
			InputStream is = new FileInputStream(mFilePath);
			BitmapFactory.decodeStream(is, null, op);
			is.close();
			
			int sample = Math.max(1, (op.outWidth + op.outHeight ) / (max_width + max_height));
			op.inSampleSize = sample;
			op.inJustDecodeBounds = false;
			mBitmap = BitmapFactory.decodeStream(new FileInputStream(mFilePath), null, op);
		} catch (Exception e) {
			isDecodeError = true;
			// ZHUJJ: handle exception
		}
		
	}

	public boolean isRecycled() {
		return isRecycled;
	}
}
