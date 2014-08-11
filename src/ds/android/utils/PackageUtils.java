package ds.android.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
	
	private static final String LOG_TAG="PackageUtils";
	
	public static int getVersionCode(Context context)
	{
		int verCode = -1;
		try{
			String name = getApplicationName(context);
			verCode = context.getPackageManager().getPackageInfo(name, 0).versionCode;
		}catch(NameNotFoundException e)
		{
			TLog.e(LOG_TAG, e.getMessage());
		}
		return verCode;
	}
	
	public static String getVersionName(Context context)
	{
		String verName ="";
		try{
			String name = getApplicationName(context);
			verName = context.getPackageManager().getPackageInfo(name, 0).versionName;
		}catch(NameNotFoundException e)
		{
			TLog.e(LOG_TAG, e.getMessage());
		}
		return verName;
	}
	
	public static String getApplicationName(Context context)
	{
		return context.getPackageName();
	}
}
