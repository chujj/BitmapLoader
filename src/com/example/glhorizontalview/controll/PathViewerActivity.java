package com.example.glhorizontalview.controll;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.ds.bitmaputils.BitmapHelper;

public class PathViewerActivity extends Activity {

	private PathContainerView mContentView;
//	private MyServer mServer;
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
	
//	private Responce res = new Responce() {
//
//		@Override
//		public String getString() {
//			return BitmapHelper.getInstance(PathViewerActivity.this).dumpAllAtomBitmaps();
//		}
//	};

	@Override
	protected void onResume() {
		super.onResume();
		mContentView.onResume();
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (mContentView.dispatchKeyEvent(event)) {
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onLowMemory() {
		BitmapHelper.getInstance(this).recycleBitmaps();
		super.onLowMemory();
	}

}
