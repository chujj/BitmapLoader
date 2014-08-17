package com.example.glhorizontalview.data;

import android.graphics.Canvas;

public interface IData {
	public static final int SORT_REVERSE = 1 << 4;
	public static final int SORT_NAME = 1;
	public static final int SORT_LASTMODIFY = 2;
	public static final int SORT_SIZE = 3;
	
	public boolean supportSort(int sortby);
	
	public void sort(int flag);
	
	
	// delegate from GLResourceModel
	public int getCount();

	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width, int require_height);
//	public Bitmap gotBitmap(); 

	/** called on UI Thread
	 * @param hit
	 */
	public void clickAt(int hit);

	public void longClick(float x , float y, int hit);

}
