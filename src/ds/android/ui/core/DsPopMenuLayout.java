package ds.android.ui.core;

import ds.android.utils.DsUtils;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class DsPopMenuLayout extends FrameLayout {
	
	// 手指按下的半径
	private static final int FINGER_RADIUS = 20;

	public DsPopMenuLayout(Context context) {
		this(context, null);
	}

	public DsPopMenuLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DsPopMenuLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setClickable(true);

		setVisibility(View.INVISIBLE);
	}

	public boolean isPopMenuShow() {
		return (getVisibility() == View.VISIBLE);
	}

	public View getPopMenu() {
		if (getChildCount() != 0) {
			return getChildAt(0);
		} else {
			return null;
		}
	}

	public void showPopMenu(DsPopMenu aPopMenu) {
		final FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lParams.gravity = Gravity.CENTER;

		showPopMenu(aPopMenu, lParams);
	}

	/**
	 * 
	 * 在某个点附近弹出菜单 注意：仅当PopMenuLayout是全屏时才可以使用该方法
	 */
	public void showPopMenu(DsPopMenu aPopMenu, Point aPosition) {
		showPopMenu(aPopMenu, aPosition, 0, 0);
	}

	public void showPopMenu(DsPopMenu aPopMenu, Point aPosition, int aPaddingTop, int aPaddingBottom) {
		showPopMenu(aPopMenu, aPosition, 0, 0, 0);
	}
	
	public void showPopMenu(DsPopMenu aPopMenu, Point aPosition, int aPaddingTop, int aPaddingBottom, int aMarginTop) {
		final DisplayMetrics dm = getResources().getDisplayMetrics();
		final int menuWidth = aPopMenu.getPopMenuWidth();
		final int menuHeight = aPopMenu.getPopMenuHeight();
		
		final int statusbarHeight = DsUtils.getStatusbarHeight((Activity) getContext());
		aPosition.y -= (statusbarHeight + aMarginTop);
		
		final int fingerRadius = (int) (FINGER_RADIUS * dm.density);

		FrameLayout.LayoutParams lParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lParams.gravity = Gravity.NO_GRAVITY;

		if (aPosition.y - aPaddingTop > menuHeight + fingerRadius) {
			// 如果点击上方放得下，则放在上方
			lParams.topMargin = aPosition.y - menuHeight - fingerRadius;
		} else if (aPosition.y + menuHeight + fingerRadius < getMeasuredHeight() - aPaddingBottom) {
			// 如果上方放不下，下方放得下，放在下方
			lParams.topMargin = aPosition.y + fingerRadius;
		} else {
			// 如果上方下方都放不下
			if (aPosition.y >= getMeasuredHeight() - aPosition.y) {
				// 上方空间更大
				lParams.topMargin = aPaddingTop;
			} else {
				// 下方空间更大
				lParams.topMargin = getMeasuredHeight() - menuHeight - aPaddingBottom;
			}
		}

		lParams.leftMargin = aPosition.x - menuWidth / 2;
		if (lParams.leftMargin < 0) {
			lParams.leftMargin = 0;
		}
		int popMenuWidth = getMeasuredWidth();
		if (popMenuWidth == 0) {
			popMenuWidth = dm.widthPixels;
		}
		if (lParams.leftMargin + menuWidth > popMenuWidth) {
			lParams.leftMargin = popMenuWidth - menuWidth;
		}

		showPopMenu(aPopMenu, lParams);
	}

	public void showPopMenu(View aPopMenu, FrameLayout.LayoutParams aLayoutParams) {
		if (getChildCount() != 0) {
			removeAllViews();
			recycleAndDestoryMenus();
		}

		if (aPopMenu instanceof DsPopMenu) {
			aPopMenu.setDrawingCacheEnabled(true);
		}
		addView(aPopMenu, aLayoutParams);

		setVisibility(View.VISIBLE);
	}

	public void dismissPopMenu() {
		recycleAndDestoryMenus();
		removeAllViews();
		setVisibility(View.INVISIBLE);
	}

	private void recycleAndDestoryMenus() {
		for (int i = 0; i < getChildCount(); i++) {
			if (!(getChildAt(i) instanceof DsPopMenu)) {
				continue;
			}
			((DsPopMenu) getChildAt(i)).recyclePopMenu();
			getChildAt(i).destroyDrawingCache();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent aEvent) {
		switch (aEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dismissPopMenu();
				break;
			default:
				break;
		}
		return super.onTouchEvent(aEvent);
	}

}
