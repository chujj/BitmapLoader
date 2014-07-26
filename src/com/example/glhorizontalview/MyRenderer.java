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
				-PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -3.0f, 
				-PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -3.0f, 
				PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -3.0f,
				-PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -3.0f, 
				PLAN_HALF_WIDTH_FIXED, -PLAN_HEIGHT_MAXIMIN, -3.0f, 
				PLAN_HALF_WIDTH_FIXED, PLAN_HEIGHT_MAXIMIN, -3.0f, };

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
			if ( !(items[i].validate)) continue;

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, items[i].mTextureHandle);
			GLES20.glUniform1i(mTextureUniformHandle, 0);
			
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, items[i].offsetX, 0.0f, items[i].offsetZ);
			// Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f, 0.0f);
			
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
	}

	
////////////////////////////////animation part ////////////////////////////////
	private final static int animation_part = 1;
	
	private final static float PLAN_HEIGHT_MAXIMIN = 1.0f;
	private final static float PLAN_HALF_WIDTH_FIXED = 1.0f; // y = k*x^2
	private final static float PLAN_NEARLEST_GAP = 0.4f;
	private final static float PLAN_NEARLEST_GAP_DEPTH = 0.3f; // -z toward
	private final static float Distance = (float) (PLAN_HALF_WIDTH_FIXED * 2 + PLAN_NEARLEST_GAP );
	private final static float K = (float) ( PLAN_NEARLEST_GAP_DEPTH / (
			Distance * Distance / 4));
	private final static float Max_Depth = (float) (K * Math.pow((Distance * 2), 2));
	
	private final static float NEAR = 1.5f;
	private final static float FAR = Max_Depth + 5;
	
	private float screen_height, screen_width;
	private final static float OVERSCROLL_WIDTH = 100;
	private int mItemCount = 1;
	private float mCurrOffset;

	protected IEvent eventHandler = new IEvent() {

		@Override
		public void onScroll(float offset_x, float offset_y) {
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
			
		}

	};

	private void initDimensionLimit() {
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
		
	}
	
	
	private void updateItems(float offset_x, float offset_y) {
		mCurrOffset += offset_x;
		mCurrOffset = Math.min(0, mCurrOffset);
		mCurrOffset = Math.max( -( items.length - 1 ) * Distance, mCurrOffset);
		for (int i = 0; i < items.length; i++) {
			items[i].calcOffset(mCurrOffset);
//			DsLog.e("item " + i + ":" + items[i].toString());
		}
	}

	
	private class Item {
		private float x, offsetX, offsetZ;
		private boolean validate;
		private Bitmap mBitmap;
		private int mTextureHandle;
		
		public Item(Bitmap bitmap, float  ax) {
			mBitmap = bitmap;
			mTextureHandle = TextureHelper.loadTexture(mActivityContext, bitmap);
			validate = false;
			x = ax;
			offsetX = -1;
		}
		
		public void calcOffset(float offset_x) {
			if (offset_x == offsetX) return;
			
			offsetX = offset_x + x; // x changed
			offsetZ = (float) (-K * Math.pow(offsetX, 2)); // z changed;
			validate = (-offsetZ > Max_Depth) ? false : true; 
		}

		@Override
		public String toString() {
			return "ox: " + x + " ostx: " + offsetX + " ostz: " + offsetZ;
		}

	}
	
	private Item[] items;

}
