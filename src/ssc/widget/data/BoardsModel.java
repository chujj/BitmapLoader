package ssc.widget.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.ds.bitmaputils.BitmapNetGetter;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.data.IData;

public class BoardsModel implements GLResourceModel, IData {

	private Context mContext;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	
	public HBoard[] mBoardsRef;
	
	public BoardsModel(Context context, MyRenderer render) {
		mContext = context;
		mRect = new Rect();
		mMyRenderer = render;

		Runnable action = new Runnable() {

			@Override
			public void run() {
				mMyRenderer.modelChanged(new ModelChangeCallback() {
					@Override
					public void onModelChanged(ModelState stat) {
						getBoards();
					}
				});
			}
		};
		
		if (Looper.getMainLooper() == Looper.myLooper()) {
			new Handler().postDelayed(action, 500);
		} else {
			action.run();
		}
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

	//////////////////////////////// IData Interface ////////////////////////////////
	@Override
	public boolean supportSort(int sortby) {
		// ZHUJJ Auto-generated method stub
		return false;
	}

	@Override
	public void sort(int flag) {
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public void backToModel(ModelChangeCallback popStack) {
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public void goingToLeaveModel(ModelState stat) {
		// ZHUJJ Auto-generated method stub
		
	}

}
