package ssc.widget.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.ds.bitmaputils.BitmapGotCallBack;
import com.ds.bitmaputils.BitmapNetGetter;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.data.FolderPicturesModel;
import com.example.glhorizontalview.data.IData;

public class BoardsModel implements GLResourceModel, IData {

	private Context mContext;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	
	public HBoard[] mBoardsRef;
	private FolderPicturesModel mFather;
	
	public BoardsModel(FolderPicturesModel father, MyRenderer render) {
		mFather = father;
		mRect = new Rect();
		mMyRenderer = render;
		mContext = father.getContext();

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
			return false;
		} else {
			Bitmap bitmap =  BitmapNetGetter.tryGetBitmapFromUrlOrCallback(mBoardsRef[aIdx]._cover_image, null);
			if (bitmap != null) {
				mRect.set(0, 0, require_width, require_height);
				mC.drawBitmap(bitmap, null, mRect, null);
			} else {
				mMyRenderer.refreshIdx(aIdx); // ZHUJJ cause last call of BitmapGetTask doesn't have call back. So we force refresh here
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void clickAt(int hit) {
		mFather.clickAtGivenBoard(mBoardsRef[hit]);
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
		return false;
	}

	@Override
	public void sort(int flag) {
	}

	private ModelState mLeaveStat;
	@Override
	public void goingToLeaveModel(ModelState stat) {
		mLeaveStat = stat;
	}

	@Override
	public void backToModel(ModelChangeCallback popStack) {
		if (mLeaveStat != null) {
			popStack.setState(mLeaveStat);
			mLeaveStat = null;
		}
	}

}
