package ssc.widget.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;

import com.ds.bitmaputils.BitmapNetGetter;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.MyRenderer;

public class BoardsModel implements GLResourceModel {

	private Context mContext;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	
	public HBoard[] mBoardsRef;
	
	public BoardsModel(Context context) {
		mContext = context;
		mRect = new Rect();

		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mMyRenderer.modelChanged(new ModelChangeCallback() {
					@Override
					public void onModelChanged(ModelState stat) {
						getBoards();
					}
				});
			}
		}, 500);
	}
	
	private void getBoards() {
		UserDataManager.init(mContext);
		mBoardsRef = UserDataManager.getInstance().getBoards();
	}


	@Override
	public int getCount() {
		if (mBoardsRef == null) {
			return 0;
		} else {
			return mBoardsRef.length;
		}
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		if (aIdx > mBoardsRef.length) return false;
		
		mRect.set(0, 0, require_width, require_height);
		mC.drawColor(0xff880000);

		if (mBoardsRef[aIdx]._cover_image == null) {
			
		} else {
			Bitmap bitmap =  BitmapNetGetter.tryGetBitmapFromUrlOrCallback(mBoardsRef[aIdx]._cover_image, null);
			if (bitmap != null) {
				mRect.set(0, 0, require_width, require_height);
				mC.drawBitmap(bitmap, null, mRect, null);
			} else {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void clickAt(int hit) {
		// ZHUJJ implemnt
	}

	@Override
	public void currRenderView(MyRenderer render) {
		mMyRenderer = render;
	}

	@Override
	public void longClick(float x, float y, int hit) {
		// ZHUJJ implemnt
	}

	@Override
	public void lastFrame(float offset_progress) {

	}

}
