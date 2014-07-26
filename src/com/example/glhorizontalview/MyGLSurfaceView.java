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
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		} else 
			return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// ZHUJJ Auto-generated method stub
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mEvent.onFinish(e.getX(), e.getY());
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
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// ZHUJJ Auto-generated method stub
		return false;
	}



}
