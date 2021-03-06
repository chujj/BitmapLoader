package com.ds.bitmaputils;

import java.util.ArrayList;

import com.ds.bitmaputils.BitmapHelper.LEVEL;

import android.graphics.Bitmap;

public class AtomBitmap implements BitmapTask , Comparable<AtomBitmap> {
	private int max_width, max_height;
	private int width, height;
	protected long size;
	private Bitmap mBitmap;
	private boolean isRecycled;
	protected long lastAccessTime;
	protected long accessCount;
	private String mFilePath;
	private boolean isDecodeError;
	private ArrayList<BitmapGotCallBack> mRegistedCallback;
	private LEVEL mLevel;
	private Object unique_key;

	protected AtomBitmap() {
		isRecycled = false;
		isDecodeError = false;
		mRegistedCallback = new ArrayList<BitmapGotCallBack>();
		BitmapHelper.sInstance.registeAtomBitmap(this);
		size = -1;
	}
	
	public AtomBitmap(BitmapHelper aHalper, LEVEL level, String abspath2File, int m_width, int m_height) {
		this();
		mFilePath = abspath2File;
		max_width = m_width;
		max_height = m_height;
		mLevel = level;
		
		unique_key = UniqueKey(mFilePath + level.name());
	}
	
	public long getSize() {
		return size;
	}
	
	/** mark time , increase timestamp
	 * @return
	 */
	public Bitmap getBitmap() {
		return getBitmap(null);
	}
	
	public Bitmap getBitmap(BitmapGotCallBack callback) {
		if (mBitmap == null || isRecycled) {
			mBitmap = BitmapNetGetter.tryGetBitmapFromUrlOrCallback(this, mInsideCallback);
			if (mBitmap == null && callback != null && !mRegistedCallback.contains(callback)) {
				mRegistedCallback.add(callback);
			}
		}
		markRead();
		return mBitmap;

	}

	public void tryCancelQueryTask() {
		BitmapNetGetter.tryCancelTask(this);
	}

	final private void markRead() {
		lastAccessTime = System.currentTimeMillis();
		accessCount ++ ;
	}
	
	private BitmapGotCallBack mInsideCallback = new BitmapGotCallBack() {
		
		@Override
		public void onBitmapGot(Bitmap aBitmap) {
			if (aBitmap == null) return;
			
			mBitmap = aBitmap;
			width = mBitmap.getWidth();
			height = mBitmap.getHeight();
			size = width * height;
			isRecycled = false;
			for (int i = 0; i < mRegistedCallback.size(); i++) {
				mRegistedCallback.get(i).onBitmapGot(mBitmap);
			}
			mRegistedCallback.clear();
		}
	};

	public boolean isRecycled() {
		return isRecycled;
	}
	
	private static Object UniqueKey(String str) { // ZHUJJ-TODO: use same key, if size are same
		return Integer.valueOf(str.hashCode());
	}

	//////////////////////////////// Task implemention ////////////////////////////////
	@Override
	public Object getTaskKey() {
		return unique_key;
	}

	@Override
	public String getNetUrl() {
//		int a = 1 / 0;
		return null;
	}

	@Override
	public String getFileSystemPath() {
		return mFilePath;
	}

	@Override
	public void saveNetUrl(String aUrl) {
		int a = 1 / 0;
	}

	@Override
	public void saveFileSystemPath(String aPath) {
		int a = 1 / 0;
	}

	@Override
	public int getBitmapMaxWidth() {
		return max_width;
	}

	@Override
	public int getBitmapMaxHeight() {
		return max_height;
	}

	@Override
	public int compareTo(AtomBitmap arg0) {
		int retval 
		= 
//		0;
				(int) (arg0.lastAccessTime - lastAccessTime );
		if (retval == 0) {
			retval = (int) (arg0.accessCount - accessCount );
		}
		if (retval == 0) {
			retval = (int) (size - arg0.size);
		}
		return retval;
	}

	public String dump() {
		return 
				"<td>" + mLevel.name() + "</td>\n"+ 
				"<td>" + isRecycled + "</td>\n" +
				"<td>" + Long.toString(size) + "</td>\n" + 
				"<td>" +Long.toString(lastAccessTime) + "</td>\n" + 
				"<td>" +Long.toString(accessCount) + "</td>\n"+  
				"<td>" +mFilePath + "</td>\n";
	}

	public void recycle() {
		isRecycled = true;
		BitmapNetGetter.releaseBitmap(this);
		mBitmap = null;
		lastAccessTime = 0;
		accessCount = 0;
	}

	@Override
	public boolean useLocalStreamCache() {
		// should not support here
		return false;
	}

	@Override
	public Bitmap decodeFromLocalStream() {
		int a = 1 / 0;
		return null;
	}

	@Override
	public boolean saveBitmapByTaskself() {
		return false;// should not support here
	}

	@Override
	public void saveBitmap(Bitmap bitmap) {
		int a = 1 / 0;
	}

	@Override
	public Bitmap getBitmapFailed() {
		return BitmapHelper.sInstance.mLoadFailed;
	}

}
