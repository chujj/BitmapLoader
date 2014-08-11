package ds.android.ui.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class YiButton extends View {

	protected boolean mIsPress = false;

	public YiButton(Context aContext) {
		super(aContext);
		init();
	}
	
	public YiButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public YiButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setClickable(true);
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
}
