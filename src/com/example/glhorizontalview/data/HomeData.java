package com.example.glhorizontalview.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.widget.Toast;

import ssc.software.picviewer.R;

import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.controll.PathContainerView;

import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenu.DsPopMenuClickListener;

public class HomeData  implements IData {
	private final static String serilized_file_name = "home_ticket";
	private final static float TEXT_AREA_HEIGHT_PERCENT_OF_FOLDERBITMAP = 0.125f; // crop height from folder.png
	private final static String BOARD_MARK = "boards";
	
	private static final boolean debug_background = false;
	
	public final static int TYPE_PATH_DEFAULT = 0;
	public final static int TYPE_PATH_FAV = 1;
	public final static int TYPE_URI = 2;
	public final static String type_sperater = ":";

	private FolderPicturesModel mFather;
	private ArrayList<HomeItem> mItems;

	private static Bitmap sFolderBitmap;
	private Rect mRect;
	private static Paint sTextPaint;
	protected static Paint sBgPaint;
	
	
	public static HomeData build(FolderPicturesModel father) {
		return new HomeData(father);
	}
	
	private HomeData(FolderPicturesModel father) {
		mFather = father;
		mItems = new ArrayList<HomeData.HomeItem>();
		
		mRect = new Rect();

		if (sFolderBitmap == null) {
			sFolderBitmap = BitmapFactory.decodeResource(father.getContext().getResources(), R.drawable.folder);
		}

		if (sTextPaint == null) {
			sTextPaint = new Paint();
			sTextPaint.setAntiAlias(true);
			sTextPaint.setColor(0xffc3c3c3);
			sTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
		}

		if (sBgPaint == null) {
			sBgPaint = new Paint();
			if (debug_background) {
				sBgPaint.setColor(0xffc3c3c3);
			} else {
				sBgPaint.setColor(0xff000000);
			}
		}
		
		loadFromFile();
	}
	
	private void loadFromFile() {
		File parent = mFather.getContext().getFilesDir();
		String[] exist_file = parent.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.equals(serilized_file_name);
			}
		});
		
		if (exist_file != null && exist_file.length == 1) { // load from it
			File serilizedFile = new File(mFather.getContext().getFilesDir(), serilized_file_name);
			try {
				BufferedReader in = new BufferedReader(new FileReader(serilizedFile));
				String line = in.readLine();
				while (line != null) {
					String[] defines = line.split(type_sperater);
					HomeItem it = new HomeItem();
					it.mType = Integer.parseInt(defines[0]);
					it.mDefine = defines[1].trim();
					if (defines.length > 2 && !defines[2].trim().equals("null")) 
						it.mShortName = defines[2].trim();
					mItems.add(it);
					line = in.readLine();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (mItems.size() == 0) {
				buildDefault();
			}
			
		} else { // use default
			buildDefault();
		}
	}
	
	private void buildDefault() {
		File rootsd = Environment.getExternalStorageDirectory();
		HomeItem it = null;
		
		{// default, boards model
			it = new HomeItem();
			it.mShortName = BOARD_MARK;
			it.mType = TYPE_URI;
			it.mDefine = BOARD_MARK;
			mItems.add(it);
		}

		if (rootsd.exists()) {
			// DCIM
			File dcim_dir = new File(rootsd.getAbsolutePath() , Environment.DIRECTORY_DCIM);
			if (dcim_dir.exists()) { 
				it = new HomeItem();
				it.mType = TYPE_PATH_DEFAULT;
				it.mDefine = dcim_dir.getAbsolutePath();
				it.mShortName = dcim_dir.getName();
				mItems.add(it);

				// Camera
				File camera_dir = new File(dcim_dir.getAbsolutePath(), "Camera");
				if (camera_dir.exists()) {
					it = new HomeItem();
					it.mType = TYPE_PATH_DEFAULT;
					it.mDefine = camera_dir.getAbsolutePath();
					it.mShortName = camera_dir.getName();
					mItems.add(it);
				}
			} // end DCIM
			
			// Picture
			File picture_dir = new File(rootsd.getAbsoluteFile(), Environment.DIRECTORY_PICTURES);
			if (picture_dir.exists()) {
				it = new HomeItem();
				it.mType = TYPE_PATH_DEFAULT;
				it.mDefine = picture_dir.getAbsolutePath();
				it.mShortName = picture_dir.getName();
				mItems.add(it);
			}
			
			// Sdcard
			it = new HomeItem();
			it.mType = TYPE_PATH_DEFAULT;
			it.mDefine = rootsd.getAbsolutePath();
			it.mShortName = rootsd.getName();
			mItems.add(it);
		}
		
		// Root
		it = new HomeItem();
		it.mType = TYPE_PATH_DEFAULT;
		it.mDefine = "/";
		it.mShortName = mFather.getContext().getString(R.string.root_dir);
		
		mItems.add(it);
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
	
	@Override
	public boolean supportSort(int sortby) {
		return false; // ZHUJJ-TODO make this flag work
	}

	@Override
	public void sort(int flag) {
	}

	@Override
	public int getCount() {
		return mItems.size();
	}
	
	protected static void drawFolderToCanvas(Canvas mC, int require_width,
			int require_height, String descript, Rect mBgRect) {
		mC.drawRect(mBgRect, sBgPaint);

		mC.drawBitmap(sFolderBitmap, null, mBgRect, null);

		float text_size = TEXT_AREA_HEIGHT_PERCENT_OF_FOLDERBITMAP * require_height / 2;
		int max_count = (int) ((require_width  / text_size)  - 1);
		if (descript.length() > max_count) {
			descript = "..." + descript.substring(descript.length() - max_count + 3);
		} else {

		}
		sTextPaint.setTextSize(text_size);
		mC.drawText(descript, (require_width - sTextPaint.measureText(descript) ) / 2, require_height - text_size, sTextPaint);
	}
	
	@Override
	public void deprecateToDraw(int aIdx) {

	}
		
	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		boolean validate = false;
		HomeItem it = mItems.get(aIdx);

		mRect.set(0, 0, require_width, require_height);
		if (it.mType == TYPE_PATH_DEFAULT || it.mType == TYPE_PATH_FAV) {
			String descript = it.mShortName == null ? it.mDefine : it.mShortName;
			drawFolderToCanvas(mC, require_width, require_height, descript, mRect);
		} else {
			mC.drawColor(0xff00aaaa);
		}

		validate = true;

		return validate;
	}

	@Override
	public void clickAt(int hit) {
		HomeItem it = mItems.get(hit);
		if (it.mType == TYPE_PATH_DEFAULT || it.mType == TYPE_PATH_FAV) {
			mFather.clickAtPathInside(this, hit, it.mDefine);
		} else if (it.mType == TYPE_URI){
			if (it.mDefine.startsWith(BOARD_MARK)) {
				// start boards model
				mFather.clickAtBoards();
			}
		} else {
			; // ZHUJJ-TODO, add more actions
		}
	}

	@Override
	public void longClick(float x, float y, int hit) {
		if (mItems.get(hit).mType != TYPE_PATH_DEFAULT) {
			DsPopMenu menu = new DsPopMenu(mFather.getContext());
			menu.addPopMenuItem(new PathContainerView.MenuItem(mFather.getContext(), mFather.getContext().getString(R.string.menu_del_fav), 1));
			menu.setPopMenuClickListener(new DelMenuListener(hit));

			mFather.showMenuForHome(menu, x, y);
		}
	}
	
	
	private class DelMenuListener implements DsPopMenuClickListener {
		private int mHitIdx;

		public DelMenuListener(int hit) {
			mHitIdx = hit;
		}

		@Override
		public void onPopMenuItemClick(int aPopMenuId, int aPopMenuItemId) {
			if (aPopMenuItemId == 1) {
				mItems.remove(mHitIdx);
			}
			
			mFather.homedataReloaded();
			mFather.mPathClickListener.dismissMenu();
		}
		
	}
	
	
	public void serilizedToFile() {
		File serilizedFile = new File(mFather.getContext().getFilesDir(), serilized_file_name);
		try {
			FileOutputStream fo = new FileOutputStream(serilizedFile, false);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mItems.size(); i++) {
				sb.setLength(0);
				HomeItem it = mItems.get(i);
				sb.append(it.mType);
				sb.append(type_sperater);
				sb.append(it.mDefine);
				sb.append(type_sperater);
				sb.append(it.mShortName);
				sb.append("\n");
				byte[] content = sb.toString().getBytes();
				fo.write(content, 0, content.length);
			}
			fo.flush();
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class HomeItem {
		private int mType;
		private String mDefine;
		private String mShortName;
	}

	public void tryAddFav(String mAbsPath, String mFName, boolean mIsFolder) {
		boolean exist = false;
		for (int i = 0; i < mItems.size(); i++) {
			if (mItems.get(i).mDefine.endsWith(mAbsPath)) {
				exist = true;
				break;
			}
		}
		
		if (exist) { // show toast
			Toast.makeText(mFather.getContext(), mFName + " already exist in Fav", Toast.LENGTH_LONG).show();
		} else {
			// add to fav
			HomeItem newFav = new HomeItem();
			newFav.mType = TYPE_PATH_FAV;
			newFav.mDefine = mAbsPath;
			newFav.mShortName = mFName;
			mItems.add(newFav);
			// reload model, or mark reload delay
			mFather.homedataReloaded();
			
		}
		
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
