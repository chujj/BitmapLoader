package com.example.glhorizontalview.data;

import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

import com.ds.bitmaputils.BitmapHelper;
import com.example.bitmaploader.R;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.controll.PathContainerView;

import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenu.DsPopMenuClickListener;

public class FolderPicturesModel implements GLResourceModel {

	private Stack<IData> mIDataStack;
	
	private Context mContext;
	public PathContainerView mPathClickListener;
	private HomeData mHomeData;

	public FolderPicturesModel(Context context, PathContainerView pathContainerView) {
		mContext = context;
		BitmapHelper.getInstance(mContext);
		mIDataStack = new Stack<IData>(); // ZHUJJ-TODO search stack find same model while path is same
		
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
			tellFatherTestTopOnUi();
		}

	}
	
	private void tellFatherTestTopOnUi() {
		mPathClickListener.post(new Runnable() {
			
			@Override
			public void run() {
				mPathClickListener.modelChanged();
			}
		});
	}

	public void loadPathContent(String path, boolean reload) {
		LoadPathRunnable run = new LoadPathRunnable(path);
		if (reload) {
			mRender.modelChanged(run);
		} else {
			run.run();
		}

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
	
	public boolean supportSort() {
		return mIDataStack.peek().supportSort(-1);
	}
	
	public void sort(int sortName) {
		mRender.modelChanged(new SortRunnable(sortName));
	}
	
	@Override
	public void clickAt(int hit) {
		mIDataStack.peek().clickAt(hit);
	}
	
	@Override
	public void longClick(float x , float y , int hit) {
		mIDataStack.peek().longClick(x, y, hit);
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
		
		mPathClickListener.clickAtPathFromView(nextAbsPath);
	}
	
	private class PopStack implements Runnable {

		@Override
		public void run() {
			if (!mIDataStack.empty()) {
				IData poped = mIDataStack.pop();
				poped.toString();
				tellFatherTestTopOnUi();
			}
		}
		
	}
	
	public String getPath() {
		if (mIDataStack.peek() instanceof FolderData) {
			return ((FolderData)mIDataStack.peek()).getmPath();
		} 
		return null;
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

	public boolean isTopLocalFolder() {
		if (mIDataStack.peek() instanceof FolderData) {
			return true;
		}
		return false;
	}

	public void showMenuForFolder(FolderData folderData, String absPath,
			String fName, boolean isFolder, float x, float y) {
		DsPopMenu menu = new DsPopMenu(getContext());
		menu.addPopMenuItem(new PathContainerView.MenuItem(getContext(), mContext.getString(R.string.menu_add_fav), 1));
		menu.setPopMenuClickListener(new FolderMenuListener(absPath, fName, isFolder));
		mPathClickListener.showMenu(menu, x, y);
	}
	
	private class FolderMenuListener implements DsPopMenuClickListener {
		
		private String mAbsPath;
		private String mFName;
		private boolean mIsFolder;

		public FolderMenuListener(String absPath, String fName, boolean isFolder) {
			mAbsPath = absPath;
			mFName = fName;
			mIsFolder = isFolder;
		}

		@Override
		public void onPopMenuItemClick(int aPopMenuId, int aPopMenuItemId) {
			if (aPopMenuItemId == 1) {
				mHomeData.tryAddFav(mAbsPath, mFName, mIsFolder);
			}
			
			mPathClickListener.dismissMenu();
		}
		
	}

	public void homedataReloaded() {
		if (mIDataStack.peek() == mHomeData) {
			mRender.modelChanged(null);
		} else {
			; // nothing
		}
		
	}
	
	public void showMenuForHome(DsPopMenu menu, float x, float y) {
		mPathClickListener.showMenu(menu, x, y);
	}


}
