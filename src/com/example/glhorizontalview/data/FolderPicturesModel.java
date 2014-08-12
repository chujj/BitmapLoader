package com.example.glhorizontalview.data;

import java.io.File;
import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;

import com.ds.bitmaputils.BitmapHelper;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.PathContainerView;

public class FolderPicturesModel implements GLResourceModel {

	private Stack<IData> mIDataStack;
	
	private Context mContext;
	private String initPath;
	private PathContainerView mPathClickListener;

	public FolderPicturesModel(Context context, PathContainerView pathContainerView) {
		mContext = context;
		BitmapHelper.getInstance(mContext);
		mIDataStack = new Stack<IData>();
		loadPathContent(initPath = getInitPath(), false);
		
		mPathClickListener = pathContainerView;
	}

	@Override
	public int getCount() {
		return mIDataStack.peek().getCount();
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		return mIDataStack.peek().updateToCanvas(aIdx, mC, require_width, require_height);
	}
	
	private class MyLoadPathRunnable implements Runnable {

		private String mPath;
		public MyLoadPathRunnable(String path) {
			mPath = path;
		}
		
		@Override
		public void run() {
			mIDataStack.push(new FolderData(FolderPicturesModel.this, mPath));
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

	private String getInitPath() {
		File rootsd = Environment.getExternalStorageDirectory();
		File dcim = new File(rootsd.getAbsolutePath() + "/wallpapers" ); //"/DCIM");
		if (dcim.exists()) {
			return dcim.getAbsolutePath();
		} else {
			return rootsd.getAbsolutePath();
		}
	}

	public String InitPath() {
		return initPath;
	}

	@Override
	public void clickAt(int hit) {
		mIDataStack.peek().clickAt(hit);
	}

	private MyRenderer mRender;
	@Override
	public void currRenderView(MyRenderer render) {
		mRender = render;
	}

	protected Context getContext() {
		return mContext;
	}

	protected void onBitmapGot(FolderData folderData, int mIdx) {
		// only refresh which on top of stack, currently showing
		if (folderData == null || mIDataStack.peek() != folderData) return;
		
		mRender.refreshIdx(mIdx);
	}

	protected void clickAtPathInside(FolderData folderData, int hit, String nextAbsPath) {
		if (folderData == null || mIDataStack.peek() != folderData) return;
		
		mPathClickListener.insideClickAtPath(nextAbsPath);
	}

}
