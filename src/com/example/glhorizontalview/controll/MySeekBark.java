package com.example.glhorizontalview.controll;

import com.ds.io.DsLog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.SeekBar;

public class MySeekBark extends SeekBar {
	private int Max_SIZE = 100;
	private boolean blockOutsizeProgress;

	public MySeekBark(Context context, OnSeekBarChangeListener listener) {
		super(context);
		this.setMax(101);
		Max_SIZE = (int) (100 * context.getResources().getDisplayMetrics().density);
		this.setOnSeekBarChangeListener(listener);
		ColorDrawable bg = new ColorDrawable(0xff00000);
		ColorDrawable cd = new ColorDrawable(0xff888888);
//		this.setBackgroundDrawable(bg);
//		this.setThumb(cd);
		this.setIndeterminateDrawable(bg);
		this.setProgressDrawable(cd);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(Max_SIZE, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private float mCurrOffset, mCurrMinOffset;
	public void setCurrProgress(float currOffset, float calced_max_offset,
			float calced_min_offset) {
		if (blockOutsizeProgress) return;
		
		if ((mCurrOffset == currOffset) && (calced_min_offset == mCurrMinOffset)) {
			return;
		}
		
		float progress = ((-currOffset) / (calced_max_offset - calced_min_offset));
		progress = Math.min(progress, 1);
		progress = Math.max(progress, 0);

		mCurrOffset = currOffset;
		mCurrMinOffset = calced_min_offset;
		setProgress( (int)(progress * 100));
	}
	
	public void stopToSeek() {
		blockOutsizeProgress = false;
	}

	public void startToSeek() {
		blockOutsizeProgress = true;
	}

	public boolean isSeeking() {
		return blockOutsizeProgress;
	}
}
