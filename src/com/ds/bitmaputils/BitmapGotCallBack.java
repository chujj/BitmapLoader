package com.ds.bitmaputils;

import android.graphics.Bitmap;

public interface BitmapGotCallBack {
	/** Call after we got the Bitmap
	 * @param aBitmap
	 */
	public void onBitmapGot(Bitmap aBitmap);
}
