package com.ds.bitmaputils;

import android.graphics.Bitmap;

public class AtomBitmap {
	private int width, height;
	private int size;
	private Bitmap mBitmap;
	private boolean isRecycled;
	private long lastAccessTime;
	private long accessCount;

	public int getSize() {
		return size;
	}
}
