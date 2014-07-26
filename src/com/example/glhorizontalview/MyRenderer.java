package com.example.glhorizontalview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.widget.Scroller;

import com.ds.io.DsLog;
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

	public MyRenderer(final Context activityContext) {
		mActivityContext = activityContext;

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
				0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
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

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
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

		for (int i = 0; i < items.length; i++) {
			if ( !(items[i].validate)) {
				items[i].deprecateToDraw();
				continue;
			}
			items[i].prepareToDraw();

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, items[i].mTextureHandle);
			GLES20.glUniform1i(mTextureUniformHandle, 0);
			
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, items[i].offsetX, 0.0f, PLAN_TRASLATE_Z + items[i].offsetZ);
			Matrix.rotateM(mModelMatrix, 0, items[i].degree, 0.0f, 1.0f, 0.0f);
			
			mCubePositions.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize,
					GLES20.GL_FLOAT, false, 0, mCubePositions);
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			
			mCubeTextureCoordinates.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
					mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
					mCubeTextureCoordinates);
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			
			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
		}
		
		if (inAutoAnimation) {
			inAutoAnimation = continueAnimation();
		}
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
	private final static float Max_Depth = (float) (K * Math.pow((Distance * 2), 2));
	private final static float Over_Max_Rotate_Degree = 45f;
	
	private final static float NEAR = 1.5f;
	private final static float FAR = Max_Depth + 5;
	
	private float calced_min_offset, calced_max_offset;
	
	private final static int animation_part = 1;
	private Scroller mScroller; // ZHUJJ rewrite use float update precise 
	private final static long AUTO_ANIMATION_TIME_PER_PIXEL = 500;
	private boolean inAutoAnimation;
	private long mThisAnimationTime;
	private float mThisAnimationDestOffset;
	private long mLastFrameTimeStamp;

	private float mCurrOffset;
	
	/** 
	 * @return true if animation should draw nextframe yet
	 */
	private boolean continueAnimation() {
		boolean finish = mScroller.computeScrollOffset();
		mCurrOffset = mScroller.getCurrX();
		DsLog.e("curr: " + mCurrOffset + " dst: " + mScroller.getFinalX());
		this.updateItems(0, 0);
		return finish;
	}
	
	protected IEvent eventHandler = new IEvent() {

		@Override
		public void onScroll(float offset_x, float offset_y) {
			if (inAutoAnimation) return;
			updateItems(offset_x, offset_y);
		}

		@Override
		public void onFling(float velocity_x, float velocity_y) {

		}

		@Override
		public void onClick(float x, float y) {

		}

		@Override
		public void onFinish(float x, float y) { // roll back
			inAutoAnimation = true;
			mLastFrameTimeStamp = System.currentTimeMillis();
			if (mCurrOffset > calced_max_offset) {
				int dx = (int )(mCurrOffset - calced_max_offset);
				mScroller.startScroll((int)mCurrOffset, -1, dx, 0, (int) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
			} else if (mCurrOffset < calced_min_offset) {
				int dx = (int) (calced_min_offset - mCurrOffset);
				mScroller.startScroll((int) mCurrOffset, -1, dx, 0, (int) ( dx * AUTO_ANIMATION_TIME_PER_PIXEL));
			} else {
				mThisAnimationDestOffset = mCurrOffset;
				mScroller.startScroll(0, 0, 0, 0, 0);
			}
			
		}

	};

	private void initDimensionLimit() {
		mScroller = new Scroller(mActivityContext);
		mCurrOffset = 0;
		
		int[] resourceIds = new int[] {
				R.drawable.p6,
				R.drawable.p5,
				R.drawable.p4,
				R.drawable.p1,
				R.drawable.p2,
				R.drawable.p3,
		};
		
		items = new Item[resourceIds.length];
		int x = 0;
		for (int i = 0; i < resourceIds.length; i++) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;	
			Bitmap bitmap = BitmapFactory.decodeResource(mActivityContext.getResources(), resourceIds[i], options);
			items[i] = new Item(bitmap, i * Distance);
		}
		
		calced_max_offset = 0;
		calced_min_offset =  -( items.length - 1 ) * Distance;
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
		
		
		public Item(Bitmap bitmap, float  ax) {
			mBitmap = bitmap;
			mTextureHandle = -1;
//			mTextureHandle = TextureHelper.loadTexture(mActivityContext, bitmap);
//			DsLog.e("try load text with result: " + mTextureHandle);
			validate = false;
			x = ax;
			offsetX = -1;
		}

		public void calcOffset(float offset_x, float f) {
//			if (offset_x == offsetX) return;
			
			degree = f;
			offsetX = offset_x + x; // x changed
			offsetZ = (float) (-K * Math.pow(offsetX, 2)); // z changed;
			boolean old_stat = validate;
			validate = (-offsetZ > Max_Depth) ? false : true;
//			if (old_stat != validate) {
//				if (validate) {
//					mTextureHandle = TextureHelper.loadTexture(mActivityContext, mBitmap);
//				} else  {
//					TextureHelper.deleteTexture(mActivityContext, mTextureHandle);
//					mTextureHandle = -1;
//				}
//			}
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
			}
			return true;
		}

		@Override
		public String toString() {
			return "ox: " + x + " ostx: " + offsetX + " ostz: " + offsetZ + " th: " + mTextureHandle;
		}

	}
	
	private Item[] items;

}
