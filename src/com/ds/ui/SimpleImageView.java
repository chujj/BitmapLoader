package com.ds.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

public class SimpleImageView extends View {

	private Rect mBgRect;
	private Bitmap mBg;

	public SimpleImageView(Context context, Bitmap bg) {
		super(context);

		mBg = bg;
		mBgRect = new Rect();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mBgRect.set(0, 0,
				DsDimensionUtil.getSizeFromMeasureSpec(widthMeasureSpec),
				DsDimensionUtil.getSizeFromMeasureSpec(heightMeasureSpec));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBg != null && !mBg.isRecycled()) {
			canvas.drawBitmap(mBg, null, mBgRect, null);
		}
	}

}
