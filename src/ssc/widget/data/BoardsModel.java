package ssc.widget.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;

import com.example.glhorizontalview.GLResourceModel;
import com.example.glhorizontalview.ModelChangeCallback;
import com.example.glhorizontalview.MyRenderer;

public class BoardsModel implements GLResourceModel {


	private Context mContext;
	private MyRenderer mMyRenderer;
	private Rect mRect;
	
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
	}


	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public boolean updateToCanvas(int aIdx, Canvas mC, int require_width,
			int require_height) {
		mRect.set(0, 0, require_width, require_height);
		mC.drawColor(0xff880000);
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
