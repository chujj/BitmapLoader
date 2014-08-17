package com.example.glhorizontalview.controll;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ds.ui.DsBitmapUtil;
import com.ds.ui.DsCanvasUtil;
import com.example.bitmaploader.R;

public class PathSelector extends ViewGroup implements OnClickListener {
	private String initPath = "/";
	
	private String pathString;
	private NinePatchDrawable m9Bg; 

	public PathSelector(Context context) {
		super(context);
		initPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		this.setCurrPath(initPath);
		m9Bg = DsBitmapUtil.getNinePatchDrawable(context, R.drawable.debug_button_focused);
	}
	
	public void setCurrPath(String path) {
		if (path == null || path.length() == 0 || path.equals(pathString)) return;
		
		File next = new File(path);
		if (next.exists() && next.isDirectory()) {
			pathString = path;
		} else {
			pathString = initPath;
		}
		refreshView();
	}
	private PathListener mListener;
	
	public void setListener(PathListener listener) {
		mListener = listener;
	}
	
	public static interface PathListener {
		public void onPathChange(String abspath);
	}
	
	private ArrayList<MyTextView> mLists = new ArrayList<PathSelector.MyTextView>();
	
	private void refreshView() {
		mLists.clear();
		this.removeAllViews();
		
		String path = pathString;
		File pathF = new File(path);
		while (pathF != null) {
			MyTextView tv = new MyTextView(getContext(), pathF.getName(), pathF.getAbsolutePath());
			mLists.add(0, tv);
			tv.setOnClickListener(this);
			this.addView(tv);
			pathF = pathF.getParentFile();
		}
		
		this.postInvalidate();
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int x = 0;
		int height = this.getMeasuredHeight();
		for (int i = 0; i < mLists.size(); i++) {
			MyTextView child = mLists.get(i); 
			child.layout(x, 0, x + child.getMeasuredWidth(), height);
			x += child.getMeasuredWidth();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int  height = 100;
		for (int i = 0; i < mLists.size(); i++) {
			mLists.get(i).measure(widthMeasureSpec, heightMeasureSpec);
			width += mLists.get(i).getMeasuredWidth();
		}
		height = mLists.get(0).getMeasuredHeight();
		width = Math.max(width, getContext().getResources().getDisplayMetrics().widthPixels);
		this.setMeasuredDimension(width, height);
	}

	private final float TextSize = 32f;
	private final float TEXT_BORDER_PADDING = 15f;
	
	private class MyTextView extends View {

		private String mAbsPath;
		private String mName;
		private Paint mPaint;
		private float width, height, text_x, text_y;
		
		public MyTextView(Context context, String name, String absPath) {
			super(context);
			if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(absPath)) { // may be root "/"
				name = "/";
			} else {
				name += "/";
			}
			mName = name;
			mAbsPath = absPath;
			mPaint = new Paint();
			mPaint.setTextSize(TextSize);
			mPaint.setColor(0xff000000);
			
			width = TEXT_BORDER_PADDING *2 + mPaint.measureText(mName);
			height = TEXT_BORDER_PADDING * 2 + TextSize;
			text_x = (width -  mPaint.measureText(mName) )/ 2;
			text_y = DsCanvasUtil.calcYWhenTextAlignCenter((int)height, mPaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			this.setMeasuredDimension((int)width, (int) height);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			m9Bg.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
			m9Bg.draw(canvas);
			canvas.drawText(mName, text_x, text_y, mPaint);
		}

	}

	@Override
	public void onClick(View v) {
		MyTextView tv = (MyTextView) v;
		if (tv.mAbsPath.equals(pathString)) return;
		
		this.setCurrPath(tv.mAbsPath);
		if (mListener != null) {
			mListener.onPathChange(pathString);
		}
	}
	
	
}
