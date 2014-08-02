package com.example.glhorizontalview;

import android.app.Activity;
import android.os.Bundle;

public class PathViewerActivity extends Activity {

	private PathContainerView mContentView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(mContentView = new PathContainerView(this));
	}

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

}
