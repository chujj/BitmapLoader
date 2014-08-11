package com.example.glhorizontalview;

import android.graphics.Canvas;

public interface GLResourceModel {
	public int getCount();

	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width, int require_height);

	/** called on UI Thread
	 * @param hit
	 */
	public void clickAt(int hit);
	
	public void currRenderView(MyRenderer render);
}
