package com.example.glhorizontalview;

import android.graphics.Canvas;

public interface GLResourceModel {
	public int getCount();

	public void updateToCanvas(int aIdx, Canvas mC, int require_width, int require_height);
}
