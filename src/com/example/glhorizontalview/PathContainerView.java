package com.example.glhorizontalview;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.ds.views.PathSelector;
import com.ds.views.PathSelector.PathListener;

public class PathContainerView extends ViewGroup implements PathListener, OnClickListener {
	private MyGLSurfaceView mGLSurfaceView;
	private PathSelector mPathSelector;
	private HorizontalScrollView mHorizontalView;
	private FolderPicturesModel mModel;
	private MyRenderer mRender;
	private ModeSWitchVIew mSwitchBtn;

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

			mGLSurfaceView.setRenderer(mRender = new MyRenderer(context, mGLSurfaceView,
//					null
					mModel = new FolderPicturesModel(context, this)
					));
		} else {
			Toast.makeText(context,
					"not support GLES2, require better machine",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		mPathSelector = new PathSelector(context);
		mPathSelector.setCurrPath(mModel.InitPath());
		mPathSelector.setListener(this);
		mHorizontalView = new HorizontalScrollView(context);
		
		this.addView(mGLSurfaceView);
		mHorizontalView.addView(mPathSelector);
		this.addView(mHorizontalView);
		
		mSwitchBtn = new ModeSWitchVIew(context);
		this.addView(mSwitchBtn);
		mSwitchBtn.setOnClickListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int height = this.getMeasuredHeight();
		int width = this.getMeasuredWidth();
		
		int h = mPathSelector.getMeasuredHeight();
		
		mHorizontalView.layout(0, 0, width - 100, h);
		mSwitchBtn.layout(width - 100, 0	, width, h);
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

	@Override
	public void onPathChange(String abspath) {
		mModel.loadPathContent(abspath, true);
	}

	public void insideClickAtPath(String absPath) {
		mPathSelector.setCurrPath(absPath);
		mModel.loadPathContent(absPath, true);
		
	}
	
	private class ModeSWitchVIew extends View {

		public ModeSWitchVIew(Context context) {
			super(context);
			
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xff880000);
		}

	}

	@Override
	public void onClick(View v) {
		mRender.changeRenderMode(-1);
		
	}

}
