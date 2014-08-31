package com.ds.bitmaputils;

import android.graphics.Bitmap;

public interface BitmapTask {
	/**
	 * The uniq key, which should always same the different sessions. An
	 * immutable string recommended, or you should serilize the memory object
	 * 
	 * @return
	 */
	public Object getTaskKey();

	/**
	 * get URI as string
	 * 
	 * @return
	 */
	public String getNetUrl();

	/** if true, will read from task's stream, instead of local file.
	 * @return
	 */
	public boolean useLocalStreamCache();
	/** decodeAsTask's stream
	 * @return
	 */
	public Bitmap decodeFromLocalStream();
	/**
	 * the abs path, tell by last call of @saveFileSystemPath, or null
	 * 
	 * @return
	 */
	public String getFileSystemPath();

	/**
	 * maybe 403, but now is meanless
	 * 
	 * @param aUrl
	 */
	public void saveNetUrl(String aUrl);

	/** If let task self handle cache?
	 * @return
	 */
	public boolean saveBitmapByTaskself();
	/** called if task decide handle save cache self
	 * @param bitmap
	 */
	public void saveBitmap(Bitmap bitmap);
	/**
	 * call then cached in local fs, the fs path given
	 * 
	 * @param aPath
	 */
	public void saveFileSystemPath(String aPath);
	
	public int getBitmapMaxWidth();
	public int getBitmapMaxHeight();

	public Bitmap getBitmapFailed();

}
