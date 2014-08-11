package com.example.glhorizontalview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Tile {
		protected FloatBuffer mCoordinates;
		protected boolean inUse , dataLoaded;
		protected int row_idx, column_idx;

		public Tile(int i, int length, int interater) {
			float step = 1.0f / interater;
			row_idx = i / interater;
			column_idx = i % interater;
			
			float x_start =  column_idx * step;
			float x_end =  x_start + step;
			float y_start = row_idx * step;
			float y_end = y_start + step;
			
			float[] cubeTextureCoordinateData = {
					// Front face
					x_start, y_start, 
					x_start, y_end, 
					x_end, y_start, 
					x_start, y_end, 
					x_end, y_end,
					x_end, y_start, };
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
