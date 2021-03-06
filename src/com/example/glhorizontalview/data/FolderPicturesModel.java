package com.example.glhorizontalview.data;

import java.io.File;
import java.util.Stack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;

import com.ds.bitmaputils.BitmapHelper;
import ssc.software.picviewer.R;
import ssc.widget.data.BoardsModel;
import ssc.widget.data.HBoard;
import ssc.widget.data.PinsModel;

import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
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

	@Override
	public void deprecateToDraw(int aIdx) {
		mIDataStack.peek().deprecateToDraw(aIdx);
	}
	
	private class LoadPathRunnable extends ModelChangeCallback {
		private String mPath;
		public LoadPathRunnable(String path) {
			mPath = path;
		}

		@Override
		public void onModelChanged(ModelState stat) {
			mIDataStack.peek().goingToLeaveModel(stat);
			mIDataStack.push(new FolderData(FolderPicturesModel.this, mPath));
			tellFatherTestTopOnUi();
		}

	}
	
	private class SortRunnable  extends ModelChangeCallback {
		private int mSortFlag;
		public SortRunnable(int sortflag) {
			mSortFlag = sortflag;
		}

		@Override
		public void onModelChanged(ModelState stat) {
			mIDataStack.peek().sort(mSortFlag);
		}

	}
	
	private class PopStack extends ModelChangeCallback {

		@Override
		public void onModelChanged(ModelState stat) {
			if (!mIDataStack.empty()) {
				IData poped = mIDataStack.pop();
				poped.toString();
				mIDataStack.peek().backToModel(this);
				tellFatherTestTopOnUi();
			}

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

	public void loadPathContent(String path) {
		LoadPathRunnable run = new LoadPathRunnable(path);
		
		mRender.modelChanged(run);
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

	public Context getContext() {
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
	
	
	private class LoadBoardsRunnable extends ModelChangeCallback {
		@Override
		public void onModelChanged(ModelState stat) {
			mIDataStack.peek().goingToLeaveModel(stat);
			mIDataStack.push(new BoardsModel(FolderPicturesModel.this, mRender));
			tellFatherTestTopOnUi();
		}
	}

	public void clickAtBoards() {
		LoadBoardsRunnable run = new LoadBoardsRunnable();
		mRender.modelChanged(run);
	}

	private class LoadPinRunnable extends ModelChangeCallback {
		private HBoard mBoard;

		public LoadPinRunnable(HBoard board) {
			mBoard = board;
		}

		@Override
		public void onModelChanged(ModelState stat) {
			mIDataStack.peek().goingToLeaveModel(stat);
			mIDataStack.push(new PinsModel(FolderPicturesModel.this, mRender, mBoard));
			tellFatherTestTopOnUi();
		}
	}
	
	public void clickAtGivenBoard(HBoard board) {
		LoadPinRunnable run = new LoadPinRunnable(board);
		mRender.modelChanged(run);		
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
		if (isFolder) {
			menu.addPopMenuItem(new PathContainerView.MenuItem(getContext(), mContext.getString(R.string.menu_add_fav), 1));
			menu.setPopMenuClickListener(new FolderMenuListener(absPath, fName, isFolder));
		} else {
			menu.addPopMenuItem(new PathContainerView.MenuItem(getContext(), mContext.getString(R.string.menu_share), 2));
		}
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
			} else if (aPopMenuItemId == 2) {
				Intent shareIntent = new Intent(Intent.ACTION_SEND); 
				File file = new File(mAbsPath); 
				shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); 
				shareIntent.setType("image/*"); 
				mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.menu_share))); 
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

	@Override
	public void lastFrame(float offset_progress) {
		
	}

	@Override
	public void drawAtOffset(float mCurrOffset, float calced_max_offset,
			float calced_min_offset) {
		mPathClickListener.onOffsetDrawed(mCurrOffset, calced_max_offset, calced_min_offset);
	}

}
