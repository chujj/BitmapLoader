package ds.android.ui.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class BitmapButton extends View {

	private Bitmap mNormalBitmap;
	private Bitmap mPressBitmap;

	private int width;
	private int height;
	
	private boolean mIsPress = false;

	public BitmapButton(Context context, Bitmap aNormalBitmap) {
		this(context, aNormalBitmap, null);
	}

	public BitmapButton(Context context, Bitmap aNormalBitmap, Bitmap aPressBitmap) {
		super(context);
		
		setClickable(true);

		mNormalBitmap = aNormalBitmap;
		mPressBitmap = aPressBitmap;

		if (mNormalBitmap != null) {
			width = mNormalBitmap.getWidth();
			height = mNormalBitmap.getHeight();
		}
	}
	
	public void release() {
//		BitmapUtil.recycleBitmap(mNormalBitmap);
//		BitmapUtil.recycleBitmap(mPressBitmap);
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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas aCanvas) {
		if (mIsPress) {
			if (mPressBitmap != null) {
				aCanvas.drawBitmap(mPressBitmap, 0, 0, null);
			}
		} else {
			if (mNormalBitmap != null) {
				aCanvas.drawBitmap(mNormalBitmap, 0, 0, null);
			}
		}
	}

	
}
