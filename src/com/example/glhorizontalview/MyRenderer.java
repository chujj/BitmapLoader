package com.example.glhorizontalview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.ds.io.DsLog;
import com.ds.views.MyScroller;
import com.example.glhorizontalview.ModelChangeCallback.ModelState;

import ssc.software.picviewer.R;
import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

public class MyRenderer implements GLSurfaceView.Renderer {
	protected final static int BytesPerFloat = 4;
	protected final static int PositionDataSize = 3;
	protected final static int TextureCoordinateDataSize = 2;

	private final Context mActivityContext;
	
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	
	private final FloatBuffer mFrontPositions, mBackPositions;
	private final FloatBuffer mFrontTextureCoordinates;
	
	private int mMVPMatrixHandle;
	private int mTextureUniformHandle;
	private int mPositionHandle;
	private int mTextureCoordinateHandle;
	private int mProgramHandle;
	
	private Item[] items;
	private Tile[] mTilePoll; private final int logoTileIdx = 0;
	private Bitmap mTileBitmap;
	private int mTileTextureHandle = -1;
	private Bitmap mLogo;

	private GLSurfaceView mGLSurfaceView;
	public MyRenderer(Context activityContext, GLSurfaceView sv, GLResourceModel aModel) {
		mActivityContext = activityContext;
		mGLSurfaceView = sv;
		mModel = aModel;
		mLogo = BitmapFactory.decodeResource(activityContext.getResources(), R.drawable.tm);
		if (aModel == null) {
			mModel = new GLResourceModel() {
				
				private static final int size = 40;
				@Override
				public int getCount() {
					return size;
				}
				
				@Override
				public boolean updateToCanvas(int aIdx, Canvas mC,
						int require_width, int require_height) {
					// we draw nothing here
					return true;
				}

				@Override
				public void clickAt(int hit) {

				}

				@Override
				public void currRenderView(MyRenderer render) {

				}

				@Override
				public void longClick(float x , float y , int hit) {

				}
			};
		}
		mModel.currRenderView(this);

		// X, Y, Z
		final float[] cubePositionData = {
				// Front face
				-PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, 0f, 
				-PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, 0f, 
				PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, 0f,
				-PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, 0f, 
				PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, 0f, 
				PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, 0f, };

		final float[] cubeTextureCoordinateData = {
				// Front face
				0.0f, 0.0f, 
				0.0f, 1.0f, 
				1.0f, 0.0f, 
				0.0f, 1.0f, 
				1.0f, 1.0f,
				1.0f, 0.0f, };

		mFrontPositions = ByteBuffer
				.allocateDirect(cubePositionData.length * BytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFrontPositions.put(cubePositionData).position(0);

		mFrontTextureCoordinates = ByteBuffer
				.allocateDirect(
						cubeTextureCoordinateData.length * BytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mFrontTextureCoordinates.put(cubeTextureCoordinateData).position(0);
		
		
		// reverse for logo
		// X, Y, Z
		final float[] logoPlanePositionData = {
				PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -0f,
				PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -0f,
				-PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -0f,
				PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -0f, 
				-PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -0f, 
				-PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -0f, 
		};
		mBackPositions = ByteBuffer
				.allocateDirect(logoPlanePositionData.length * BytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mBackPositions.put(logoPlanePositionData).position(0);
	}
	
	private static final int MSG_ONSCRELL = 0x0000;
	private static final int MSG_FLING = 0x0001;
	private static final int MSG_FINISH = 0x0002;
	private static final int MSG_HIT_TEST = 0x0003;
	private static final int MSG_MODEL_RELOAD = 0x0004;
	private static final int MSG_REFRESH_IDX = 0x0005;
	private static final int MSG_EXTERNAL_RUNNABLE = 0x0006;
	private static final int MSG_SWITCH_RENDER_MODE = 0x0007;
	
	private ArrayList<Message> mMessagesList = new ArrayList<Message>();
	
	private void sendMesg(Message msg) {
		if (mGLHandler == null) return;
		
		synchronized (mMessagesList) {
			mMessagesList.add(msg);
			// call surface view's check
			mGLSurfaceView.queueEvent(mGLHandler);
		}
	}
	
	private class MyGLHandler implements Runnable {

		@Override
		public void run() {
			synchronized (mMessagesList) {
				while (mMessagesList.size() > 0) {
//					DsLog.e("handle msg start");
					if (handleMessageAndNeedRefresh(mMessagesList.remove(0)))
						mGLSurfaceView.requestRender();
//					DsLog.e("handle msg end");
				}
			}
		}
		
		/** make sure x is still same toward of next spin animation
		 * @param x
		 * @return
		 */
		private boolean timeToTriggerSpinAnimation(float x) {
			return false;
		}
		
		private void startSpinAnimation(float x) {
			
		}

		public boolean handleMessageAndNeedRefresh(Message msg) {
			boolean refresh = true;
			
			if (inAutoAnimation && !mScroller.isFinished() && mScroller.inFlingMode() && (msg.what == MSG_ONSCRELL || msg.what == MSG_FLING)) {
				mScroller.forceFinished(true);
				inAutoAnimation = false;
			}
			
			switch (msg.what) {
			case MSG_ONSCRELL:
				float offset_x = msg.getData().getFloat("x");
				float offset_y = msg.getData().getFloat("y");
				if (inAutoAnimation) return refresh;
//				DsLog.e("eventHub onScroll");
				updateItems(offset_x, offset_y);
				break;
			case MSG_FLING:
				float vx = msg.getData().getFloat("x");
				float vy = msg.getData().getFloat("y");
				long durning =  (long) ( Math.abs(vx) * AUTO_ANIMATION_TIME_PER_PIXEL);
//				DsLog.e("eventHub onFling: vx: " + vx + " durning: " + durning);
				if (timeToTriggerSpinAnimation(vx)) {
					this.startSpinAnimation(vx);
					mScroller.forceFinished(true);
					inAutoAnimation = true;
				} else {
					mScroller.fling(mCurrOffset, 0, vx, 0, calced_min_offset - Distance, calced_max_offset + Distance, 0, 0, durning, rollback_routinue_msg);
					inAutoAnimation = true;
				}
				break;
			case MSG_FINISH:
				if (inAutoAnimation) return refresh;
//				DsLog.e("eventHub onFinish");
				float x = 0;
				if (msg.getData().containsKey("x")) {
					 x = msg.getData().getFloat("x");
				}
				
				rollback(x);
				break;
				
			case MSG_HIT_TEST:
//				DsLog.e("eventHub hitTest");
				float viewport_offset_x_percent = msg.getData().getFloat("viewport_offset_x_percent");
				float viewport_offset_y_percent = msg.getData().getFloat("viewport_offset_y_percent");

				int hit = testHit(viewport_offset_x_percent, viewport_offset_y_percent);
				if (inAutoAnimation) hit = -1;
				Bundle b = msg.getData();
				b.putInt("hit", hit);
				synchronized (b) {
					b.notifyAll();
				}
				
				break;
			case MSG_MODEL_RELOAD:
//				DsLog.e("eventHub modelReload");
				mMessagesList.clear();
				float next_offset_x = 0;;
				if (msg.obj != null) {
					ModelChangeCallback callback = ((ModelChangeCallback)msg.obj); 
					callback.onModelChanged(new ModelState(testHit(0.5f, 0.5f), mCurrOffset, mCurrMode));
					if (callback.mStat != null) {
						mCurrMode = callback.mStat.lastRenderMode;
						next_offset_x = callback.mStat.lastOffset;
						callback.mStat = null;
					}
				}
				initDimensionLimit();
				updateItems(next_offset_x, 0);
				break;
			case MSG_REFRESH_IDX:
//				DsLog.e("eventHub refreshIdx");
				refresh = false;
				for (int i = 0; i < items.length; i++) {
					if (items[i].validate && i == msg.arg1) {
						refresh = true;
						break;
					}
				}
				break;
			case MSG_EXTERNAL_RUNNABLE:
//				DsLog.e("eventHub external runnable");
				((Runnable)msg.obj).run();
				break;
			case MSG_SWITCH_RENDER_MODE:
				int nextMode = msg.arg1;
				if (nextMode == mCurrMode) break;
				
				mCurrMode = nextMode;
				// recalc size
				initDimensionLimit();
				updateItems(0, 0);
				// release last texture hander
				// realloc texture handel
				// redraw
				break;
			default:
				break;
			}
			return refresh;
		}
	}
	
	private int testHit(float nearPerX, float nearPerY) {
		int hit = -1;
		if (mCurrMode == MODE_CURVE) {
			if ( (Math.abs(nearPerY - 0.5f) / 0.5)< TOP_PERCENT && 
					(Math.abs(nearPerX - 0.5f) / 0.5)< LEFT_PERCENT) {
				hit = (int) (-mCurrOffset / Distance);
			}
		} else {
			float dx = (PLANE_VISIABLE_NEAR_X_END - PLANE_VISIABLE_NEAR_X_START) * nearPerX + PLANE_VISIABLE_NEAR_X_START;
			int idx = -1;
			for (int i = 0; i < items.length; i+= PLANE_ROW_COUNT) {
				if( (dx > items[i].offsetX - PLAN_HALF_WIDTH_FIXED) && (dx < items[i].offsetX + PLAN_HALF_WIDTH_FIXED)) {
					idx = i;
					break;
				}
			}
			if (idx != -1) {
				int _offset_y = (int) ((1 - nearPerY )/ (1.0f / PLANE_ROW_COUNT));
				hit = idx + _offset_y;
			} else {
				hit = -1;
			}
		}
		return hit;
	}
	private Runnable rollback_routinue_msg = new Runnable() {

		@Override
		public void run() {
//			DsLog.e("fling rollback msg send");
			sendMesg(Message.obtain(null, MSG_EXTERNAL_RUNNABLE, rollback_routinue));
		}
	};
		
	private Runnable rollback_routinue = new Runnable() {
		
		@Override
		public void run() {
//			DsLog.e("fling rollback called");
			rollback(0);
		}
	};
	
	private void rollback(float x) {
		
		if (mCurrOffset > calced_max_offset) {
			float dx =  (mCurrOffset - calced_max_offset);
			mScroller.startScroll(mCurrOffset, -1, -dx, 0,(long) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
			inAutoAnimation = true;
		} else if (mCurrOffset < calced_min_offset) {
			float dx =  (calced_min_offset - mCurrOffset);
			mScroller.startScroll(mCurrOffset, -1, dx, 0,(long) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
			inAutoAnimation = true;
		} else {
			if (mCurrMode == MODE_PLANE) return;
			
			if (((Math.abs( mCurrOffset % Distance) > 0.001) || x != 0)) {
				float left = Math.abs(mCurrOffset % Distance);
				float dx = 0; 
				if (left < Distance /2) {
					dx = -left;
				} else {
					dx = Distance - left;
				}
				if (x != 0) {
					dx = -x;
				}
				inAutoAnimation = true;
				mScroller.startScroll(mCurrOffset, 0, -dx, 0, (long) (Math.abs(dx) * AUTO_ANIMATION_TIME_PER_PIXEL));
			}
		}
	}
	
	private MyGLHandler mGLHandler;

	private void checkInitedOrRelease() {
		if (mGLHandler != null) {
			mGLHandler = null;
			GLES20.glDeleteProgram(mProgramHandle);
			mProgramHandle = -1;
			items = null;
			if (mTileTextureHandle != -1) {
				TextureHelper.deleteTexture(mActivityContext, mTileTextureHandle);
				mTileTextureHandle = -1;
			}
		}
	}
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		DsLog.e("lifet onSurfaceCreated");
		checkInitedOrRelease();
		mGLHandler = new MyGLHandler();
		if (Looper.myLooper() == Looper.getMainLooper())
			;
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		Matrix.setIdentityM(mViewMatrix, 0);

		final int vertexShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_VERTEX_SHADER, RawResourceReader
						.readTextFileFromRawResource(mActivityContext,
								R.raw.vertex_shader));

		final int fragmentShaderHandle = ShaderHelper.compileShader(
				GLES20.GL_FRAGMENT_SHADER, RawResourceReader
						.readTextFileFromRawResource(mActivityContext,
								R.raw.fragment_shader));

		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position",
						"a_TexCoordinate" });

	}

	private float PLANE_VISIABLE_NEAR_X_START, PLANE_VISIABLE_NEAR_X_END;
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		DsLog.e("lifet onSurfaceChanged " + width + " h:" + height);
		GLES20.glViewport(0, 0, width, height);

		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		
		PLANE_VISIABLE_NEAR_X_START = - (ratio / NEAR * ( -PLAN_TRASLATE_Z + PLANE_OFFSET_Z));
		PLANE_VISIABLE_NEAR_X_END = -PLANE_VISIABLE_NEAR_X_START;

		LEFT_PERCENT = (NEAR * (PLAN_HALF_WIDTH_FIXED / (-PLAN_TRASLATE_Z))) / ratio;
		RIGHT_PERCENT = LEFT_PERCENT;
		Matrix.frustumM(mProjectionMatrix, 0, left, right,
				-PLAN_HEIGHT_MAXIMIN, PLAN_HEIGHT_MAXIMIN, NEAR, FAR);
		
		calcTileSizeForBothMode(width, height);
		initDimensionLimit();
		updateItems(0, 0);
	}
	
	// setup the tile cache's position all in one texture
	private void calcTileSizeForBothMode(int width, int height) {
		int[] t_max_size = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, t_max_size, 0);
		t_max_size[0] = t_max_size[0] / 2; // half size of the maximn, 8192 in virtualbox always crashed

		{// curve-mode: 1. calc count ; 2. calc size
			int min_count = (ONE_SIZE_COUNT * 2) + 1 + 1; // +1 for logo
			int max_count = (t_max_size[0] / SUGGEST_MAX_TILE_SIZE);
			max_count = max_count * max_count;
			if (max_count >= min_count) { // use as much tile as possible
				Curve_Tile_Count = max_count;
				Curve_Tile_Size = SUGGEST_MAX_TILE_SIZE;
			} else { // scale in the size to fit min_count, for better pic qulite
				Curve_Tile_Count = min_count;
				// we put all tile as squarly as possible
				Curve_Tile_Size =  t_max_size[0] / ( (int) (Math.floor(Math.sqrt(min_count))) + 1);
			}
		}		

		{// plane: 1.calc count; 2. calc size
			int min_count = (int) (((PLANE_VISIABLE_NEAR_X_END - PLANE_VISIABLE_NEAR_X_START) / Distance) + 1);
			min_count = min_count * PLANE_ROW_COUNT;
			min_count += 1; // +1 for logo
			int max_count = (t_max_size[0] / SUGGEST_MAX_TILE_SIZE);
			max_count = max_count * max_count;
			if (max_count >= min_count) { // use as much tile as possible
				Plane_Tile_Count = max_count;
				Plane_Tile_Size = SUGGEST_MAX_TILE_SIZE;
			} else { // scale in the size to fit min_count, for better pic qulite
				Plane_Tile_Count = min_count;
				Plane_Tile_Size =  t_max_size[0] / ( (int) (Math.floor(Math.sqrt(min_count))) + 1);
			}
		}

	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
//		DsLog.e("lifet onDrawFrame");
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glUseProgram(mProgramHandle);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVPMatrix");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_Position");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_TexCoordinate");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTileTextureHandle);
		GLES20.glUniform1i(mTextureUniformHandle, 0);
		
		for (int i = 0; i < items.length; i++) {
			if ( !(items[i].validate)) {
				items[i].deprecateToDraw();
				continue;
			}
//			items[i].prepareToDraw();
			items[i].prepareToDraw2();

			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, items[i].offsetX, items[i].y, PLAN_TRASLATE_Z + items[i].offsetZ);
			Matrix.rotateM(mModelMatrix, 0, items[i].degree, 0.0f, 1.0f, 0.0f);
			
			mFrontPositions.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, PositionDataSize,
					GLES20.GL_FLOAT, false, 0, mFrontPositions);
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			
			FloatBuffer fb = mTilePoll[ 
//			                            logoTileIdx 
			                            items[i].mTileIdx
			                            ].mCoordinates;
			fb.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
					TextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
					fb);
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			
			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
			
			// draw logo
			mBackPositions.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, PositionDataSize,
					GLES20.GL_FLOAT, false, 0, mBackPositions);
			GLES20.glEnableVertexAttribArray(mPositionHandle);

			fb = mTilePoll[logoTileIdx].mCoordinates;
			fb.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
					TextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
					fb);
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		
		if (inAutoAnimation) {
			inAutoAnimation = continueAnimation();
			mGLSurfaceView.requestRender();
		}
//		testSubTex();
		int error = glUnused.glGetError();
		if (error != 0)
			DsLog.e("GL Error: " + Integer.toHexString(error));
	}

	////////////////////////////////animation part ////////////////////////////////
	private final static int View_part = 1;
	
	private final static int SUGGEST_MAX_TILE_SIZE = 256;
	
	private int Curve_Tile_Size = 256; // ZHUJJ-TODO use different tile_size under differnt reder_mode, accord the screen size
	private int Curve_Tile_Count = -1;
	private int Plane_Tile_Size = 100;
	private int Plane_Tile_Count = -1;
	
	private int mCurr_Tile_Size;
	private int mCurr_Tile_Count;
	
	private final static float PLAN_HEIGHT_MAXIMIN = 1.0f;
	private final static float PLAN_HALF_WIDTH_FIXED = 1.0f; // y = k*x^2
	private final static float PLAN_NEARLEST_GAP = 0.4f;
	private final static float PLAN_NEARLEST_GAP_DEPTH = 0.3f; // -z toward
	private final static float PLAN_TRASLATE_Z = -3f;
	private final static float Distance = (float) (PLAN_HALF_WIDTH_FIXED * 2 + PLAN_NEARLEST_GAP );
	private final static float K = (float) ( PLAN_NEARLEST_GAP_DEPTH / (
			Distance * Distance / 4));
	private final static int ONE_SIZE_COUNT = 2;
	private final static float Max_Depth = (float) (K * Math.pow((Distance * ONE_SIZE_COUNT), 2));
	private final static float Over_Max_Rotate_Degree = 180f;
	
	private final static float NEAR = 1.5f;
	private final static float FAR = Max_Depth + 5;
	// p?: the top percent of nearest plan in viewport
	// tanginta == tanginta --> p? / near == t / (near + t_z) -->> p? = near * (t / (near + t_z))
	private final static float TOP_PERCENT = NEAR * (PLAN_HEIGHT_MAXIMIN / ( -PLAN_TRASLATE_Z)) / PLAN_HEIGHT_MAXIMIN;
	private final static float BOTTOM_PERCENT = TOP_PERCENT;
	private static float LEFT_PERCENT, RIGHT_PERCENT; // cause viewport change in width, this value should calced continuely
	
	private float calced_min_offset, calced_max_offset;
	
	private final static int animation_part = 1;
	private MyScroller mScroller;
	private final static long AUTO_ANIMATION_TIME_PER_PIXEL = 200;
	private boolean inAutoAnimation;

	private float mCurrOffset;
	
	private GLResourceModel mModel;
	
	private int mCurrMode =
//			MODE_PLANE;
			MODE_CURVE;
	public final static int MODE_CURVE = 0;
	public final static int MODE_PLANE = 1;
	
	/** 
	 * @return true if animation should draw nextframe yet
	 */
	private boolean continueAnimation() {
		boolean finish = mScroller.computeScrollOffset();
		mCurrOffset = mScroller.getCurrX();
//		DsLog.e("curr: " + mCurrOffset + " durning: " + mScroller.getDuration() + " finished? " + finish);
		this.updateItems(0, 0);
		return finish;
	}
	
	protected IEvent eventHandler = new IEvent() {

		@Override
		public void onScroll(float offset_x, float offset_y) {
//			DsLog.e("event Sender onscroll");
			Message m = Message.obtain(null, MSG_ONSCRELL);
			Bundle b = new Bundle();
			b.putFloat("x", offset_x);
			b.putFloat("y", offset_y);
			m.setData(b);
			sendMesg(m);
		}

		@Override
		public void onFling(float velocity_x, float velocity_y) {
//			DsLog.e("event Sender onFling");
			Message m = Message.obtain(null, MSG_FLING);
			Bundle b = new Bundle();
			b.putFloat("x", velocity_x);
			b.putFloat("y", velocity_y);
			m.setData(b);
			sendMesg(m);
		}

		@Override
		public void onClick(float x, float y) {
//			DsLog.e("event Sender onClick");
			Message m = Message.obtain(null, MSG_HIT_TEST);
			Bundle b = new Bundle();
			b.putFloat("viewport_offset_x_percent", x);
			b.putFloat("viewport_offset_y_percent", y);
			m.setData(b);
			synchronized (b) {
				try {
					sendMesg(m); // avoid notified before call wait, so synchnized on object_b
					b.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int hit = m.getData().getInt("hit", -1);
			if (hit != -1) {
				DsLog.e(" onClick x y: " + x + " " + y + " hit: " + m.getData().getInt("hit", -1));
				mModel.clickAt(hit);
			} else {
				if (mCurrMode == MODE_PLANE) return;
				// auto scroll accord with the down location
				float offset = (Distance * -(Math.signum(x - 0.5f)));
				m.setData(null);
				m.what = MSG_FINISH;
				b.clear();
				b.putFloat("x", offset);
				b.putFloat("y", y);
				m.setData(b);
				sendMesg(m);
			}
		}

		@Override
		public void onFinish(float x, float y) { // roll back
//			DsLog.e("event Sender onFinish");
			Message m = Message.obtain(null, MSG_FINISH);
			sendMesg(m);
		}

		@Override
		public void onLongPress(float x, float y, float origin_x, float origin_y) {
			Message m = Message.obtain(null, MSG_HIT_TEST);
			Bundle b = new Bundle();
			b.putFloat("viewport_offset_x_percent", x);
			b.putFloat("viewport_offset_y_percent", y);
			m.setData(b);
			synchronized (b) {
				try {
					sendMesg(m); // avoid notified before call wait, so synchnized on object_b
					b.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			int hit = m.getData().getInt("hit", -1);
			DsLog.e(" onLongPress at x y: " + x + " " + y + " hit: " + hit);
			if (hit != -1) {
				mModel.longClick(origin_x , origin_y , hit);
			} else {
				
			}
			
		}

	};
	
	private void dumpTextureLimit () {
		int[] maxSize = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);

		int[] maxNum = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS, maxNum, 0);

		DsLog.e("max pixels size: " + maxSize[0] + " max_pixel_unit: " + maxNum[0]);
	}

	private void initDimensionLimit() {
		mScroller = new MyScroller(mActivityContext);
		mCurrOffset = 0;

		Paint p = new Paint();
		p.setTextSize(50);
		p.setColor(0xff00ff00);
		
		int size = mModel.getCount();
		if (mCurrMode == MODE_CURVE) {
			mCurr_Tile_Size = Curve_Tile_Size;
			mCurr_Tile_Count = Curve_Tile_Count;
		} else {
			mCurr_Tile_Size = Plane_Tile_Size;
			mCurr_Tile_Count = Plane_Tile_Count;			
		}
		
		final int tile_size = mCurr_Tile_Size; // ZHUJJ NO NEED allocate again when we in same session, if we always use the half size, or mode not changed 
		items = new Item[size];
		Bitmap.Config cf = Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(tile_size, tile_size, cf);
		Canvas c = new Canvas(bitmap);
		for (int i = 0; i < size ;i++) {
			items[i] = new Item(bitmap, c, p, i * Distance, i, mCurrMode);
		}
		
		if (mCurrMode == MODE_CURVE) {
			calced_max_offset = 0;
			calced_min_offset =  -( items.length - 1 ) * Distance;
		} else {
			calced_max_offset = 0;
			calced_min_offset =  -( items.length / PLANE_ROW_COUNT ) * Distance;
		}

		// calc the tile count maybe used in render routine
		final int count = mCurr_Tile_Count;

		// check max texture size
		int[] t_max_size = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, t_max_size, 0);
		int it = (int) (Math.floor(Math.sqrt(count))); 
		if ((it * tile_size) > t_max_size[0]) {
			throw new RuntimeException("multi textures NOT Supported!: square count: " + it + " tile_size: " + tile_size + " bigger than limit: " + t_max_size[0]);
		}

		mTilePoll = new Tile[count];
		for (int i = 0; i < mTilePoll.length; i++) {
			mTilePoll[i] = new Tile(i, count, it);
		}

		mTileBitmap = Bitmap.createBitmap(tile_size * it, tile_size * it, cf);
		if (mTileTextureHandle != -1) {
			TextureHelper.deleteTexture(mActivityContext, mTileTextureHandle);
			mTileTextureHandle = -1;
		}
		mTileTextureHandle = TextureHelper.loadTexture(mActivityContext, mTileBitmap);
		mTileBitmap.recycle();

		mTilePoll[logoTileIdx].markInUse(logoTileIdx); //NOTE, here is some trick. Cause inUsed flag marked, the idx will not calced in reuse
		mTilePoll[logoTileIdx].dataLoaded = true;
		// draw logo, and update into texture
		c.drawColor(0xff000000);
		c.drawBitmap(mLogo, (tile_size - mLogo.getWidth()) / 2, (tile_size - mLogo.getHeight()) / 2, null);
//		mLogo.recycle();
//		mLogo = null;
		GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 
				tile_size * mTilePoll[logoTileIdx].column_idx, 
				tile_size * mTilePoll[logoTileIdx].row_idx,
				bitmap);
	}

	private void updateItems(float offset_x, float offset_y) {
		mCurrOffset += offset_x;
		float progress = 0;
		final float max = calced_max_offset;
		final float min = calced_min_offset;
		// calc over progress
		if (mCurrOffset > max) {
			progress = Math.min((mCurrOffset - 0) / Distance , 1);
		} else if (mCurrOffset < min) {
			progress = Math.max((mCurrOffset  - min) / Distance, -1);
		}
		
		// limit under over
		mCurrOffset =  Math.max(min - Distance, 
				Math.min(mCurrOffset, max + Distance));

//		DsLog.e("currentOffset: " + mCurrOffset);
		for (int i = 0; i < items.length; i++) {
			items[i].calcOffset(
					Math.max( min, Math.min(max, mCurrOffset))
					, Over_Max_Rotate_Degree * progress);
//			DsLog.e("item " + i + ":" + items[i].toString());
		}
	}
	
	public final static int PLANE_ROW_COUNT = 3;
	public final static float PLANE_HEIGHT = Distance * PLANE_ROW_COUNT - PLAN_NEARLEST_GAP;
	public final static float PLANE_TOP_Y_OFFSET_START =  PLANE_HEIGHT / 2;
	public final static float PLANE_OFFSET_Z = ( ((Distance * PLANE_ROW_COUNT + PLAN_NEARLEST_GAP ) / 2) *
			NEAR / PLAN_HEIGHT_MAXIMIN) - ( -PLAN_TRASLATE_Z);

	private class Item {
		private float x, y, offsetX, offsetZ, degree;
		private boolean validate;
		private Bitmap mBitmap;
		
		private int mTileIdx, mLastTileIdx;
		private Canvas mC;
		private Paint mP;
		private int mIdx;
		private int mMode;
		
		
		public Item(Bitmap bitmap, Canvas c, Paint p, float  ax, int idx, int currmode) {
			mMode = currmode;
			
			mBitmap = bitmap;
			validate = false;
			mTileIdx = -1;
			mLastTileIdx = -1;
			mC = c;
			mP = p;
			mIdx = idx;
			if (mMode == MODE_CURVE) {
				x = ax;
				offsetX = -1;
				y = 0;
			} else {
				y = PLANE_TOP_Y_OFFSET_START - idx % PLANE_ROW_COUNT * Distance - PLAN_HALF_WIDTH_FIXED;
				x = idx / PLANE_ROW_COUNT * Distance;
				offsetZ =  -PLANE_OFFSET_Z;
			}
		}

		public void calcOffset(float offset_x, float f) {
//			if (offset_x == offsetX) return;
			
			if (mMode == MODE_CURVE) {
				calcOffsetModeCurve(offset_x, f);
			} else {
				calcOffsetModePlane(offset_x, f);
			}
		}
		
		private final void calcOffsetModeCurve(float offset_x, float f) {
			offsetX = offset_x + x; // x changed
			degree = f + offsetX * 45 / (Distance)   * (offsetX > 0 ? -1 : -1); // add spin feeling
			offsetZ = (float) (-K * Math.pow(offsetX, 2)); // z changed;
			boolean old_stat = validate;
			validate = (-offsetZ > Max_Depth) ? false : true;
			markOrReleaseTile(old_stat);
		}
		
		private final void calcOffsetModePlane(float offset_x, float f) {
			degree = f;
			offsetX = offset_x + x; // x changed
			boolean old_stat = validate;
			final float left = offsetX - PLAN_HALF_WIDTH_FIXED;
			final float right = offsetX + PLAN_HALF_WIDTH_FIXED ;
			if ((left > PLANE_VISIABLE_NEAR_X_START && left < PLANE_VISIABLE_NEAR_X_END) || 
					(right > PLANE_VISIABLE_NEAR_X_START && right < PLANE_VISIABLE_NEAR_X_END)) {
				validate = true;
			} else {
				validate = false;
			}

			markOrReleaseTile(old_stat);
		}
		
		private final void markOrReleaseTile(boolean old_stat) {
			if (old_stat != validate) {
				if (validate) {
					if (mLastTileIdx != -1 && mTilePoll[mLastTileIdx].getUsedToken() == mIdx) {
						mTilePoll[mLastTileIdx].markInUse(mIdx);
						mTileIdx = mLastTileIdx;
						return;
					}

					int tile = findUnusedTile();
					if (tile == -1) {
						throw new RuntimeException("Find tile fail, unacceptable");
					} else {
						mTilePoll[tile].markInUse(mIdx);
						mTileIdx = tile;
					}
				} else  {
					if (mTileIdx == -1) {
						throw new RuntimeException("validate changed here, but why last idx is  still invalidate? unacceptable");
					}
					mTilePoll[mTileIdx].unmark();
					mLastTileIdx = mTileIdx;
					mTileIdx = -1;
				}
			}
		}
				
		public void deprecateToDraw() {
		}

		public void prepareToDraw2() {
			if (mTileIdx == -1) {
				throw new RuntimeException("invalidate tile idx, should calc already");
			} else {
				if (mTilePoll[mTileIdx].dataLoaded) {
					; // nothing to do
				} else {
					mC.drawColor(0xffffffff);

					mC.drawText(Integer.toString(mIdx), 0, 50, mP);
					// zhujj: here, we should always make set the dataLoaded flag to save time. But I print the time cost is very small, and the 
					// algorithem of change the flag is complex, so I just ignore here.
					mTilePoll[mTileIdx].dataLoaded = mModel.updateToCanvas(mIdx, mC, mCurr_Tile_Size, mCurr_Tile_Size);
					// we do not need to bind, which already bind before this called
					GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 
							mCurr_Tile_Size * mTilePoll[mTileIdx].column_idx, 
							mCurr_Tile_Size * mTilePoll[mTileIdx].row_idx,
							mBitmap);
					DsLog.e("update pixel of id: " + mIdx  + " into tile: " + mTileIdx);
				}
			}
		}

		@Override
		public String toString() {
			return "ox: " + x + " oy: " + y + " ostx: " + offsetX + " ostz: " + offsetZ;
		}

	}

	private int findUnusedTile() {
		int first_cached = -1;
		int first_clean = -1;
		
		for (int i = 0; i < mTilePoll.length; i++) {
			
			if (!mTilePoll[i].inUse ) { // cached
				if (first_cached == -1 && mTilePoll[i].getUsedToken() != -1) {
					first_cached = i;
				} else if (first_clean == -1 && mTilePoll[i].getUsedToken() == -1) {
					first_clean = i;
				}
			} 
		}

		if (first_clean != -1) {
			return first_clean;
		} else {
			return first_cached;
		}
	}

	public void modelChanged(ModelChangeCallback callback) {
		sendMesg(Message.obtain(null, MSG_MODEL_RELOAD, callback));
	}

	public void refreshIdx(int idxToRefresh) {
		sendMesg(Message.obtain(null, MSG_REFRESH_IDX, idxToRefresh, -1));
	}
	
	public void changeRenderMode (int mode) {
		if (mode == -1) {
			if (mCurrMode == MODE_CURVE) {
				mode = MODE_PLANE;
			} else {
				mode = MODE_CURVE;
			}
		}
		sendMesg(Message.obtain(null, MSG_SWITCH_RENDER_MODE, mode, -1));
	}

}
