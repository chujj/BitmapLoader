package com.example.glhorizontalview.controll;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.ds.io.DsLog;
import com.ds.ui.DsCanvasUtil;
import com.ds.views.PathSelector;
import com.ds.views.PathSelector.PathListener;
import com.example.glhorizontalview.MyGLSurfaceView;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.data.FolderPicturesModel;
import com.example.glhorizontalview.data.IData;

import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenuItem;
import ds.android.ui.core.DsPopMenuLayout;
import ds.android.ui.core.DsPopMenu.DsPopMenuClickListener;

public class PathContainerView extends ViewGroup implements PathListener, OnClickListener {
	private MyGLSurfaceView mGLSurfaceView;
	private PathSelector mPathSelector;
	private HorizontalScrollView mHorizontalView;
	private FolderPicturesModel mModel;
	private MyRenderer mRender;
	private ModeSWitchVIew mSwitchBtn;
	
	private DsPopMenuLayout mMenuLayout;
	private DsPopMenu mMenu;

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
		
		mMenuLayout = new DsPopMenuLayout(context);
//		mMenuLayout.setBackgroundColor(0xffaa0000);
		this.addView(mMenuLayout);
		
		mMenu = new DsPopMenu(context);
		mMenu.setMaxColumn(1);
		mMenu.addPopMenuItem(new MenuDivider(context, "+Layout", 0)); // ZHUJJ-FIXME update the UI
		mMenu.addPopMenuItem(new MenuDivider(context, "-Grid", 1));
		mMenu.addPopMenuItem(new MenuDivider(context, "-Slide", 2));
		mMenu.addPopMenuItem(new MenuDivider(context, "+Sort", 0));
		mMenu.addPopMenuItem(new MenuDivider(context, "-Time", 3));
		mMenu.addPopMenuItem(new MenuDivider(context, "-Size", 4));
		mMenu.addPopMenuItem(new MenuDivider(context, "-Name", 5));
		mMenu.setPopMenuClickListener(new DsPopMenuClickListener() {
			
			@Override
			public void onPopMenuItemClick(int aPopMenuId, int aPopMenuItemId) {
				if (aPopMenuItemId == 1) {
					mRender.changeRenderMode(MyRenderer.MODE_PLANE);
				} else if (aPopMenuItemId == 2) {
					mRender.changeRenderMode(MyRenderer.MODE_CURVE);
				} else if (aPopMenuItemId == 3) { // s_time
					mModel.sort(IData.SORT_NAME);
				} else if (aPopMenuItemId == 4) { // s_size
					mModel.sort(IData.SORT_SIZE);
				} else if (aPopMenuItemId == 5) { // s_name
					mModel.sort(IData.SORT_NAME);
				}
				
				
				
				mMenuLayout.dismissPopMenu();
			}
		});
	}

	private final int TOOL_BAR_WIDTH = 100;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int height = this.getMeasuredHeight();
		int width = this.getMeasuredWidth();
		
		int h = mPathSelector.getMeasuredHeight();
		
		mHorizontalView.layout(0, 0, width - TOOL_BAR_WIDTH, h);
		mSwitchBtn.layout(width - TOOL_BAR_WIDTH, 0	, width, h);
		mGLSurfaceView.layout(0, h, width, height);
		mMenuLayout.layout(0, 0, width, height);
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
		if (mMenuLayout.isPopMenuShow()) {
			mMenuLayout.dismissPopMenu();
		} else {
			mMenuLayout.showPopMenu(mMenu, new Point(this.getMeasuredWidth() , mPathSelector.getMeasuredHeight()));
		}
//		
	}
	
	private class MenuDivider extends DsPopMenuItem {

		private String mString;
		private Paint mPaint;
		public MenuDivider(Context aContext, String msg, int id) {
			super(aContext);
			mString = msg;
			mPaint = new Paint();
			mPaint.setColor(0xffffffff);
			mPaint.setTextSize(24);
			mPaint.setAntiAlias(true);
			this.setMargin(0);
			this.setId(id);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawText(mString, (this.getMeasuredWidth() - mPaint.measureText(mString)) / 2, DsCanvasUtil.calcYWhenTextAlignCenter(this.getMeasuredHeight(), mPaint),mPaint);
		}

	}

}
