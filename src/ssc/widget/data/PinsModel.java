package ssc.widget.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapHelper;
import com.ds.bitmaputils.BitmapHelper.LEVEL;
import com.ds.bitmaputils.BitmapNetGetter;
import com.ds.ui.DsCanvasUtil;
import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;
import com.example.glhorizontalview.MyRenderer;
import com.example.glhorizontalview.controll.MyPagerAdapter;
import com.example.glhorizontalview.data.FolderPicturesModel;
import com.example.glhorizontalview.data.IData;

public class PinsModel implements GLResourceModel, IData {

	private FolderPicturesModel mFather;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	private HBoard mBoard;
	private Paint mTextPaint;

	public PinsModel(FolderPicturesModel folderPicturesModel,
			MyRenderer render, HBoard board) {
		mFather = folderPicturesModel;
		mRect = new Rect();
		mMyRenderer = render;
		mBoard = board;
		
		mTextPaint = new Paint();
		mTextPaint.setTextSize(15);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(0xffc3c3c3);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
	}

	@Override
	public int getCount() {
		return mBoard.mPins.length;
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		boolean retval = false;
		if (aIdx > mBoard.mPins.length) return retval;

		mRect.set(0, 0, require_width, require_height);
		mC.drawColor(0xff000000);

		if (mBoard.mPins[aIdx]._img == null) {
			retval = false;
		} else {
			AtomBitmap abitmap = BitmapHelper.getInstance(mFather.getContext()).
					getBitmap(mBoard.mPins[aIdx]._img.remote_query_url, LEVEL.ORIGIN, true, BoardsModel.sFactory, mBoard.mPins[aIdx]._img);
			Bitmap bitmap =  abitmap.getBitmap();
			
			if (bitmap != null) {
				DsCanvasUtil.drawToCenterOfCanvas(mC, bitmap, require_width, require_height, mRect);
				retval = true;
			} else {
				mMyRenderer.refreshIdx(aIdx); //cause last call of BitmapGetTask doesn't have call back. So we force refresh here
				retval = false;
			}
		}
		final String raw_text =  mBoard.mPins[aIdx]._raw_text;
		drawFolderToCanvas(mC, require_width, require_height, raw_text == null ? "" : raw_text, mRect, mTextPaint);
		
		return retval;
	}
	
	public static void drawFolderToCanvas(Canvas mC, int require_width,
			int require_height, String descript, Rect mBgRect, Paint textPaint) {

		float text_size = 15;
		int max_count = (int) ((require_width  / text_size)  - 1);
		if (descript.length() > max_count) {
			descript = descript.subSequence(0, max_count) + "...";
		} else {

		}
		
		mC.drawText(descript, (require_width - textPaint.measureText(descript) ) / 2, require_height - text_size, textPaint);
	}
	
	@Override
	public void clickAt(int hit) {
		if (mBoard.mPins[hit]._img != null) {
			mFather.mPathClickListener.showGallery(new MyPagerAdapter(mFather
					.getContext(), BitmapHelper.getInstance(
							mFather.getContext()).
							getCbitmap(mBoard.mPins[hit]._img.remote_query_url, true, BoardsModel.sFactory, mBoard.mPins[hit]._img)));
		}
	}

	@Override
	public void currRenderView(MyRenderer render) {
		mMyRenderer = render;
	}

	@Override
	public void longClick(float x, float y, int hit) {
		
		
	}

	@Override
	public void lastFrame(float offset_progress) {
		
		
	}

	@Override
	public boolean supportSort(int sortby) {
		
		return false;
	}

	@Override
	public void sort(int flag) {
		

	}

	@Override
	public void backToModel(ModelChangeCallback popStack) {
		

	}

	@Override
	public void goingToLeaveModel(ModelState stat) {
		

	}

	@Override
	public void drawAtOffset(float mCurrOffset, float calced_max_offset,
			float calced_min_offset) {
	}

	@Override
	public void deprecateToDraw(int aIdx) {
		// ZHUJJ Auto-generated method stub
		
	}
}
