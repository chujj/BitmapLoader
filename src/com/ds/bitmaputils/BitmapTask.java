package com.ds.bitmaputils;

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

	/**
	 * call then cached in local fs, the fs path given
	 * 
	 * @param aPath
	 */
	public void saveFileSystemPath(String aPath);
	
	public int getBitmapMaxWidth();
	public int getBitmapMaxHeight();
}
