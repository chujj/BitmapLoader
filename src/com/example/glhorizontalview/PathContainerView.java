package com.example.glhorizontalview;

import com.ds.views.PathSelector;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

public class PathContainerView extends ViewGroup {
	private GLSurfaceView mGLSurfaceView;
	private PathSelector mPathSelector;
	private HorizontalScrollView mHorizontalView;

	public PathContainerView(Context context) {
		super(context);

		mGLSurfaceView = new MyGLSurfaceView(context);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) {

			mGLSurfaceView.setEGLContextClientVersion(2);

			mGLSurfaceView.setRenderer(new MyRenderer(context, mGLSurfaceView,
					null
			// new DCIMCameraModel(this)
					));
		} else {
			Toast.makeText(context,
					"not support GLES2, require better machine",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		mPathSelector = new PathSelector(context);
		mHorizontalView = new HorizontalScrollView(context);
		
		this.addView(mGLSurfaceView);
		mHorizontalView.addView(mPathSelector);
		this.addView(mHorizontalView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int height = this.getMeasuredHeight();
		int width = this.getMeasuredWidth();
		
		int h = mPathSelector.getMeasuredHeight();
		
		mHorizontalView.layout(0, 0, width, h);
		mGLSurfaceView.layout(0, h, width, height);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		for (int i = 0; i < this.getChildCount(); i++) {
			this.getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		
		this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), 
				MeasureSpec.getSize(heightMeasureSpec));
	}

	public void onResume() {
		mGLSurfaceView.onResume();
	}

	public void onPause() {
		mGLSurfaceView.onPause();
	}

}
