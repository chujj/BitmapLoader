package com.example.glhorizontalview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Tile {
		protected FloatBuffer mCoordinates;
		protected boolean inUse , dataLoaded;

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
						cubeTextureCoordinateData.length * MyRenderer.BytesPerFloat)
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
