package com.example.glhorizontalview;

import java.io.File;
import java.io.FileFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapHelper;
import com.example.bitmaploader.R;

public class DCIMCameraModel implements GLResourceModel {

	private Bitmap mDefaultBitmap;
	private Rect mRect;
	private String[] mKeys;
	private Context mContext;
	
	public DCIMCameraModel(Context context) {
		mRect = new Rect();
		mContext = context;
		mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		BitmapHelper.getInstance(mContext);
		loadBitmaps();
	}
	
	@Override
	public int getCount() {
		
		return mKeys.length;
	}

	@Override
	public void updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		mRect.set(0, 0, require_width, require_height);
		AtomBitmap abp = BitmapHelper.getInstance(mContext).getBitmap(mKeys[aIdx]);
		Bitmap bp = abp.getBitmap();
		if (bp == null)
			bp = mDefaultBitmap;
		mC.drawBitmap(bp, null, mRect, null);
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

		for (int i = 0; i < files.length; i++) {
			mKeys[i] = files[i].getAbsolutePath();
		}
		
	}

}
