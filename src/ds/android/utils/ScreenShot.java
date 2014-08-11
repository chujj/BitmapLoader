package ds.android.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

public class ScreenShot {
	// 获取指定Activity的截屏，保存到png文件 
	private static Bitmap takeActivityShot(Activity activity) { 		
		//View是你需要截图的View 
		View view = activity.getWindow().getDecorView(); 
		view.setDrawingCacheEnabled(true); 
		view.buildDrawingCache(); 
		Bitmap b1 = view.getDrawingCache(); 


		//获取状态栏高度 
		Rect frame = new Rect(); 
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame); 
		int statusBarHeight = frame.top; 
		System.out.println(statusBarHeight); 

		//获取屏幕长和高 
		int width = activity.getWindowManager().getDefaultDisplay().getWidth(); 
		int height = activity.getWindowManager().getDefaultDisplay().getHeight(); 


		//去掉标题栏 
		//Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455); 
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight); 
		view.destroyDrawingCache(); 
		return b; 
	} 
	
	private static Bitmap takeViewShot(View v) { // 将一个View转化成一张图片
		v.clearFocus(); // 清除视图焦点  
	    v.setPressed(false);// 将视图设为不可点击  
	  
	    boolean willNotCache = v.willNotCacheDrawing(); // 返回视图是否可以保存他的画图缓存  
	    v.setWillNotCacheDrawing(false);  
	  
	    // Reset the drawing cache background color to fully transparent  
	    // for the duration of this operation //将视图在此操作时置为透明  
	    int color = v.getDrawingCacheBackgroundColor(); // 获得绘制缓存位图的背景颜色  
	    v.setDrawingCacheBackgroundColor(0); // 设置绘图背景颜色  
	    if (color != 0) { // 如果获得的背景不是黑色的则释放以前的绘图缓存  
	        v.destroyDrawingCache(); // 释放绘图资源所使用的缓存  
	    }  
	    v.buildDrawingCache(); // 重新创建绘图缓存，此时的背景色是黑色  
	    Bitmap cacheBitmap = v.getDrawingCache(); // 将绘图缓存得到的,注意这里得到的只是一个图像的引用  
	    if (cacheBitmap == null) {  
	        return null;  
	    }  
	    Bitmap bitmap = Bitmap.createBitmap(cacheBitmap); // 将位图实例化  
	    // Restore the view //恢复视图  
	    v.destroyDrawingCache();// 释放位图内存  
	    v.setWillNotCacheDrawing(willNotCache);// 返回以前缓存设置  
	    v.setDrawingCacheBackgroundColor(color);// 返回以前的缓存颜色设置  
	    return bitmap;  
	}

	//保存到sdcard 
	private static void savePic(Bitmap b,String strFileName){ 
		FileOutputStream fos = null; 
		try { 
			fos = new FileOutputStream(strFileName); 
			if (null != fos) { 
				b.compress(Bitmap.CompressFormat.PNG, 90, fos); 
				fos.flush(); 
				fos.close(); 
			} 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	} 


	//程序入口 
	public static void shootActivity(Activity a, String shotFile){ 
		if (!StringUtils.isNull(shotFile)) {
			ScreenShot.savePic(ScreenShot.takeActivityShot(a), shotFile);
		}
	} 
	public static void shootView(View v, String shotFile){ 
		if (!StringUtils.isNull(shotFile)) {
			ScreenShot.savePic(ScreenShot.takeViewShot(v), shotFile);
		}
	} 
	
}
