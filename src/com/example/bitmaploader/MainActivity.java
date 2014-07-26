package com.example.bitmaploader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapHelper;
import com.ds.bitmaputils.BitmapHelper.LEVEL;
import com.ds.bitmaputils.Cbitmap;
import com.ds.io.DsLog;
import com.example.httepsernvertest.MyServer;
import com.example.httepsernvertest.MyServer.Responce;

public class MainActivity extends Activity {

	private String[] mKeys;
	private MyServer mServer;
	private View mView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadBitmaps();
		this.setContentView(mView = new ImageView(this));
		try {
			mServer = new MyServer(res);
			mServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mView.postDelayed(recycleCall, 5000);
		
	}
	
	private Runnable recycleCall = new Runnable() {
		
		@Override
		public void run() {
			BitmapHelper.getInstance(MainActivity.this).recycleBitmaps();
			mView.postDelayed(recycleCall, 5000);
		}
	};
	
	private Responce res = new Responce() {
		
		@Override
		public String getString() {
			return BitmapHelper.getInstance(MainActivity.this).dumpAllAtomBitmaps();
		}
	};
	

	@Override
	protected void onDestroy() {
		if (mServer != null) {
			mServer.stop();
		}
		super.onDestroy();
	}

	private void loadBitmaps() {
		String testDir = "/sdcard/wallpapers";
		File dir = new File(testDir);
		File[] files = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				boolean retval;
				retval = arg0.getName().endsWith("jpg") ? true : false;
				return retval;
			}
		});
		
		mKeys = new String[files.length];
		
		for (int i = 0; i < 10; i++) {
			mKeys[i] = files[i].getAbsolutePath();
		}
	}
	
	private class ImageView extends View {

		private Bitmap mBp;
		private int mIdx;
		private Paint mPaint;
		private Random mRandom;
		public ImageView(Context context) {
			super(context);
			mIdx = 0;
			mPaint = new Paint();
			mPaint.setColor(0xffffffff);
			mPaint.setTextSize(50);
			mRect = new Rect();
			mRandom = new Random();
		}

		private LEVEL mCurrLevel = LEVEL.THUMBNAIL;
		private Rect mRect;
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xff000000);
			
			int i = 			mRandom.nextInt(3);
			if (i == 0) {
				mCurrLevel = LEVEL.THUMBNAIL;
			} else if (i == 1) {
				mCurrLevel = LEVEL.FITSCREEN;
			} else if (i == 2) {
				mCurrLevel = LEVEL.ORIGIN;
			}
			
			mIdx = mRandom.nextInt(55);
			int[] random = new int[] {
					1, 
					3, 
					6,
					10,
					15,
					21,
					28,
					36,
					45,
					55,
			};
			i = 0;
			while (i < random.length) {
				if (i == 0) {
					if (mIdx <= random[i]) {
						break;
					}
				} else if (mIdx > random[i - 1] && mIdx <= random[i]) {
					break;
				}
				i ++;
			}
			mIdx = i;
		

			AtomBitmap abp = BitmapHelper.getInstance(getContext()).getBitmap(mKeys[mIdx], mCurrLevel);
			mBp = abp.getBitmap();
			if (mBp != null) {
				canvas.drawBitmap(mBp, null, mRect, null);
			}
			canvas.drawText(Integer.toString(mIdx), 0, 50, mPaint);

			this.postInvalidate();
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mRect.set(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
		}

	}
	
	private void testHashOfString() {
		String test = "/sdcard/wt/test.jpg";
		DsLog.e(Integer.toHexString(test.hashCode()));
		test = "/sdcard" + "/wt" + "/test" + ".jpg";
		DsLog.e(Integer.toHexString(test.hashCode()));
		test = "/sdcar" + "d/wt" + "/test" + ".jpg";
		DsLog.e(Integer.toHexString(test.hashCode()));

		test = "http://pic.bandaonews.com/PicView.aspx?id=37219";
		DsLog.e(Integer.toHexString(test.hashCode()));
		test = "http://tech.163.com/05/0829/09/1SAIIRG8000915BD.html";
		DsLog.e(Integer.toHexString(test.hashCode()));
	}



}
