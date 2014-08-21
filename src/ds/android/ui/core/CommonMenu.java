package ds.android.ui.core;

import java.util.ArrayList;

import ssc.software.picviewer.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

public class CommonMenu extends ViewGroup implements OnClickListener, Popable {
	
	private int UI_ITEM_WIDTH;
	private int UI_TOTAL_WIDTH;
	
	private int UI_X_PADDING = 7;
	private int UI_Y_PADDING_TOP = 5;
	private int UI_Y_PADDING_BOTTOM = 9;
	private int UI_Y_GAP = 3;

	private float mDensity;
	private Rect mPopUpTarget;
	private int mMenuId;
	private onMenuItemClickListener mListener;
	private NinePatchDrawable mBg9Path; 
	
	public CommonMenu(Context aContext, ArrayList<String> aItems, int aMenuId, onMenuItemClickListener aListener) {
		super(aContext);
		
		setWillNotDraw(false);
		mDensity = getResources().getDisplayMetrics().density;
		for (String iterate : aItems) {
			CommonMenuItem item = new CommonMenuItem(getContext(), iterate);
			addView(item);
			item.setOnClickListener(this);
		}
		calcSize(aContext, aItems);
		mMenuId = aMenuId;
		mListener = aListener;
		mBg9Path = this.getNinePatchDrawable(R.drawable.menu_bg);
	}

	private void calcSize(Context aContext, ArrayList<String> aItems) {
		UI_ITEM_WIDTH = calcaPpropriateWidth(aContext, aItems);
		UI_X_PADDING = (int) (UI_X_PADDING * mDensity);
		UI_Y_PADDING_TOP = (int) (UI_Y_PADDING_TOP * mDensity);
		UI_Y_PADDING_BOTTOM = (int) (UI_Y_PADDING_BOTTOM * mDensity);
		UI_Y_GAP = (int) (UI_Y_GAP * mDensity);
	
		UI_TOTAL_WIDTH = UI_ITEM_WIDTH + (UI_X_PADDING << 1);
	}

	public static interface onMenuItemClickListener {
		public void onMenuItemClick(View aMenu, int aMenuId, int aItemOrder);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (getChildCount() == 0) {
			setMeasuredDimension(0, 0);
			return;
		}
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(MeasureSpec.makeMeasureSpec(UI_ITEM_WIDTH, MeasureSpec.EXACTLY), 0);
		}

		setMeasuredDimension(getGuessWidth(), getGuessHeight());
	}

	final private int getGuessWidth() {
		 return UI_TOTAL_WIDTH;
	}
	
	final private int getGuessHeight() {
		final int count = getChildCount();
		final int height = ((int) (CommonMenuItem.UI_HEIGHT * mDensity)) * count
				+ UI_Y_GAP * (count -1)
				+ (UI_Y_PADDING_TOP + UI_Y_PADDING_BOTTOM);
		return  height;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int y = UI_Y_PADDING_TOP;
		int x = UI_X_PADDING;
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).layout(x, y, x + getChildAt(i).getMeasuredWidth(), 
					y + getChildAt(i).getMeasuredHeight());
			y = y + getChildAt(i).getMeasuredHeight() + UI_Y_GAP; 
		}
	}

	@Override
	protected void onDraw(Canvas aCanvas) {
		mBg9Path.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
		mBg9Path.draw(aCanvas);
	}

	private int calcaPpropriateWidth(Context aContext, ArrayList<String> aStrings) {
		final float dsy = mDensity;
		final int gap = ((int) (CommonMenuItem.UI_NAME_PADDING_LEFT * dsy)) << 1;
		int max = 0;
		Paint tmpPaint = new Paint();
		tmpPaint.setTextSize(CommonMenuItem.UI_TEXT_SIZE * dsy);
		for (String item: aStrings) {
			int t = (int) tmpPaint.measureText(item);
			if (t > max) {
				max = t;
			}
		}

		max += gap;
		return max;
	}

	private NinePatchDrawable getNinePatchDrawable(int aNinePatchResourceId) {
		Bitmap bg = BitmapFactory.decodeResource(this.getResources(),
				aNinePatchResourceId);
		NinePatch np = new NinePatch(bg, bg.getNinePatchChunk(), null);
		NinePatchDrawable t9Patch = new NinePatchDrawable(getResources(), np);
		return t9Patch;
	}
	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onMenuItemClick(this, mMenuId, indexOfChild(v));
		}
	}

	@Override
	public Rect getPopupTarget() {
		return mPopUpTarget;
	}

	@Override
	public void setPopupTarget(Rect aSrc) {
		if (mPopUpTarget == null) {
			mPopUpTarget = new Rect(aSrc);
		} else {
			mPopUpTarget.set(aSrc);
		}
	}

	public void showAbrove(View v, int aTop, int aLeft, int aRightLimit) {
		int l, t, r, b;
		b = aTop;
		t = b - getGuessHeight();

		l = aLeft - (getGuessWidth() >> 1);
		r = l + (getGuessWidth());
		if (r > aRightLimit) {
			r = aRightLimit;
			l = r - getGuessWidth();
		}
		Rect where = new Rect(l, t, r, b);
		setPopupTarget(where);
	}
	
	public void showBelow(View v, int aTop, int aLeft, int aRightLimit) {
		int l, t, r, b;
		t = aTop;
		b = t + getGuessHeight();

		l = aLeft - (getGuessWidth() >> 1);
		r = l + (getGuessWidth());
		if (r > aRightLimit) {
			r = aRightLimit;
			l = r - getGuessWidth();
		}
		Rect where = new Rect(l, t, r, b);
		setPopupTarget(where);
	}
	
	public void showBelow(View v, int aLeftLimit, int aTopLimit, int aRightLimit, int aBottomLimit) {
		int l, t, r, b;
		t = v.getBottom();
		b = t + getGuessHeight();

		l = v.getLeft() + ((v.getMeasuredHeight() - getGuessWidth() ) / 2);
		r = l + (getGuessWidth());
		Rect where = new Rect(l, t, r, b);
		if (b > aBottomLimit) {
			where.offset(0, (aBottomLimit - b));
		}
		
		setPopupTarget(where);
	}


	public void startScaleOutAnim() {
		startScaleOutAnimFromBottom();
	}
	
	public void startScaleOutAnimFromBottom() {
		ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 1f);
		scale.setDuration(200);
		this.startAnimation(scale);
	}
	
	public void startScaleOutAnimFromTop() {
		ScaleAnimation scale = new ScaleAnimation(0f, 1f, 0f, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0f);
		scale.setDuration(200);
		this.startAnimation(scale);
	}
}
