package com.ds.views;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class PathSelector extends ViewGroup implements OnClickListener {
	private String initPath = "/sdcard/";
	
	private String pathString;

	public PathSelector(Context context) {
		super(context);
		initPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		this.setCurrPath(initPath);
		this.setBackgroundColor(0xffff0000);
	}
	
	public void setCurrPath(String path) {
		File next = new File(path);
		if (next.exists() && next.isDirectory()) {
			pathString = path;
		} else {
			pathString = initPath;
		}
		refreshView();
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
		this.setMeasuredDimension(width, height);
	}

	private class MyTextView extends TextView {

		private String mAbsPath;
		public MyTextView(Context context, String name, String absPath) {
			super(context);
			if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(absPath)) { // may be root "/"
				name = "/";
			} else {
				name += "/";
			}
			this.setText(Html.fromHtml(name).toString());
			this.setSingleLine();
			this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			mAbsPath = absPath;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension((int) (this.getTextSize() * this.getText().length()), (int) this.getTextSize());
		}

	}

	@Override
	public void onClick(View v) {
		MyTextView tv = (MyTextView) v;
		this.setCurrPath(tv.mAbsPath);
	}
	
	
}
