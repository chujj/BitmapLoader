package com.example.glhorizontalview.controll;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import android.content.Context;
import android.view.ViewGroup;

import com.ds.bitmaputils.AtomBitmap;

public class MyPagerAdapter extends BasePagerAdapter {

	private AtomBitmap mAbp;
	public MyPagerAdapter(Context context, AtomBitmap abp) {
		super(context, null);
		mAbp = abp;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		((GalleryViewPager) container).mCurrentView = ((MixedTouchImageView) object)
				.getImageView();
	}

	@Override
	public Object instantiateItem(ViewGroup collection, int position) {
		if (position != 0) throw new RuntimeException("should only have 1 child, not write with position: " + position);
		
		final MixedTouchImageView iv = new MixedTouchImageView(mContext);
		iv.setAbp(mAbp);
		iv.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		collection.addView(iv, 0);
		return iv;
	}

	@Override
	public int getCount() {
		return 1;
	}

}
