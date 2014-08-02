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
import com.ds.bitmaputils.BitmapHelper;
import com.example.bitmaploader.R;

public class DCIMCameraModel implements GLResourceModel {

	private Bitmap mDefaultBitmap, mFolderBitmap;
	private Rect mRect;
	private Item[] mKeys;
	private Context mContext;
	private Paint mPaint;
	private String initPath;
	private PathContainerView mPathClickListener;

	public DCIMCameraModel(Context context, PathContainerView pathContainerView) {
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
	}

	@Override
	public int getCount() {
		return mKeys.length;
	}

	@Override
	public void updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		mRect.set(0, 0, require_width, require_height);
		if (mKeys[aIdx].isFolder) {
			mC.drawBitmap(mFolderBitmap, null, mRect, null);
			mC.drawText(mKeys[aIdx].fName, 0, 40, mPaint);
		} else {
			AtomBitmap abp = BitmapHelper.getInstance(mContext).getBitmap(
					mKeys[aIdx].absPath);
			Bitmap bp = abp.getBitmap();
			if (bp == null)
				bp = mDefaultBitmap;
			mC.drawBitmap(bp, null, mRect, null);
		}
	}

	public void loadPathContent(String path, boolean reload) {
		if (reload)
			mRender.modelChangedStart();
		
		File dir = new File(path);
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				boolean retval;
				retval = arg0.getName().endsWith("jpg") ? true : 
					arg0.getName().endsWith("png") ? true : 
						arg0.isDirectory() ? arg0.getName().startsWith(".") ? false : true
						: false;
				return retval;
			}
		});

		mKeys = new Item[files.length];

		for (int i = 0; i < files.length; i++) {
			mKeys[i] = new Item(files[i].isDirectory(), files[i].getName(),
					files[i].getAbsolutePath());
		}

		if (reload)
			mRender.modelChangedEnd();
	}

	private class Item {
		boolean isFolder;
		String absPath;
		String fName;

		public Item(boolean folder, String foldername, String path) {
			isFolder = folder;
			if (isFolder) {
				fName = foldername;
			}
			absPath = path;
		}
	}
	
	private String getInitPath() {
		File rootsd = Environment.getExternalStorageDirectory();
		File dcim = new File(rootsd.getAbsolutePath() + "/DCIM");
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
