package com.example.glhorizontalview;

import java.io.File;
import java.io.FileFilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapGotCallBack;
import com.ds.bitmaputils.BitmapHelper;
import com.example.bitmaploader.R;

public class FolderPicturesModel implements GLResourceModel {

	private Bitmap mDefaultBitmap, mFolderBitmap;
	private Rect mRect;
	private Item[] mKeys;
	private Context mContext;
	private Paint mPaint, mBgPaint;
	private String initPath;
	private PathContainerView mPathClickListener;

	public FolderPicturesModel(Context context, PathContainerView pathContainerView) {
		mRect = new Rect();
		mContext = context;
		mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		mFolderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder);
		BitmapHelper.getInstance(mContext);
		loadPathContent(initPath = getInitPath(), false);
		mPaint = new Paint();
		mPaint.setTextSize(30);
		mPathClickListener = pathContainerView;
		mBgPaint = new Paint();
		mBgPaint.setColor(0xffc3c3c3);
	}

	@Override
	public int getCount() {
		return mKeys.length;
	}

	@Override
	public void updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		mRect.set(0, 0, require_width, require_height);
		mC.drawRect(mRect, mBgPaint);
		
		if (mKeys[aIdx].isFolder) {
			mC.drawBitmap(mFolderBitmap, null, mRect, null);
			mC.drawText(mKeys[aIdx].fName, 0, 40, mPaint);
		} else {
			AtomBitmap abp = BitmapHelper.getInstance(mContext).getBitmap(
					mKeys[aIdx].absPath);
			Bitmap bp = abp.getBitmap(mKeys[aIdx]);
			if (bp != null) {
				final int b_w = bp.getWidth();
				final int b_h = bp.getHeight();
				if (b_w <= require_width && b_h <= require_height) { // center aligned
					mC.drawBitmap(bp, (require_width - b_w) / 2, (require_height - b_h) /2, null);
				} else { // scaled fix given rect size
					final float s_w = 1.0f * require_width / b_w;
					final float s_h = 1.0f * require_height / b_h;
					final float f_s = Math.min(s_w, s_h);
					final int f_w = (int) (b_w * f_s);
					final int f_h = (int) (b_h * f_s);
					mRect.set(0, 0, f_w, f_h);
					mRect.offset( (require_width - f_w) / 2, (require_height - f_h) / 2);
					mC.drawBitmap(bp, null, mRect, null);
				}
			} else {
				
			}
		}
	}
	
	private class MyLoadPathRunnable implements Runnable {

		private String mPath;
		public MyLoadPathRunnable(String path) {
			mPath = path;
		}
		
		@Override
		public void run() {
			File dir = new File(mPath);
			File[] files = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					boolean retval;
					String name = arg0.getName();
					name = name.length() > 3 ? name.substring(name.length() - 3) : name;
					retval = name.equalsIgnoreCase("jpg") ? true : 
						name.equalsIgnoreCase("png") ? true : 
							arg0.isDirectory() ? arg0.getName().startsWith(".") ? false : true
									: false;
					return retval;
				}
			});
			
			mKeys = new Item[files.length];
			
			for (int i = 0; i < files.length; i++) {
				mKeys[i] = new Item(i, files[i].isDirectory(), files[i].getName(),
						files[i].getAbsolutePath());
			}
		}

	}

	public void loadPathContent(String path, boolean reload) {
		MyLoadPathRunnable run = new MyLoadPathRunnable(path);
		if (reload) {
			mRender.modelChanged(run);
		} else {
			run.run();
		}

	}

	private class Item implements BitmapGotCallBack {
		boolean isFolder;
		String absPath;
		String fName;
		int mIdx;

		public Item(int idx, boolean folder, String foldername, String path) {
			mIdx = idx;
			isFolder = folder;
			if (isFolder) {
				fName = foldername;
			}
			absPath = path;
		}

		@Override
		public void onBitmapGot(Bitmap aBitmap) {
			mRender.refreshIdx(mIdx);
		}
	}
	
	private String getInitPath() {
		File rootsd = Environment.getExternalStorageDirectory();
		File dcim = new File(rootsd.getAbsolutePath() + "/wallpapers" ); //"/DCIM");
		if (dcim.exists()) {
			return dcim.getAbsolutePath();
		} else {
			return "/";
		}
	}

	public String InitPath() {
		return initPath;
	}

	@Override
	public void clickAt(int hit) {
		if (mKeys[hit].isFolder) {
			mPathClickListener.insideClickAtPath(mKeys[hit].absPath);
		}
		
	}

	private MyRenderer mRender;
	@Override
	public void currRenderView(MyRenderer render) {
		mRender = render;
	}

}
