package ds.android.ui.core;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class CommonButton extends View {
	private int UI_MENU_ITEM_CORNER = 2;
	private int UI_PRESS_COLOR = 0xff1981eb;
	protected boolean mIsPress = false;
	private Paint mBgPaint;
	private RectF mRoundRect;

	public CommonButton(Context aContext) {
		super(aContext);
		
		setClickable(true);
		mRoundRect = new RectF();
		mBgPaint = new Paint();
		calcSize();
	}

	private void calcSize() {
		final float dsy = getResources().getDisplayMetrics().density;
		UI_MENU_ITEM_CORNER = (int) (UI_MENU_ITEM_CORNER * dsy);
	}

	@Override
	public boolean onTouchEvent(MotionEvent aEvent) {
		switch (aEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mIsPress = true;
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsPress = false;
				invalidate();
				break;
			default:
				break;
		}
		return super.onTouchEvent(aEvent);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mIsPress) {
			mRoundRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
			mBgPaint.setColor(UI_PRESS_COLOR);
			canvas.drawRoundRect(mRoundRect, UI_MENU_ITEM_CORNER, UI_MENU_ITEM_CORNER, mBgPaint);
		} else {
			;
		}
	}
}
