package ssc.widget.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.ds.bitmaputils.BitmapNetGetter;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.data.FolderPicturesModel;
import com.example.glhorizontalview.data.IData;

public class PinsModel implements GLResourceModel, IData {

	private FolderPicturesModel mFather;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	private HBoard mBoard;
	

	public PinsModel(FolderPicturesModel folderPicturesModel,
			MyRenderer render, HBoard board) {
		mFather = folderPicturesModel;
		mRect = new Rect();
		mMyRenderer = render;
		mBoard = board;
	}

	@Override
	public int getCount() {
		return mBoard._pin_count;
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		if (aIdx > mBoard.mPins.length) return false;

		mRect.set(0, 0, require_width, require_height);
		mC.drawColor(0xff880000);

		if (mBoard.mPins[aIdx]._img == null) {
			
		} else {
			Bitmap bitmap =  BitmapNetGetter.tryGetBitmapFromUrlOrCallback(mBoard.mPins[aIdx]._img, null);
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
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public void currRenderView(MyRenderer render) {
		mMyRenderer = render;
	}

	@Override
	public void longClick(float x, float y, int hit) {
		// ZHUJJ Auto-generated method stub
		
	}

	@Override
	public void lastFrame(float offset_progress) {
		// ZHUJJ Auto-generated method stub
		
	}

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
