package com.example.glhorizontalview.controll;


import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ds.ui.DsCanvasUtil;
import ssc.software.picviewer.R;
import com.example.glhorizontalview.MyGLSurfaceView;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.controll.PathSelector.PathListener;
import com.example.glhorizontalview.data.FolderPicturesModel;
import com.example.glhorizontalview.data.IData;
import com.umeng.fb.FeedbackAgent;

import ds.android.ui.core.BitmapButton;
import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenu.DsPopMenuClickListener;
import ds.android.ui.core.DsPopMenuItem;
import ds.android.ui.core.DsPopMenuLayout;

public class PathContainerView extends ViewGroup implements PathListener, OnClickListener {
	private MyGLSurfaceView mGLSurfaceView;
	private PathSelector mPathSelector;
	private HorizontalScrollView mHorizontalView;
	private FolderPicturesModel mModel;
	private MyRenderer mRender;
	private View mSwitchBtn, mHomeBtn;
	
	private MySeekBark mSeekbar;
	
	private DsPopMenuLayout mMenuLayout;
	private DsPopMenu mFullMenu, mMenuWithoutSort;
	
	private GalleryViewPager mGalleryViewer;
	private static FeedbackAgent sAgent;
	
	public PathContainerView(Context context) {
		super(context);
		
		if (sAgent == null) {
			sAgent = new FeedbackAgent(context); 
		}
		
		sAgent.sync();

		MENU_TEXT_SIZE_WITHOUT_DENSITY = (int) (20 * context.getResources().getDisplayMetrics().density);
		
		mGLSurfaceView = new MyGLSurfaceView(context);
		this.setBackgroundColor(0xff000000);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) {
			mGLSurfaceView.setEGLContextClientVersion(2);
			mModel = new FolderPicturesModel(context, this);
			mGLSurfaceView.setRenderer(mRender = new MyRenderer(context,
					mGLSurfaceView, mModel));
		} else {
			Toast.makeText(context,
					"not support GLES2, require better machine",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		mPathSelector = new PathSelector(context);
		mPathSelector.setListener(this);
		mHorizontalView = new HorizontalScrollView(context);
		mHorizontalView.setHorizontalScrollBarEnabled(false);
		mPathSelector.setParentHorizontalView(mHorizontalView);
		this.addView(mGLSurfaceView);
		mHorizontalView.addView(mPathSelector);
		this.addView(mHorizontalView);
		modelChanged(); // force change pathselector into invisiable
		
		mHomeBtn = new BitmapButton(context, BitmapFactory.decodeResource(context.getResources(), R.drawable.toolbar_homepage));
//		this.addView(mHomeBtn);
		mHomeBtn.setOnClickListener(this);
		
		Bitmap menu_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.toolbar_menu);
		mSwitchBtn = new BitmapButton(context, menu_bitmap);
		this.addView(mSwitchBtn);
		mSwitchBtn.setOnClickListener(this);
		mPathSelector.setMinWidth(context.getResources().getDisplayMetrics().widthPixels  - menu_bitmap.getWidth());
		
		mMenuLayout = new DsPopMenuLayout(context);

		this.addView(mMenuLayout);

		buildSeekbar(context);
		
		mGalleryViewer = new GalleryViewPager(context);
		mGalleryViewer.setBackgroundColor(0xff000000);
		this.addView(mGalleryViewer);
		mGalleryViewer.setVisibility(View.INVISIBLE);
		
		buildMenu(context);
	}
	
	private void buildSeekbar(Context context) {
		mSeekbar = new MySeekBark(context, new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mRender.jumpFinished();
				mSeekbar.stopToSeek();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mSeekbar.startToSeek();
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (!mSeekbar.isSeeking()) // block none activity motion
					return;
				mRender.jumpToPercent(progress);
			}
		});
		this.addView(mSeekbar);
	}
	
	private void buildMenu(Context context) {
		mFullMenu = new DsPopMenu(context);
		mFullMenu.setMaxColumn(1);
		mFullMenu.addPopMenuItem(new MenuDivider(context, context.getString(R.string.menu_layout_title), 0));
		mFullMenu.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_layout_1), 1));
		mFullMenu.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_layout_2), 2));
		mFullMenu.addPopMenuItem(new MenuDivider(context, context.getString(R.string.menu_sort_title), 0));
		mFullMenu.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_sort_time), 3));
		mFullMenu.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_sort_size), 4));
		mFullMenu.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_sort_name), 5));
		mFullMenu.addPopMenuItem(new MenuDivider(context, context.getString(R.string.menu_feedback), 6));
		
		
		mMenuWithoutSort = new DsPopMenu(context);
		mMenuWithoutSort.setMaxColumn(1);
		mMenuWithoutSort.addPopMenuItem(new MenuDivider(context, context.getString(R.string.menu_layout_title), 0));
		mMenuWithoutSort.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_layout_1), 1));
		mMenuWithoutSort.addPopMenuItem(new MenuItem(context, context.getString(R.string.menu_layout_2), 2));
		mMenuWithoutSort.addPopMenuItem(new MenuDivider(context, context.getString(R.string.menu_feedback), 6));

		MenuListener listener = new MenuListener();
		
		mFullMenu.setPopMenuClickListener(listener);
		mMenuWithoutSort.setPopMenuClickListener(listener);
	}
	
	
	private class MenuListener implements DsPopMenuClickListener {
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
			} else if (aPopMenuItemId == 6) { // feedback
				sAgent.startFeedbackActivity();
			}

			mMenuLayout.dismissPopMenu();
		}
	}
	
	public void showGallery(MyPagerAdapter pagerAdapter) {
		mGalleryViewer.setAdapter(pagerAdapter);
		mGalleryViewer.setVisibility(View.VISIBLE);
		this.postInvalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int height = this.getMeasuredHeight();
		int width = this.getMeasuredWidth();
		
		int h = mPathSelector.getMeasuredHeight();
		
		int TOOL_BAR_WIDTH = mSwitchBtn.getMeasuredWidth(); 
		mHorizontalView.layout(0, 0, width - TOOL_BAR_WIDTH , h);
//		mHomeBtn.layout(width - TOOL_BAR_WIDTH - TOOL_BAR_WIDTH, 0	, width - TOOL_BAR_WIDTH, h);
		mSwitchBtn.layout(width - TOOL_BAR_WIDTH, 0	, width, h);
		mGLSurfaceView.layout(0, h, width, height);
		mMenuLayout.layout(0, 0, width, height);
		mGalleryViewer.layout(0, 0, width, height);
		
		mSeekbar.layout(0, height - mSeekbar.getMeasuredHeight(), width, height);
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
		mModel.onPause();
	}

	@Override
	public void onPathChange(String abspath) {
		mModel.loadPathContent(abspath);
	}

	public void clickAtPathFromView(String absPath) {
		mPathSelector.setCurrPath(absPath);
		mModel.loadPathContent(absPath);
	}
	
	public void setPathOnly(String absPath) {
		mPathSelector.setCurrPath(absPath);
	}

	@Override
	public void onClick(View v) {
		if (v == mHomeBtn) {
			
		} else if (v == mSwitchBtn) {
			if (mMenuLayout.isPopMenuShow()) {
				mMenuLayout.dismissPopMenu();
			} else {
				mMenuLayout.showPopMenu(mModel.supportSort() ? mFullMenu : mMenuWithoutSort, new Point(this.getMeasuredWidth() , mPathSelector.getMeasuredHeight()));
			}
		}
	}
	
	public void showMenu(DsPopMenu menu, float x, float y) {
		mMenuLayout.showPopMenu(menu, new Point((int)x,(int) y));
	}
	
	public void dismissMenu() {
		mMenuLayout.dismissPopMenu();
	}
	
	private static int MENU_TEXT_SIZE_WITHOUT_DENSITY = 20;
	
	private class MenuDivider extends MenuItem {

		public MenuDivider(Context aContext, String msg, int id) {
			super(aContext, msg, id);
			mString = msg;
			mPaint.setColor(0xffffffff);
			this.setBackgroundColor(0xff43ace8);
		}
	}

	public static class MenuItem extends DsPopMenuItem {
		protected String mString;
		protected Paint mPaint;
		public MenuItem(Context aContext, String msg, int id) {
			super(aContext);
			mString = msg;
			mPaint = new Paint();
			mPaint.setColor(0xff000000);
			mPaint.setTextSize(MENU_TEXT_SIZE_WITHOUT_DENSITY);
			mPaint.setAntiAlias(true);
			this.setMargin(0);
			this.setId(id);
			this.setBackgroundColor(0xff888888);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			canvas.drawText(mString,  (this.getMeasuredWidth() - mPaint.measureText(mString)) / 2, DsCanvasUtil.calcYWhenTextAlignCenter(this.getMeasuredHeight(), mPaint),mPaint);
		}


		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension(MENU_TEXT_SIZE_WITHOUT_DENSITY * 5, MENU_TEXT_SIZE_WITHOUT_DENSITY * 2);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mGalleryViewer.getVisibility() == View.VISIBLE) {
				mGalleryViewer.setVisibility(View.INVISIBLE);
				return true;
			} else 
				return mModel.backPressed();
		}
		return false;
	}

	/**
	 *  // Called From UI thread
	 */
	public void modelChanged() {
		if (mModel.isTopLocalFolder()) {
			mPathSelector.setCurrPath(mModel.getPath());
			mHorizontalView.setVisibility(View.VISIBLE); 
		} else {
			mHorizontalView.setVisibility(View.INVISIBLE);
		}
		
	}

	public void onOffsetDrawed(float mCurrOffset, float calced_max_offset,
			float calced_min_offset) {
		mSeekbar.setCurrProgress(mCurrOffset, calced_max_offset, calced_min_offset);
	}

}
