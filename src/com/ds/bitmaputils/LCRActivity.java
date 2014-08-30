package com.ds.bitmaputils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ds.io.DsLog;
import com.ds.theard.WorkThread;
import com.jakewharton.disklrucache.DiskLruCache;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class LCRActivity extends Activity {
	public final static String key = "c55c4fb556183aaaf8026bae75b0ccf7adff7e2012b00-qx8auT";
	public final static String cache_key = key.toLowerCase();
	
	public final static String remote_query_url = "http://img.hb.aicdn.com/";

	private DiskLruCache mCache;
	
	
	
	
//If your faces are not drawn, please check try 
//	disabling back face culling. Your faces may be missing because they are 
//	facing away from the camera. DefaultShader.defaultCullFace = 0;
//Also, it is quite common that the materials from Blender export with 
//	opacity set to Zero. If you notice your model is not being rendered. 
//	Go to the Material in Blender, and below "Transparency" 
//	set its Alpha to the desired one (usually 1, for full opacity).
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WorkThread.init();
		BitmapNetGetter.setCacheFileDir(this.getFilesDir().getAbsolutePath());
		
		DiskLruCache cache = null;
		try {
			cache = DiskLruCache.open(this.getCacheDir(), 1, 1, 1024 * 1024 * 20);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (cache  == null) {
			throw new RuntimeException("cache init error");
		}
		
		mCache  = cache;

		BitmapNetGetter.tryGetBitmapFromUrlOrCallback(netTask, null);
	}
	
	BitmapTask netTask = new BitmapTask() {
		
		@Override
		public void saveNetUrl(String aUrl) {
			int a =  1 / 0;
		}
		
		@Override
		public void saveFileSystemPath(String aPath) {

		}
		
		@Override
		public Object getTaskKey() {
			return key;
		}
		
		@Override
		public String getNetUrl() {
			return remote_query_url + key;
		}
		
		@Override
		public boolean useLocalStreamCache() {
			return true;
		}

		@Override
		public Bitmap decodeFromLocalStream() {
			Bitmap retval = null;
			try {
				Snapshot snapshot = mCache.get(cache_key);
				if (snapshot == null || snapshot.getLength(0) == 0) {
					
				} else {
					InputStream is = snapshot.getInputStream(0);
					retval = BitmapFactory.decodeStream(is);
					snapshot.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (retval != null) {
				DsLog.e("decode success:" + key);
			} else {
				DsLog.e("decode fail:" + key);
			}
			return retval;
		}

		@Override
		public boolean saveBitmapByTaskself() {
			return true;
		}

		@Override
		public void saveBitmap(Bitmap bitmap) {
			try {
				Editor editor = mCache.edit(cache_key);
				OutputStream os = editor.newOutputStream(0);
				bitmap.compress(CompressFormat.JPEG, 100, os);
				os.flush();
				os.close();
				editor.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		@Override
		public String getFileSystemPath() {
			
			return null;
		}
		
		@Override
		public int getBitmapMaxWidth() {
			
			return 0;
		}
		
		@Override
		public int getBitmapMaxHeight() {
			
			return 0;
		}

	};

}
