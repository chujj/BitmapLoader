package ds.android.utils;

import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class DsUtils {
	/**
	 * 强制对View及其子视图进行递归刷新
	 * 
	 * @param aView
	 */
	public static void forceChildrenInvalidateRecursively(View aView) {
		if (aView instanceof ViewGroup) {
			ViewGroup childGroup = (ViewGroup) aView;
			int childCnt = childGroup.getChildCount();
			for (int i = 0; i < childCnt; i++) {
				View childView = childGroup.getChildAt(i);
				forceChildrenInvalidateRecursively(childView);
			}
		}
		if (aView != null) {
			aView.invalidate();
		}
	}
	
	public static float degree2arc(float aDegree) {
		return (float) (aDegree / 180.0f * Math.PI);
	}
	
	public static float arc2degree(float aArc) {
		return (float) (aArc / Math.PI * 180.0f);
	}

	
	public static void removeFromParent(View aChild) {
		if (aChild != null) {
			View parent = (View) aChild.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(aChild);
			}
		}
	}
	
	public static float getDensity(Context context) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.density;
	}
	
	public static int getDensityDimen(Context context, int dimen) {
		final DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return ((int) (dimen * dm.density));
	}
	
	public static void layoutViewAtPos(View view, int offsetX, int offsetY) {
		view.layout(offsetX, offsetY, offsetX + view.getMeasuredWidth(),
				offsetY + view.getMeasuredHeight());
	}
	
	public static void measureExactly(View view, int width, int height) {
		int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		view.measure(widthSpec, heightSpec);
	}
	

	public static int getStatusbarHeight(Activity aActivity) {
		int statusbarHeight;
		try {
			Rect frame = new Rect();
			aActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			statusbarHeight = frame.top;
		} catch (Exception e) {
			final DisplayMetrics dm = aActivity.getResources().getDisplayMetrics();
			statusbarHeight = (int) (24 * dm.density);
		}
		return statusbarHeight;
	}
	
	public static String getTruncateEndString(String aSrcString, Paint aPaint, int aWidth) {
		if (aSrcString == null) {
			return "";
		}
		return TextUtils.ellipsize(aSrcString, new TextPaint(aPaint), aWidth, TextUtils.TruncateAt.END)
				.toString();
	}
	
	public static boolean isEmptyCollection(Collection collection) {
		if (collection == null || collection.size() == 0) {
			return true;
		}
		return false;
	}
}
