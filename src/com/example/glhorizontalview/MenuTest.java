package com.example.glhorizontalview;

import ds.android.ui.core.DsPopMenu;
import ds.android.ui.core.DsPopMenuItem;
import ds.android.ui.core.DsPopMenuLayout;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

public class MenuTest extends Activity {

	private DsPopMenuLayout mMenuLayout;
	private DsPopMenu mMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mMenuLayout = new DsPopMenuLayout(this);
		mMenuLayout.setBackgroundColor(0xffaa0000);
		this.setContentView(mMenuLayout);
		
		mMenu = new DsPopMenu(this);
		mMenu.setMaxColumn(1);
		
		mMenu.addPopMenuItem(new DsPopMenuItem(this));
		mMenu.addPopMenuItem(new DsPopMenuItem(this));
		mMenu.addPopMenuItem(new DsPopMenuItem(this));
		mMenuLayout.showPopMenu(mMenu);
		
		mMenuLayout.postDelayed(delay, 1500);
	}

	
	private Runnable delay = new Runnable() {
		
		@Override
		public void run() {
			if (mMenuLayout.isPopMenuShow()) {
				mMenuLayout.dismissPopMenu();
			} else {
				mMenuLayout.showPopMenu(mMenu);
			}

			mMenuLayout.postDelayed(delay, 1500);
		}
	};


}
