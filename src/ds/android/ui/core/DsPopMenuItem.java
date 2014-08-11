package ds.android.ui.core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class DsPopMenuItem extends YiButton {
	private static final int UI_DEFAULT_BACKGROUND = 0x66000000;

	private static final int DEFAULT_HEIGHT = 50;
	private static final int DEFAULT_WIDTH = 50;

	private static final int DEFAULT_MARGIN = 5;

	private int mMarginLeft;
	private int mMarginTop;
	private int mMarginRight;
	private int mMarginBottom;

	public DsPopMenuItem(Context aContext) {
		super(aContext);

		final DisplayMetrics dm = getResources().getDisplayMetrics();
		mMarginLeft = (int) (DEFAULT_MARGIN * dm.density);
		mMarginTop = (int) (DEFAULT_MARGIN * dm.density);
		mMarginRight = (int) (DEFAULT_MARGIN * dm.density);
		mMarginBottom = (int) (DEFAULT_MARGIN * dm.density);

		setBackgroundColor(UI_DEFAULT_BACKGROUND);
	}

	public void setMargin(int aMargin) {
		setMargins(aMargin, aMargin, aMargin, aMargin);
	}

	public void setMargins(int aLeftMargin, int aTopMargin, int aRightMargin, int aBottomMargin) {
		mMarginLeft = aLeftMargin;
		mMarginTop = aTopMargin;
		mMarginRight = aRightMargin;
		mMarginBottom = aBottomMargin;
	}

	public int getMarginLeft() {
		return mMarginLeft;
	}

	public int getMarginTop() {
		return mMarginTop;
	}

	public int getMarginRight() {
		return mMarginRight;
	}

	public int getMarginBottom() {
		return mMarginBottom;
	}

	protected void recyclePopMenuItem() {

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int width = (int) (DEFAULT_WIDTH * dm.density);
		final int height = (int) (DEFAULT_HEIGHT * dm.density);
		setMeasuredDimension(width, height);
	}

}
