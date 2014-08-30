package com.example.glhorizontalview.data;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapGotCallBack;
import com.ds.bitmaputils.BitmapHelper;
import com.ds.ui.DsCanvasUtil;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.controll.MyPagerAdapter;

public class FolderData implements IData {
	private String mPath;
	public String getmPath() {
		return mPath;
	}

	private Item[] mKeys;
	private Rect mRect;
	private FolderPicturesModel mFather;
	
	public FolderData(FolderPicturesModel father, String abspath) {
		mRect = new Rect();
		mFather = father;

		mPath = abspath;
		
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

		if (files == null) {
			files = new File[0];
		}

		mKeys = new Item[files.length];

		for (int i = 0; i < files.length; i++) {
			mKeys[i] = new Item(i, files[i]);
		}
	}
	
	private class Item implements BitmapGotCallBack {
		boolean isFolder;
		String absPath;
		String fName;
		long lastModify;
		long size;
		int mIdx;

		public Item(int idx, File file) {
			mIdx = idx;
			isFolder = file.isDirectory();
			fName = file.getName();
			absPath = file.getAbsolutePath();
			lastModify = file.lastModified();
			size = file.length();
		}

		@Override
		public void onBitmapGot(Bitmap aBitmap) {
			mFather.onBitmapGot(FolderData.this, mIdx);
		}
	}

	@Override
	public boolean supportSort(int sortby) {
		return true;
	}

	@Override
	public void sort(final int flag) {
		Arrays.sort(mKeys, new Comparator<Item>() {

			@Override
			public int compare(Item lhs, Item rhs) {
				boolean revert = ( (flag & IData.SORT_REVERSE )== IData.SORT_REVERSE );
				int result = 0;
				if (flag == IData.SORT_NAME) {
					result = lhs.fName.compareTo(rhs.fName);
				} else if (flag == IData.SORT_LASTMODIFY) {
					result = (int) (lhs.lastModify - rhs.lastModify);
				} else if (flag == IData.SORT_SIZE) {
					result = (int) (lhs.size - rhs.size);
				}
				
				if (revert)
					result = - result;
				
				return result;
			}
		});
		
		for (int i = 0; i < mKeys.length; i++) {
			mKeys[i].mIdx = i;
		}
		
	}

	@Override
	public int getCount() {
		return mKeys.length;
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		boolean validate = false;

		mRect.set(0, 0, require_width, require_height);
		mC.drawRect(mRect, HomeData.sBgPaint);

		if (mKeys[aIdx].isFolder) {
			HomeData.drawFolderToCanvas(mC, require_width, require_height, mKeys[aIdx].fName, mRect);
			validate = true;
		} else {
			AtomBitmap abp = BitmapHelper.getInstance(mFather.getContext()).getBitmap(
					mKeys[aIdx].absPath);
			Bitmap bp = abp.getBitmap(mKeys[aIdx]);
			if (bp != null) {
				DsCanvasUtil.drawToCenterOfCanvas(mC, bp, require_width, require_height, mRect);
				validate = true;
			} else {
				validate = false;
			}
			
		}

		return validate;
	}

	@Override
	public void clickAt(int hit) {
		if (mKeys[hit].isFolder) {
			mFather.clickAtPathInside(FolderData.this, hit, mKeys[hit].absPath);
		} else {
			mFather.mPathClickListener.showGallery(new MyPagerAdapter(mFather
					.getContext(), BitmapHelper.getInstance(
					mFather.getContext()).getCbitmap(mKeys[hit].absPath)));
		}
	}
	
	@Override
	public void longClick(float x, float y, int hit) {
		Item it = mKeys[hit];
		
		mFather.showMenuForFolder(this, it.absPath, it.fName, it.isFolder, x, y);
	}

	private ModelState mLeaveStat;
	@Override
	public void goingToLeaveModel(ModelState stat) {
		mLeaveStat = stat;
	}

	@Override
	public void backToModel(ModelChangeCallback popStack) {
		if (mLeaveStat != null) {
			popStack.setState(mLeaveStat);
			mLeaveStat = null;
		}
	}

}
