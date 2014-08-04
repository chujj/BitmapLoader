package com.example.glhorizontalview;

import com.ds.io.DsLog;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView implements OnGestureListener {

	private MyRenderer mRender;
	private IEvent mEvent;
	private GestureDetector mGestureDetector;
	public MyGLSurfaceView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context, this);
	}
	

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		mRender = (MyRenderer) renderer;
		mEvent = mRender.eventHandler;
		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean retval = false;
		if (mGestureDetector.onTouchEvent(event)) {
			retval = true;
		} else {
			retval = super.onTouchEvent(event);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL || 
				event.getAction() == MotionEvent.ACTION_UP) {
			mEvent.onFinish(event.getX(), event.getY());
		}
		return retval;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mEvent.onClick(e.getX() / this.getMeasuredWidth(), 1 - (e.getY() / this.getMeasuredHeight()));
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		mEvent.onScroll(-distanceX / 200, distanceY);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		mEvent.onFling(velocityX / this.getContext().getResources().getDisplayMetrics().widthPixels, velocityY / 80);
		return true;
	}

}
