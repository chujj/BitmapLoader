package com.example.glhorizontalview.data;

import java.io.File;
import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;

import com.ds.bitmaputils.BitmapHelper;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.controll.PathContainerView;

public class FolderPicturesModel implements GLResourceModel {

	private Stack<IData> mIDataStack;
	
	private Context mContext;
	private String initPath = "/";
	public PathContainerView mPathClickListener;
	private HomeData mHomeData;

	public FolderPicturesModel(Context context, PathContainerView pathContainerView) {
		mContext = context;
		BitmapHelper.getInstance(mContext);
		mIDataStack = new Stack<IData>();
		
		mIDataStack.push(mHomeData = HomeData.build(this));

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
	
	private class LoadPathRunnable implements Runnable {
		private String mPath;
		public LoadPathRunnable(String path) {
			mPath = path;
		}
		
		@Override
		public void run() {
			mIDataStack.push(new FolderData(FolderPicturesModel.this, mPath));
		}

	}

	public void loadPathContent(String path, boolean reload) {
		LoadPathRunnable run = new LoadPathRunnable(path);
		if (reload) {
			mRender.modelChanged(run);
		} else {
			run.run();
		}

	}

	public String InitPath() {
		return initPath;
	}
	
	private class SortRunnable implements Runnable {
		private int mSortFlag;
		public SortRunnable(int sortflag) {
			mSortFlag = sortflag;
		}

		@Override
		public void run() {
			mIDataStack.peek().sort(mSortFlag);
		}
		
	}
	
	public void sort(int sortName) {
		mRender.modelChanged(new SortRunnable(sortName));
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

	protected void clickAtPathInside(IData folderData, int hit, String nextAbsPath) {
		if (folderData == null || mIDataStack.peek() != folderData) return;
		
		mPathClickListener.insideClickAtPath(nextAbsPath);
	}
	
	private class PopStack implements Runnable {

		@Override
		public void run() {
			if (!mIDataStack.empty()) {
				IData poped = mIDataStack.pop();
				poped.toString();
			}
		}
		
	}

	public boolean backPressed() {
		if (mIDataStack.size() == 1) {
			return false;
		} else if (mIDataStack.size() > 1) {
			mRender.modelChanged(new PopStack());
			return true;
		} else {
			return false;
		}
		
	}

	public void onPause() {
		mHomeData.serilizedToFile();
	}

}
