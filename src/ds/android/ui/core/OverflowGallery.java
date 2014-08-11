package ds.android.ui.core;

import com.ds.io.DsLog;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class OverflowGallery extends ViewGroup {
	private static enum TouchState {REST, SCROLLING };

	private int SNAP_VELOCITY = 300;

	private int mCurScreen;
	private int mTouchSlop;
	private float mLastMotionX;
	private float mDownMotionX;
	private float mDownMotionY;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private TouchState mTouchState = TouchState.REST;

	private DsHomeGalleryListener mListener;
	
	private boolean isXLocked = false;
	private boolean isLockETH = false;

	private int[] mScreenWidths;
	
	public OverflowGallery(Context aContext) {
		super(aContext);
		init(aContext);
	}

	private void init(Context aCtx) {
		mScroller = new Scroller(aCtx);
		mTouchSlop = ViewConfiguration.get(aCtx).getScaledTouchSlop();
		mVelocityTracker = VelocityTracker.obtain();
		this.setWillNotDraw(true);
		
		this.reset();
		
		if (android.os.Build.MODEL.toLowerCase().startsWith("mi-one")) {
			SNAP_VELOCITY = 200;
		}
	}

	public void onStart(int aCurScreen) {
		this.setDefaultScreen(aCurScreen);
	}
	
	private void setDefaultScreen(int aDefScreen) {
		mCurScreen = aDefScreen;
	}
	
	public void lockX() {
		isXLocked = true;
		lockETH();
	}
	
	private void lockETH () {
		isLockETH = true;
	}

	public void unlockX() {
		isXLocked = isLockETH = false;
	}

	public int getCurrX() {
		return this.getScrollX();
	}
	
	public int getCurScreen() {
		return mCurScreen;
	}

	private int mTotalWidth;
	private int mLeft_edge;
	private int mRight_edge;
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measureWidth =  MeasureSpec.getSize(widthMeasureSpec);
		int totalWidth = 0;

		if (((MeasureSpec.getMode(widthMeasureSpec) & (~MeasureSpec.EXACTLY)) != 0) ||  
			((MeasureSpec.getMode(heightMeasureSpec) & (~MeasureSpec.EXACTLY)) != 0) ) {
//			Log.e("this gallery could only work in EXACTLY mode!");
			Math.abs(1 / 0);
		}

		int count = getChildCount();
		mScreenWidths = new int[count];
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, heightMeasureSpec);
//			if (i == 0) {
//				mScreenWidths[i] = totalWidth = measureWidth;
//			} else {
				mScreenWidths[i] = child.getMeasuredWidth();
				totalWidth += mScreenWidths[i];
//			}
		}

		mTotalWidth = totalWidth;
		mLeft_edge = 0;
		mRight_edge = getDestScrollIdx(1);
//		Log.e(mTotalWidth + " " + mSingleWidth + " "  + mLeft_edge + " " + mRight_edge);
		
		this.setMeasuredDimension(mTotalWidth, MeasureSpec.getSize(heightMeasureSpec));
		
		scrollTo(getDestScrollIdx(mCurScreen), 0);
	}

	private int getDestScrollIdx(int aWhich) {
		if (aWhich ==0) {
			return 0;
		} else {
			// 注意，这里的逻辑比较复杂，将来整理
			if (mScreenWidths[aWhich - 1] < mScreenWidths[aWhich]) {
				return mScreenWidths[aWhich - 1];
			} else {
				return mScreenWidths[aWhich];
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			if (childView.getVisibility() == View.GONE) continue;
			
			final int childWidth = childView.getMeasuredWidth();
			childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
			childLeft += childWidth;
		}
	}
	
	public void snapToScreen(int aWhichScreen) {
		snapToScreenWithDuration(aWhichScreen, -1);
	}

	private void snapToScreenWithDuration(int whichScreen, int aDuration) {
		whichScreen = whichScreen < 0 ? 0 : 
			whichScreen > (getChildCount() -1) ? (getChildCount() -1 ) : whichScreen;
			
		if (getScrollX() == (getDestScrollIdx(whichScreen)))	return;
		
		final int delta = getDestScrollIdx(whichScreen) - getScrollX();
		final int during =aDuration == -1 ? Math.abs(delta) :  aDuration;
		mScroller.startScroll(getScrollX(), 0, delta, 0, during);

		mCurScreen = whichScreen;
		invalidate(); // Redraw the layout
		
		if (mListener != null) {
			mListener.onGalleryScreenChanged(getChildAt(mCurScreen), mCurScreen);
		}
		
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (mListener != null) {
					mListener.onGalleryScreenChangeComplete(getChildAt(mCurScreen), mCurScreen);
				}
			}
		}, during + 10);
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	public void computeScroll() {
		if (!mScroller.computeScrollOffset()) return;
		
		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		
		this.invalidate();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mVelocityTracker.addMovement(ev);
		if (ev.getAction() ==MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP)
			if (isXLocked)
				this.unlockX();
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TouchState.REST)
				&& !isLockETH && !isXLocked) {
			return true;
		}

		final float x = event.getX();

		switch (action) {

			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int) Math.abs(mLastMotionX - x);
				if (xDiff > mTouchSlop) {
					mTouchState = TouchState.SCROLLING;
					mLastMotionX = x;
					if (xDiff < Math.abs(event.getY() - mDownMotionY)) {
						this.lockX();
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = x;
				mDownMotionY = event.getY();
				mDownMotionX = x;
				mTouchState = mScroller.isFinished() ? TouchState.REST : TouchState.SCROLLING;
				break;

			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mTouchState = TouchState.REST;
				isLockETH = isXLocked = false;
				break;
		}
		
		if (isLockETH) return false;

		return mTouchState != TouchState.REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
			case MotionEvent.ACTION_DOWN:

				if (!mScroller.isFinished()) {
					mScroller.forceFinished(true);
				}
				mLastMotionX = x;
				break;

			case MotionEvent.ACTION_MOVE:
				if (this.isXLocked) break;
					
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;
				
				if (mListener != null && deltaX != 0) {
					mListener.onXChange(deltaX);
				}

				final int dst = getScrollX() + deltaX;
//				Log.e("zhujj" , dst + " l:" + mLeft_edge + " r: " + mRight_edge);
				deltaX = dst < mLeft_edge ? 0 : dst > mRight_edge ? 0 : deltaX;
				
				scrollBy(deltaX, 0);
				break;

			case MotionEvent.ACTION_UP:
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				float velocityX = velocityTracker.getXVelocity();
				if (velocityX > SNAP_VELOCITY && mCurScreen > 0) { // left
					snapToScreen(mCurScreen - 1);
				} else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
					snapToScreen(mCurScreen + 1);
				} else {
					snapAccordCurrX();
				}

				reset();
				break;

			case MotionEvent.ACTION_CANCEL:
				snapAccordCurrX();
				
				reset();
				break;
		}

		return true;
	}
	
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);
			if (childView instanceof GalleryChildListener) {
				((GalleryChildListener) childView).onScrollXChanged(l);
			}
		}
	}
	
	private void reset() {
		this.unlockX();
		mVelocityTracker.clear();
		mTouchState = TouchState.REST;
	}
	
	private void snapAccordCurrX() {
		int destScreen;
		if (getScrollX() > (getDestScrollIdx(1) >> 1)) {
			destScreen = 1;
		} else {
			destScreen = 0;
		}
		snapToScreen(destScreen);
	}

	public void setGalleryChangeListener(DsHomeGalleryListener aListener) {
		mListener = aListener;
	}

	public interface DsHomeGalleryListener {
		public void onGalleryScreenChanged(View aView, int aScreen);
		public void onGalleryScreenChangeComplete(View aView, int aScreen);
		public void onXChange(int aDelta);
	}
	
	public interface GalleryChildListener {
		void onScrollXChanged(int currX);
	}
}
