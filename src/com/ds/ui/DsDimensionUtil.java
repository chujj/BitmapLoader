package com.ds.ui;

import android.content.Context;
import android.view.View.MeasureSpec;

public class DsDimensionUtil {
	public static float getDensity(Context aContext) {
		return aContext.getResources().getDisplayMetrics().density;
	}
	public static int getSizeFromMeasureSpec(int aMeasureSpec) {
		return MeasureSpec.getSize(aMeasureSpec);
	}
	
	public static int makeExactlyMeasureSpecSize(int aExactlySize) {
		return MeasureSpec.makeMeasureSpec(aExactlySize, MeasureSpec.EXACTLY);
	}
}
