package com.ds.views;

import com.ds.io.DsLog;

import android.content.Context;
import android.util.FloatMath;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class MyScroller {

    private int mMode;

    private float mStartX;
    private float mStartY;
    private float mFinalX;
    private float mFinalY;
    private float mCurrX;
    private float mCurrY;
    private float mDeltaX;
    private float mDeltaY;

    private float mMinX;
    private float mMaxX;
    private float mMinY;
    private float mMaxY;

    private long mStartTime;
    private long mDuration;
    private float mDurationReciprocal;
    private boolean mFinished;
    private Interpolator mInterpolator;

    private float mVelocity;

    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;

    private static float DECELERATION_RATE = (float) (Math.log(0.75) / Math.log(0.9));
    private static float ALPHA = 800; // pixels / seconds
    private static float START_TENSION = 0.4f; // Tension at start: (0.4 * total T, 1.0 * Distance)


    /**
     * Create a Scroller with the default duration and interpolator.
     */
    public MyScroller(Context context) {
        mFinished = true;
        mInterpolator = new DecelerateInterpolator() ;
    }

    /**
     * 
     * Returns whether the scroller has finished scrolling.
     * 
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public final boolean isFinished() {
        return mFinished;
    }
    
    /**
     * Force the finished field to a particular value.
     *  
     * @param finished The new finished value.
     */
    public final void forceFinished(boolean finished) {
        mFinished = finished;
        flingEnd = null;
    }
    
    /**
     * Returns how long the scroll event will take, in milliseconds.
     * 
     * @return The duration of the scroll in milliseconds.
     */
    public final long getDuration() {
        return mDuration;
    }
    
    /**
     * Returns the current X offset in the scroll. 
     * 
     * @return The new X offset as an absolute distance from the origin.
     */
    public final float getCurrX() {
        return mCurrX;
    }
    
    /**
     * Returns the current Y offset in the scroll. 
     * 
     * @return The new Y offset as an absolute distance from the origin.
     */
    public final float getCurrY() {
        return mCurrY;
    }

    /**
     * Call this when you want to know the new location.  If it returns true,
     * the animation is not yet finished.  loc will be altered to provide the
     * new location.
     */ 
    public boolean computeScrollOffset() {
        if (mFinished) {
            return false;
        }

        long timePassed = (System.currentTimeMillis() - mStartTime);
    
        if (timePassed < mDuration) {
            switch (mMode) {
            case SCROLL_MODE:
//            	DsLog.e("in scroll mode");
                float x = timePassed * mDurationReciprocal;

                x = mInterpolator.getInterpolation(x);
    
                mCurrX = mStartX + (x * mDeltaX);
                mCurrY = mStartY + (x * mDeltaY);
                break;
            case FLING_MODE:
                float t = timePassed /  (float)mDuration;
                
                t  = mInterpolator.getInterpolation(t);

                mCurrX = mStartX + t * mDeltaX;
                mCurrX = Math.min(mCurrX, mMaxX);
                mCurrX = Math.max(mCurrX, mMinX);
                
                mCurrY = mStartY + t * mDeltaY;
                mCurrY = Math.min(mCurrY, mMaxY);
                mCurrY = Math.max(mCurrY, mMinY);

//                DsLog.e(String.format("t_time:%d, t_past:%.3f, mCurrX:%.3f, finelX:%.3f\n",(int) mDuration, t, mCurrX, mFinalX));
                if ((Math.abs(mCurrX - mFinalX) < 0.001)&& (Math.abs(mCurrY - mFinalY) < 0.001)) {
//                	DsLog.e("fling finished");

                    mFinished = true;
                    if (flingEnd != null) {
                    	flingEnd.run();
                    	flingEnd = null;
                    }
                }

                break;
            }
        }
        else {
//        	DsLog.e("scroller durning over finish");
            mCurrX = mFinalX;
            mCurrY = mFinalY;
            mFinished = true;
            if (FLING_MODE == mMode) {
            	if (flingEnd != null) {
            		flingEnd.run();
            		flingEnd = null;
            	}
            }
        }
        return true;
    }

    public void startScroll(float startX, float startY, float dx, float dy, long duration) {
        mMode = SCROLL_MODE;
        mFinished = false;
        mDuration = duration;
        mStartTime = System.currentTimeMillis();
        mStartX = startX;
        mStartY = startY;
        mFinalX = startX + dx;
        mFinalY = startY + dy;
        mDeltaX = dx;
        mDeltaY = dy;
        mDurationReciprocal = 1.0f / (float) mDuration;
    }

    private Runnable flingEnd;
    public void fling(float startX, float startY, float dx, float dy,
            float minX, float maxX, float minY, float maxY, long duration, Runnable endOfFling) {
    	if (mMode == FLING_MODE && !mFinished) {
    		return ;
    	}

        mMode = FLING_MODE;
        mFinished = false;
        mStartTime = System.currentTimeMillis();
        mDuration = duration;
        flingEnd = endOfFling;
        
        mStartX = startX;
        mStartY = startY;
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;

        mDeltaX = dx;
        mDeltaY = dy;

        mFinalX = startX + dx;
        // Pin to mMinX <= mFinalX <= mMaxX
        mFinalX = Math.min(mFinalX, mMaxX);
        mFinalX = Math.max(mFinalX, mMinX);

        mFinalY = startY + dy;
        // Pin to mMinY <= mFinalY <= mMaxY
        mFinalY = Math.min(mFinalY, mMaxY);
        mFinalY = Math.max(mFinalY, mMinY);
    }

    /**
     * Stops the animation. Contrary to {@link #forceFinished(boolean)},
     * aborting the animating cause the scroller to move to the final x and y
     * position
     *
     * @see #forceFinished(boolean)
     */
    public void abortAnimation() {
        mCurrX = mFinalX;
        mCurrY = mFinalY;
        mFinished = true;
    }

	public boolean inFlingMode() {
		return mMode == FLING_MODE;
	}

}
