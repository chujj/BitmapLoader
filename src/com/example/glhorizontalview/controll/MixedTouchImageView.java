package com.example.glhorizontalview.controll;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ru.truba.touchgallery.TouchView.InputStreamWrapper;
import ru.truba.touchgallery.TouchView.InputStreamWrapper.InputStreamProgressListener;
import ru.truba.touchgallery.TouchView.TouchImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapGotCallBack;
import com.example.bitmaploader.R;

public class MixedTouchImageView extends RelativeLayout {
		protected ProgressBar mProgressBar;
		protected TouchImageView mImageView;

		protected Context mContext;
		
//		private int mGrounpOffset;

		public MixedTouchImageView(Context ctx)
		{
			super(ctx);
			mContext = ctx;
			init();

		}

		public TouchImageView getImageView() { return mImageView; }

		@SuppressWarnings("deprecation")
		protected void init() {
			mImageView = new TouchImageView(mContext);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			mImageView.setLayoutParams(params);
			this.addView(mImageView);
			mImageView.setVisibility(GONE);

//			mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
			mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge);
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.setMargins(30, 0, 30, 0);
			mProgressBar.setLayoutParams(params);
			mProgressBar.setIndeterminate(false);
			mProgressBar.setMax(100);
			this.addView(mProgressBar);
		}

		public void setUrl(String imageUrl)
		{
			new ImageLoadTask().execute(imageUrl);
		}

		public void setAbp(AtomBitmap abp) {
			Bitmap bm = abp.getBitmap(mBitmapGot);
			if (bm != null) {
				mBitmapGot.onBitmapGot(bm);
			}
		}
		
		public void setPosition(int position) {
			/* zhujj: Need no more
			mGrounpOffset = position;
			Bitmap bm = BitmapGetter.tryGetBitmapFromUrlOrCallback(
					new BitmapTask() {
						
						@Override
						public void saveNetUrl(String aUrl) {
							throw new RuntimeException("no implement");
						}
						
						@Override
						public void saveFileSystemPath(String aPath) {
							mDetailBean.mProperties.put(mGrounpOffset + "", aPath);
						}
						
						@Override
						public Object getTaskKey() {
							return mDetailBean.mPairUrlAndLocalFilePath[mGrounpOffset].mUrl;
						}
						
						@Override
						public String getNetUrl() {
							return mDetailBean.mPairUrlAndLocalFilePath[mGrounpOffset].mUrl;
						}
						
						@Override
						public String getFileSystemPath() {
							return mDetailBean.mProperties.getProperty(mGrounpOffset + "");
						}
					}, mBitmapGot);
			if (bm != null) {
				mBitmapGot.onBitmapGot(bm);
			} */
		}
		
		private BitmapGotCallBack mBitmapGot = new BitmapGotCallBack() {

			@Override
			public void onBitmapGot(Bitmap aBitmap) {
				if (aBitmap == null) 
				{
					mImageView.setScaleType(ScaleType.CENTER);
					aBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
					mImageView.setImageBitmap(aBitmap);
				}
				else 
				{
					mImageView.setScaleType(ScaleType.MATRIX);
					mImageView.setImageBitmap(aBitmap);
				}
				mImageView.setVisibility(VISIBLE);
				mProgressBar.setVisibility(GONE);
			}
		};
		
		//No caching load
		public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap>
		{
			@Override
			protected Bitmap doInBackground(String... strings) {
				String url = strings[0];
				Bitmap bm = null;
				try {
					URL aURL = new URL(url);
					URLConnection conn = aURL.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					int totalLen = conn.getContentLength();
					InputStreamWrapper bis = new InputStreamWrapper(is, 8192, totalLen);
					bis.setProgressListener(new InputStreamProgressListener()
					{					
						@Override
						public void onProgress(float progressValue, long bytesLoaded,
								long bytesTotal)
						{
							publishProgress((int)(progressValue * 100));
						}
					});
					bm = BitmapFactory.decodeStream(bis);
					bis.close();
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return bm;
			}
			
			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap == null) 
				{
					mImageView.setScaleType(ScaleType.CENTER);
					bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
					mImageView.setImageBitmap(bitmap);
				}
				else 
				{
					mImageView.setScaleType(ScaleType.MATRIX);
					mImageView.setImageBitmap(bitmap);
				}
				mImageView.setVisibility(VISIBLE);
				mProgressBar.setVisibility(GONE);
			}
			
			@Override
			protected void onProgressUpdate(Integer... values)
			{
				mProgressBar.setProgress(values[0]);
			}
		}
	}