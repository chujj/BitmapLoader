package com.example.glhorizontalview.controll;

import java.io.IOException;

import com.ds.bitmaputils.BitmapHelper;
import com.example.httepsernvertest.MyServer;
import com.example.httepsernvertest.MyServer.Responce;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class PathViewerActivity extends Activity {

	private PathContainerView mContentView;
	private MyServer mServer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(mContentView = new PathContainerView(this));
//		try {
//			mServer = new MyServer(res);
//			mServer.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private Responce res = new Responce() {

		@Override
		public String getString() {
			return BitmapHelper.getInstance(PathViewerActivity.this).dumpAllAtomBitmaps();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		mContentView.onResume();
//		mContentView.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				BitmapHelper.getInstance(PathViewerActivity.this).recycleBitmaps();
//				Toast.makeText(PathViewerActivity.this, "recycle", Toast.LENGTH_LONG).show();
//				
//			}
//		}, 10000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mContentView.onPause();
	}
	

//	@Override
//	protected void onDestroy() {
//		if (mServer != null) {
//			mServer.stop();
//		}
//		super.onDestroy();
//	}

	@Override
	public void onLowMemory() {
		BitmapHelper.getInstance(this).recycleBitmaps();
		super.onLowMemory();
	}

}
