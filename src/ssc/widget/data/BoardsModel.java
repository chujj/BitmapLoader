package ssc.widget.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;

import com.ds.bitmaputils.AtomBitmap;
import com.ds.bitmaputils.BitmapHelper;
import com.ds.bitmaputils.BitmapHelper.LEVEL;
import com.ds.bitmaputils.Cbitmap.CustomBuildAtomBitmapFactory;
import com.ds.ui.DsCanvasUtil;
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
	private Paint mTextPaint;
	
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

		mTextPaint = new Paint();
		mTextPaint.setTextSize(15);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(0xffc3c3c3);
		mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
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
		boolean retval = false;
		if (aIdx > mBoardsRef.length) return retval;
		
		mRect.set(0, 0, require_width, require_height);
		mC.drawColor(0xff000000);

		if (mBoardsRef[aIdx]._cover_image == null) {
			mC.drawColor(0xff3c3c3c);
			DsCanvasUtil.drawToCenterOfCanvas(mC, BitmapHelper.getInstance(null).mCommonLinearShadowBg, require_width, require_height, mRect);
			retval = true;
		} else {
			AtomBitmap abitmap = BitmapHelper.getInstance(mContext).
					getBitmap(mBoardsRef[aIdx]._cover_image.remote_query_url, LEVEL.ORIGIN, true, sFactory, mBoardsRef[aIdx]._cover_image);
			Bitmap bitmap =  abitmap.getBitmap();
			if (bitmap != null) {
				DsCanvasUtil.drawToCenterOfCanvas(mC, bitmap, require_width, require_height, mRect);
				retval = true;
			} else {
				mMyRenderer.refreshIdx(aIdx); //cause last call of BitmapGetTask doesn't have call back. So we force refresh here
				retval = false;
			}
		}
		
		final String title = mBoardsRef[aIdx]._title;
		mC.drawText(title,( require_width - mTextPaint.measureText(title))  / 2, require_height - 30, mTextPaint);

		return retval;
	}
	
	protected static CustomBuildAtomBitmapFactory sFactory = new CustomBuildAtomBitmapFactory() {

		@Override
		public AtomBitmap buildAtomBitmap(LEVEL level, Object userData) {
			if (userData != null) {
				return (ImageFile) userData;
			}
			return null;
		}
	};

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

	@Override
	public void drawAtOffset(float mCurrOffset, float calced_max_offset,
			float calced_min_offset) {
	}

	@Override
	public void deprecateToDraw(int aIdx) {

	}

}
