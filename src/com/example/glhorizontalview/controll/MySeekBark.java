package com.example.glhorizontalview.controll;

import android.content.Context;
import android.widget.SeekBar;

public class MySeekBark extends SeekBar {
	private int Max_SIZE = 100;
	private boolean blockOutsizeProgress;

	public MySeekBark(Context context, OnSeekBarChangeListener listener) {
		super(context);
		this.setMax(101);
		Max_SIZE = (int) (100 * context.getResources().getDisplayMetrics().density);
		this.setOnSeekBarChangeListener(listener);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(Max_SIZE, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setCurrProgress(float offset_progress) {
		if (blockOutsizeProgress) return;
		
		setProgress((int)(offset_progress * 100));
	}

	public void stopToSeek() {
		blockOutsizeProgress = false;
	}

	public void startToSeek() {
		blockOutsizeProgress = true;
	}

	
}
