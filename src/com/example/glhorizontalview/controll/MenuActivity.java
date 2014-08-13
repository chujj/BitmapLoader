package com.example.glhorizontalview.controll;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import com.ds.ui.DsCanvasUtil;

import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenuItem;
import ds.android.ui.core.DsPopMenuLayout;

public class MenuActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = this;
		
		DsPopMenuLayout mMenuLayout = new DsPopMenuLayout(context);
		this.setContentView(mMenuLayout);
		
		DsPopMenu mMenu = new DsPopMenu(context);
		mMenu.setMaxColumn(1);
		mMenu.addPopMenuItem(new MenuDivider(context, " Layout", 0));
		mMenu.addPopMenuItem(new MenuItem(context, " Grid", 1));
		mMenu.addPopMenuItem(new MenuItem(context, " Slide", 2));
		mMenu.addPopMenuItem(new MenuDivider(context, " Sort", 0));
		mMenu.addPopMenuItem(new MenuItem(context, " Time", 3));
		mMenu.addPopMenuItem(new MenuItem(context, " Size", 4));
		mMenu.addPopMenuItem(new MenuItem(context, " Name", 5));
		
		mMenuLayout.showPopMenu(mMenu);
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
			this.setBackgroundColor(0xff43ace8);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawText(mString, 0, DsCanvasUtil.calcYWhenTextAlignCenter(this.getMeasuredHeight(), mPaint),mPaint);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension(100, 40);
		}
		
		
	}

	private class MenuItem extends DsPopMenuItem {

		private String mString;
		private Paint mPaint;
		public MenuItem(Context aContext, String msg, int id) {
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
			canvas.drawText(mString, 24, DsCanvasUtil.calcYWhenTextAlignCenter(this.getMeasuredHeight(), mPaint),mPaint);
		}
		

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension(100, 40);
		}
	}

}
