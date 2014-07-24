package com.example.bitmaploader;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapHelper;
import com.ds.bitmaputils.Cbitmap;
import com.ds.io.DsLog;

public class MainActivity extends Activity {

	private String[] mKeys;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadBitmaps();
		this.setContentView(new ImageView(this));
	}
	
	// same string value has same hashcode, different value has different hashcode

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
		
		for (int i = 0; i < files.length; i++) {
			mKeys[i] = files[i].getAbsolutePath();
		}
	}
	
	private class ImageView extends View {

		private Bitmap mBp;
		private int mIdx;
		private Paint mPaint;
		public ImageView(Context context) {
			super(context);
			mIdx = 0;
			mPaint = new Paint();
			mPaint.setColor(0xffffffff);
			mPaint.setTextSize(50);
		}

		
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(0xff000000);
			
			AtomBitmap abp = BitmapHelper.getInstance(getContext()).getBitmap(mKeys[mIdx]);
			mBp = abp.getBitmap();
			if (mBp != null) {
				canvas.drawBitmap(mBp, 0, 0, null);
			}
			canvas.drawText(Integer.toString(mIdx), 0, 50, mPaint);
			
			mIdx ++;
			mIdx = mIdx > mKeys.length ? 0 : mIdx;
			this.postInvalidateDelayed(1000);
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
