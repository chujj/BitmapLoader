package com.example.bitmaploader;

import java.io.File;
import java.io.FileFilter;

import com.ds.io.DsLog;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadBitmaps();
	}

	private void loadBitmaps() {
		String testDir = "/sdcard/wallpapers";
		File dir = new File(testDir);
		File[] files = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				boolean retval;
				retval = arg0.getName().endsWith("jpg") ? true : false;
				return retval;
			}
		});
		
		for (int i = 0; i < files.length; i++) {
			DsLog.e(files[i].getName());
		}
		int a = 1 / 0;
		
	}



}
