package ds.android.ui.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class CommonMenuItem extends CommonButton {
	public static final int UI_TEXT_SIZE = 20;
	public static final int UI_NAME_PADDING_LEFT = 6;
	public static final int UI_HEIGHT = 40;
	private float mDensity;

	private Paint mTextPaint;
	private String mItemName;

	public CommonMenuItem(Context aContext, String aName) {
		super(aContext);
		mDensity = getResources().getDisplayMetrics().density;
		mItemName = aName;
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(UI_TEXT_SIZE * mDensity);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int height = (int) (UI_HEIGHT * dm.density);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas aCanvas) {
		super.onDraw(aCanvas);
		final int namePaddingLeft = (int) (UI_NAME_PADDING_LEFT * mDensity);

		int offsetX, offsetY;
		offsetX = namePaddingLeft;

		mTextPaint.setColor(0xffaaaaaa);
		offsetY = (int) calcYWhenTextAlignCenter(getMeasuredHeight(), mTextPaint);
		aCanvas.drawText(mItemName, offsetX, offsetY, mTextPaint);
	}

	public static float calcYWhenTextAlignCenter(int aCanvasHeight, Paint aPaint) {
		if (aPaint == null) {
			return 0;
		}

		final float fontHeight = aPaint.getFontMetrics().bottom - aPaint.getFontMetrics().top;
		return ((aCanvasHeight - fontHeight) / 2 - aPaint.getFontMetrics().top);
	}

}
