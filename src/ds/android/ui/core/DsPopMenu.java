package ds.android.ui.core;

import java.util.ArrayList;

import android.content.Context;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DsPopMenu extends ViewGroup implements View.OnClickListener {
	private static final int UI_DEFAULT_BACKGROUND = 0x66000000;

	private static final int DEFAULT_MAX_COL = 3;

	/** 弹出菜单的列数和行数 */
	protected int mColNum;
	protected int mRowNum;

	/** PopMenuItem之间的margin是否重叠 */
	private boolean mIsMarginCollapsing = true;

	/** PopMenuItem的最大列数 */
	private int mMaxCol;

	private DsPopMenuClickListener mListener;
	
	public int mWidth;
	public int mHeight;
	
	public DsPopMenu(Context context) {
		super(context);

		mMaxCol = DEFAULT_MAX_COL;

		setClickable(true);
		setBackgroundColor(UI_DEFAULT_BACKGROUND);
	}
	
	public int getPopMenuWidth() {
		if (mWidth == 0) {
			measureInner();
		}
		return mWidth;
	}
	
	public int getPopMenuHeight() {
		if (mHeight == 0) {
			measureInner();
		}
		return mHeight;
	}

	public void setIsMarginCollapsing(boolean aIsMarginCollapsing) {
		mIsMarginCollapsing = aIsMarginCollapsing;
	}

	public void setMaxColumn(int aMaxCol) {
		mMaxCol = aMaxCol;
	}

	public void addPopMenuItem(DsPopMenuItem aMenuItem) {
		addView(aMenuItem);
		aMenuItem.setOnClickListener(this);
		calculateRowAndCol();
		requestLayout();
	}

	public void setPopMenuClickListener(DsPopMenuClickListener aListener) {
		mListener = aListener;
	}

	public ArrayList<DsPopMenuItem> getPopMenuItems() {
		ArrayList<DsPopMenuItem> list = new ArrayList<DsPopMenuItem>();
		for (int i = 0; i < getChildCount(); i++) {
			list.add((DsPopMenuItem) getChildAt(i));
		}
		return list;
	}

	protected void calculateRowAndCol() {
		mRowNum = (getChildCount() - 1) / mMaxCol + 1;
		mColNum = Math.min(mMaxCol, getChildCount());
	}

	protected void recyclePopMenu() {
		for (int i = 0; i < getChildCount(); i++) {
			((DsPopMenuItem) getChildAt(i)).recyclePopMenuItem();
		}
	}
	
	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onPopMenuItemClick(getId(), v.getId());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureInner();
	}
	
	private void measureInner() {
		if (getChildCount() == 0) {
			setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
			return;
		}

		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(0, 0);
		}

		final DsPopMenuItem item = (DsPopMenuItem) getChildAt(0);

		mWidth = getPaddingLeft() + getPaddingRight() + mColNum
				* (item.getMeasuredWidth() + item.getMarginLeft() + item.getMarginRight());
		mHeight = getPaddingTop() + getPaddingBottom() + mRowNum
				* (item.getMeasuredHeight() + item.getMarginTop() + item.getMarginBottom());

		if (mIsMarginCollapsing) {
			mWidth-= Math.min(item.getMarginLeft(), item.getMarginRight()) * (mColNum - 1);
			mHeight -= Math.max(item.getMarginTop(), item.getMarginBottom()) * (mRowNum - 1);
		}

		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			final int row = i / mColNum;
			final int col = i % mColNum;
			final DsPopMenuItem item = (DsPopMenuItem) getChildAt(i);

			int offsetX = getPaddingLeft();
			int offsetY = getPaddingTop();
			offsetX += (item.getMeasuredWidth() + item.getMarginLeft() + item.getMarginRight()) * col;
			offsetY += (item.getMeasuredHeight() + item.getMarginTop() + item.getMarginBottom()) * row;
			offsetX += item.getMarginLeft();
			offsetY += item.getMarginTop();

			if (mIsMarginCollapsing) {
				offsetX -= Math.min(item.getMarginLeft(), item.getMarginRight()) * col;
				offsetY -= Math.max(item.getMarginTop(), item.getMarginBottom()) * row;
			}

			item.layout(offsetX, offsetY, offsetX + item.getMeasuredWidth(),
					offsetY + item.getMeasuredHeight());
		}
	}

	public interface DsPopMenuClickListener {
		void onPopMenuItemClick(int aPopMenuId, int aPopMenuItemId);
	}

}
