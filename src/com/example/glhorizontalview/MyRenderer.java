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
import com.example.bitmaploader.R;
import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

public class MyRenderer implements GLSurfaceView.Renderer {
	private final Context mActivityContext;

	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	private final FloatBuffer mCubePositions;
	private final FloatBuffer mCubeTextureCoordinates;

	private int mMVPMatrixHandle;
	private int mTextureUniformHandle;
	private int mPositionHandle;
	private int mTextureCoordinateHandle;
	private int mProgramHandle;

	private final int mBytesPerFloat = 4;
	private final int mPositionDataSize = 3;
	private final int mTextureCoordinateDataSize = 2;

	private GLSurfaceView mGLSurfaceView;
	public MyRenderer(Context activityContext, GLSurfaceView sv) {
		mActivityContext = activityContext;
		mGLSurfaceView = sv;

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

		mCubePositions = ByteBuffer
				.allocateDirect(cubePositionData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubePositions.put(cubePositionData).position(0);

		mCubeTextureCoordinates = ByteBuffer
				.allocateDirect(
						cubeTextureCoordinateData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
	}
	
	private static final int MSG_ONSCRELL = 0x0000;
	private static final int MSG_FLING = 0x0001;
	private static final int MSG_FINISH = 0x0002;
	
	private ArrayList<Message> mMessagesList = new ArrayList<Message>();
	
	public void sendMesg(Message msg) {
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
				if (mMessagesList.size() > 0) {
					handleMessage(mMessagesList.remove(0));
					mGLSurfaceView.requestRender();
				}
			}
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ONSCRELL:
				float offset_x = msg.getData().getFloat("x");
				float offset_y = msg.getData().getFloat("y");
				if (inAutoAnimation) return;
				updateItems(offset_x, offset_y);
				break;
			case MSG_FLING:
				
				break;
			case MSG_FINISH:
				float x = msg.getData().getFloat("x");
				float y = msg.getData().getFloat("y");
				if (mCurrOffset > calced_max_offset) {
					float dx =  (mCurrOffset - calced_max_offset);
					mScroller.startScroll(mCurrOffset, -1, -dx, 0,(long) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
					inAutoAnimation = true;
				} else if (mCurrOffset < calced_min_offset) {
					float dx =  (calced_min_offset - mCurrOffset);
					mScroller.startScroll(mCurrOffset, -1, dx, 0,(long) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
					inAutoAnimation = true;
				} else {
					if (!inAutoAnimation && Math.abs(mCurrOffset % Distance) > 0.001) {
						float left = Math.abs(mCurrOffset % Distance);
						float dx = 0; 
						if (left < Distance /2) {
							dx = -left;
						} else {
							dx = Distance - left;
						}
						inAutoAnimation = true;
						mScroller.startScroll(mCurrOffset, 0, -dx, 0, (long) (Math.abs(dx) * AUTO_ANIMATION_TIME_PER_PIXEL));
					}
				}
				
				break;
			default:
				break;
			}
		}
	}
	
	private MyGLHandler mGLHandler;

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
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

		initDimensionLimit();
		
		updateItems(0, 0);
		
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;

		Matrix.frustumM(mProjectionMatrix, 0, left, right,
				-PLAN_HEIGHT_MAXIMIN, PLAN_HEIGHT_MAXIMIN, NEAR, FAR);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
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
			Matrix.translateM(mModelMatrix, 0, items[i].offsetX, 0.0f, PLAN_TRASLATE_Z + items[i].offsetZ);
			Matrix.rotateM(mModelMatrix, 0, items[i].degree, 0.0f, 1.0f, 0.0f);
			
			mCubePositions.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize,
					GLES20.GL_FLOAT, false, 0, mCubePositions);
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			
			FloatBuffer fb = mTilePoll[items[i].mTileIdx].mCoordinates;
			fb.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
					mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
					fb);
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			
			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		
		if (inAutoAnimation) {
			inAutoAnimation = continueAnimation();
			mGLSurfaceView.requestRender();
		}
//		testSubTex();
//		int error = glUnused.glGetError();
//		DsLog.e("GL Error: " + error);
	}

	////////////////////////////////animation part ////////////////////////////////
	private final static int View_part = 1;

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
	private final static float Over_Max_Rotate_Degree = 45f;
	
	private final static float NEAR = 1.5f;
	private final static float FAR = Max_Depth + 5;
	
	private float calced_min_offset, calced_max_offset;
	
	private final static int animation_part = 1;
	private MyScroller mScroller;
	private final static long AUTO_ANIMATION_TIME_PER_PIXEL = 200;
	private boolean inAutoAnimation;

	private float mCurrOffset;
	
	/** 
	 * @return true if animation should draw nextframe yet
	 */
	private boolean continueAnimation() {
		boolean finish = mScroller.computeScrollOffset();
		mCurrOffset = mScroller.getCurrX();
//		DsLog.e("curr: " + mCurrOffset + " dst: " + mScroller.getFinalX());
		this.updateItems(0, 0);
		return finish;
	}
	
	protected IEvent eventHandler = new IEvent() {

		@Override
		public void onScroll(float offset_x, float offset_y) {
			Message m = Message.obtain(null, MSG_ONSCRELL);
			Bundle b = new Bundle();
			b.putFloat("x", offset_x);
			b.putFloat("y", offset_y);
			m.setData(b);
			sendMesg(m);
		}

		@Override
		public void onFling(float velocity_x, float velocity_y) {

		}

		@Override
		public void onClick(float x, float y) {

		}

		@Override
		public void onFinish(float x, float y) { // roll back
			Message m = Message.obtain(null, MSG_FINISH);
			Bundle b = new Bundle();
			b.putFloat("x", x);
			b.putFloat("y", y);
			m.setData(b);
			sendMesg(m);
		}

	};

	private void initDimensionLimit() {
		mScroller = new MyScroller(mActivityContext);
		mCurrOffset = 0;
		
		int[] resourceIds = new int[] {
				R.drawable.p6,
				R.drawable.p5,
				R.drawable.p4,
				R.drawable.p1,
				R.drawable.p2,
				R.drawable.p3,
		};
		Paint p = new Paint();
		p.setTextSize(50);
		p.setColor(0xff00ff00);
		
		int size = 20; //resourceIds.length;
		items = new Item[size];
		Bitmap.Config cf = Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(100, 100, cf);
		Canvas c = new Canvas(bitmap);
		for (int i = 0; i < size ;i++) {
//			final BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inScaled = false;	
//			Bitmap bitmap = BitmapFactory.decodeResource(mActivityContext.getResources(), resourceIds[i], options);
			items[i] = new Item(bitmap, c, p, i * Distance, i);
		}
		
		calced_max_offset = 0;
		calced_min_offset =  -( items.length - 1 ) * Distance;
		
		int count = (ONE_SIZE_COUNT * 2) + 1 + 1;
		mTilePoll = new Tile[count];
		for (int i = 0; i < mTilePoll.length; i++) {
			mTilePoll[i] = new Tile(i, count);
		}
		
		mTileBitmap = Bitmap.createBitmap(100 * count, 100, cf);
		mTileTextureHandle = TextureHelper.loadTexture(mActivityContext, mTileBitmap);
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

		for (int i = 0; i < items.length; i++) {
			items[i].calcOffset(
					Math.max( min, Math.min(max, mCurrOffset))
					, Over_Max_Rotate_Degree * progress);
//			DsLog.e("item " + i + ":" + items[i].toString());
		}
	}

	
	private class Item {
		private float x, offsetX, offsetZ, degree;
		private boolean validate;
		private Bitmap mBitmap;
		private int mTextureHandle;
		private int mTileIdx;
		private Canvas mC;
		private Paint mP;
		private int mIdx;
		
		
		public Item(Bitmap bitmap, Canvas c, Paint p, float  ax, int idx) {
			mBitmap = bitmap;
			mTextureHandle = -1;
//			mTextureHandle = TextureHelper.loadTexture(mActivityContext, bitmap);
//			DsLog.e("try load text with result: " + mTextureHandle);
			validate = false;
			x = ax;
			offsetX = -1;
			mTileIdx = 0;
			mC = c;
			mP = p;
			mIdx = idx;
		}

		public void calcOffset(float offset_x, float f) {
//			if (offset_x == offsetX) return;
			
			degree = f;
			offsetX = offset_x + x; // x changed
			offsetZ = (float) (-K * Math.pow(offsetX, 2)); // z changed;
			boolean old_stat = validate;
			validate = (-offsetZ > Max_Depth) ? false : true;
			if (old_stat != validate) {
				if (validate) {
					int tile = findUnusedTile();
					if (tile == -1) {
						throw new RuntimeException("Find tile fail, unacceptable");
					} else {
						mTilePoll[tile].markInUse();
						mTileIdx = tile;
					}
				} else  {
					if (mTileIdx == -1) {
						throw new RuntimeException("here tile should in used before release, unacceptable");
					}
					mTilePoll[mTileIdx].unmark();
					mTileIdx = -1;
				}
			}
		}
				
		public void deprecateToDraw() {
//			if (mTextureHandle != -1) {
//				TextureHelper.deleteTexture(mActivityContext, mTextureHandle);
//				mTextureHandle = -1;
//			}
		}
		
		public boolean prepareToDraw() {
			if (validate && mTextureHandle == -1) {
				mTextureHandle = TextureHelper.loadTexture(mActivityContext, mBitmap);
				validate = true;
			}
			return true;
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
					// we do not need to bind, which already bind before this called
					GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 
							100 * mTileIdx, 
							0, 
							mBitmap);
				}
			}
		}

		@Override
		public String toString() {
			return "ox: " + x + " ostx: " + offsetX + " ostz: " + offsetZ + " th: " + mTextureHandle;
		}

	}
	
	private Item[] items;
	
	private Tile[] mTilePoll;
	private Bitmap mTileBitmap;
	private int mTileTextureHandle;

	private class Tile {
		private FloatBuffer mCoordinates;
		private boolean inUse , dataLoaded;

		public Tile(int i, int length) {
			float step = 1.0f / length;
			float start =  i * step;
			float end =  start + step;
			float[] cubeTextureCoordinateData = {
					// Front face
					start, 0.0f, 
					start, 1.0f, 
					end, 0.0f, 
					start, 1.0f, 
					end, 1.0f,
					end, 0.0f, };
			mCoordinates = ByteBuffer
				.allocateDirect(
						cubeTextureCoordinateData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
			mCoordinates.put(cubeTextureCoordinateData).position(0);
			inUse = false;
			dataLoaded = false;
		}

		public void unmark() {
			inUse = false;
			dataLoaded = false;
		}

		public void markInUse() {
			inUse = true;
			dataLoaded = false;
		}
		
	}
	private int findUnusedTile() {
		for (int i = 0; i < mTilePoll.length; i++) {
			if (!mTilePoll[i].inUse) {
				return i;
			}
		}
		return -1;
	}

}
