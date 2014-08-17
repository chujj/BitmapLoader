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

import com.example.bitmaploader.R;
import com.example.glhorizontalview.controll.PathContainerView;

import dalvik.system.PathClassLoader;
import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenu.DsPopMenuClickListener;

public class HomeData  implements IData {
	private final static String serilized_file_name = "home_ticket";
	private final float TEXT_AREA_HEIGHT_PERCENT_OF_FOLDERBITMAP = 0.125f; // crop height from folder.png
	
	private static final boolean debug_background = false;
	
	public final static int TYPE_PATH_DEFAULT = 0;
	public final static int TYPE_PATH_FAV = 1;
	public final static int TYPE_URI = 2;
	public final static String type_sperater = ":";

	private FolderPicturesModel mFather;
	private ArrayList<HomeItem> mItems;

	private Bitmap mFolderBitmap;
	private Rect mRect;
	private Paint mTextPaint, mBgPaint;
	
	
	public static HomeData build(FolderPicturesModel father) {
		return new HomeData(father);
	}
	
	private HomeData(FolderPicturesModel father) {
		mFather = father;
		mItems = new ArrayList<HomeData.HomeItem>();
		
		mRect = new Rect();

		mFolderBitmap = BitmapFactory.decodeResource(father.getContext().getResources(), R.drawable.folder);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(0xffc3c3c3);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

		mBgPaint = new Paint();
		if (debug_background) {
			mBgPaint.setColor(0xffc3c3c3);
		} else {
			mBgPaint.setColor(0xff000000);
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
		HomeItem it = new HomeItem();
		it.mType = TYPE_PATH_DEFAULT;
		it.mDefine = getInitPath();
		it.mShortName = new File(it.mDefine).getName();
		
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
		// ZHUJJ Auto-generated method stub
		return false;
	}

	@Override
	public void sort(int flag) {
		// ZHUJJ Auto-generated method stub
	}

	@Override
	public int getCount() {
		return mItems.size();
	}
	
	
	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		boolean validate = false;
		HomeItem it = mItems.get(aIdx);

		mRect.set(0, 0, require_width, require_height);
		if (it.mType == TYPE_PATH_DEFAULT || it.mType == TYPE_PATH_FAV) {
			mC.drawRect(mRect, mBgPaint);
			
			mC.drawBitmap(mFolderBitmap, null, mRect, null);
			
			String descript = it.mShortName == null ? it.mDefine : it.mShortName;
			float text_size = TEXT_AREA_HEIGHT_PERCENT_OF_FOLDERBITMAP * require_height / 2;
			int max_count = (int) ((require_width  / text_size)  - 1);
			if (descript.length() > max_count) {
				descript = "..." + descript.substring(descript.length() - max_count + 3);
			} else {
				
			}
			mTextPaint.setTextSize(text_size);
			mC.drawText(descript, (require_width - mTextPaint.measureText(descript) ) / 2, require_height - text_size, mTextPaint);
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
		} else {
			; // ZHUJJ-TODO, add more actions
		}
	}

	@Override
	public void longClick(int hit) {
		DsPopMenu menu = new DsPopMenu(mFather.getContext());
		menu.addPopMenuItem(new PathContainerView.MenuItem(mFather.getContext(), "del", 1));
		menu.setPopMenuClickListener(new DelMenuListener(hit));
			
		
		mFather.showMenuForHome(menu);
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


}
