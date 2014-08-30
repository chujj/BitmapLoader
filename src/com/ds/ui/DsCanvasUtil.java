package com.ds.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

/** 提供绘图相关的公共方法 */
public final class DsCanvasUtil {
	
	private static final float DEFUALT_NIGHT_DARK_RATIO = 0.5f;

	private DsCanvasUtil() {

	}

	/**
	 * 计算文字垂直居中时，drawText方法中y值
	 * 
	 * @param aCanvasHeight
	 *            画布高度
	 * @param aPaint
	 *            文字画笔
	 * @return 提供drawText方法中y值
	 */
	public static float calcYWhenTextAlignCenter(int aCanvasHeight, Paint aPaint) {
		if (aPaint == null) {
			return 0;
		}

		final float fontHeight = aPaint.getFontMetrics().bottom - aPaint.getFontMetrics().top;
		return ((aCanvasHeight - fontHeight) / 2 - aPaint.getFontMetrics().top);
	}

	public static RectF getReduceRectF(RectF aRectF, int aReduceSize) {
		RectF rectF = new RectF(aRectF.left + aReduceSize, aRectF.top + aReduceSize, aRectF.right
				- aReduceSize, aRectF.bottom - aReduceSize);
		return rectF;
	}

	/**
	 * 
	 * @param aX
	 *            起始x坐标
	 * @param aY
	 *            起始y坐标
	 * @param aSize
	 *            箭头大小
	 * @return 提供形如 “﹥”的箭头
	 */
	public static Path createRightArrowPath(int aX, int aY, int aWidth, int aHeight) {
		Path path = new Path();

		path.moveTo(aX, aY);
		path.lineTo(aX + aWidth, aY + (aHeight >> 1));
		path.lineTo(aX, aY + aHeight);

		return path;
	}

	/**
	 * 
	 * @param aX
	 *            起始x坐标
	 * @param aY
	 *            起始y坐标
	 * @param aSize
	 *            箭头大小
	 * @return 提供形如 “∨”的箭头
	 */
	public static Path createDownArrowPath(int aX, int aY, int aWidth, int aHeight) {
		Path path = new Path();

		path.moveTo(aX, aY);
		path.lineTo(aX + (aWidth >> 1), aY + aHeight);
		path.lineTo(aX + aWidth, aY);

		return path;
	}

	/**
	 * 
	 * @param aX
	 *            起始x坐标
	 * @param aY
	 *            起始y坐标
	 * @param aWidth
	 *            三角形宽度
	 * @param aHeight
	 *            三角形高度
	 * @return 提供顶角朝上的等腰三角形
	 */
	public static Path createUpTrianglePath(int aX, int aY, int aWidth, int aHeight) {
		Path path = new Path();

		path.moveTo(aX, aY + aHeight);
		path.lineTo(aX + (aWidth >> 1), aY);
		path.lineTo(aX + aWidth, aY + aHeight);
		path.close();

		return path;
	}

	/**
	 * 
	 * @param aX
	 *            起始x坐标
	 * @param aY
	 *            起始y坐标
	 * @param aWidth
	 *            三角形宽度
	 * @param aHeight
	 *            三角形高度
	 * @return 提供顶角朝下的等腰三角形
	 */
	public static Path createDownTrianglePath(int aX, int aY, int aWidth, int aHeight) {
		Path path = new Path();

		path.moveTo(aX, aY);
		path.lineTo(aX + aWidth, aY);
		path.lineTo(aX + (aWidth >> 1), aY + aHeight);
		path.close();

		return path;
	}

	/**
	 * @param aWidth
	 *            整体宽度
	 * @param aHeight
	 *            整体高度
	 * @param aTriangleX
	 *            小三角的位置
	 * @param aTriangleSize
	 *            小三角的尺寸
	 * @param aXPadding
	 *            横向的padding
	 * @param aYPadding
	 *            纵向的padding
	 * @return 提供如下的Path：
	 */
	// ┌────∧────┐
	// └──────────┘
	public static Path createTagPath(int aWidth, int aHeight, int aTriangleX, int aTriangleSize,
			int aXPadding, int aYPadding) {

		return createTagPath(aWidth, aHeight, aTriangleX, aTriangleSize, aXPadding, aYPadding, false);
	}
	
	public static Path createTagPath(int aWidth, int aHeight, int aTriangleX, int aTriangleSize, int aXPadding,
			int aYPadding, boolean aOrientDown) {
		
		Path path = new Path();
		
		float P1x = aXPadding ;
		float P1y = aTriangleSize + aYPadding;
		float P2x =aTriangleX - aTriangleSize;
		float P2y =aTriangleSize + aYPadding  ;
		float P3x = aTriangleX;
		float P3y = aYPadding;
		float P4x = aTriangleX + aTriangleSize;
		float P4y = aTriangleSize + aYPadding;
		float P5x = aWidth - aXPadding;
		float P5y = aTriangleSize + aYPadding;
		float P6x = aWidth - aXPadding;
		float P6y = aHeight - aYPadding;
		float P7x = aXPadding;
		float P7y = aHeight - aYPadding;

		if (aOrientDown) {
			float X = aWidth - aXPadding;
			float Y = aHeight - aYPadding;
			
			P1x = mirror(P1x, X);
			P2x = mirror(P2x, X);
			P3x = mirror(P3x, X);
			P4x = mirror(P4x, X);
			P5x = mirror(P5x, X);
			P6x = mirror(P6x, X);
			P7x = mirror(P7x, X);
			
			P1y = mirror(P1y, Y);
			P2y = mirror(P2y, Y);
			P3y = mirror(P3y, Y);
			P4y = mirror(P4y, Y);
			P5y = mirror(P5y, Y);
			P6y = mirror(P6y, Y);
			P7y = mirror(P7y, Y);
		}
			
		path.moveTo(P1x, P1y);
		path.lineTo(P2x, P2y);
		path.lineTo(P3x, P3y);
		path.lineTo(P4x, P4y);
		path.lineTo(P5x, P5y);
		path.lineTo(P6x, P6y);
		path.lineTo(P7x, P7y);
		path.close();

		return path;
	}
	
	private static float mirror(float point, float mirrorLength) {
		return mirrorLength - (point);
	}


	/**
	 * @param aWidth
	 *            整体宽度
	 * @param aHeight
	 *            整体高度
	 * @param aXPadding
	 *            横向的padding
	 * @param aYPadding
	 *            纵向的padding
	 * @param aCornerSize
	 *            圆角大小
	 * @param isRound1
	 *            1号角是否是圆角
	 * @param isRound2
	 *            2号角是否是圆角
	 * @param isRound3
	 *            3号角是否是圆角
	 * @param isRound4
	 *            4号角是否是圆角
	 * @return 提供如下的圆角矩形Path：
	 */
	// 1┌───────┐2
	//  │       │
	// 4└───────┘3
	public static Path createRoundRectPath(float aWidth, float aHeight, float aXPadding, float aYPadding,
			float aCornerSize, boolean isRound1, boolean isRound2, boolean isRound3, boolean isRound4) {
		return createRoundRectPath(aWidth, aHeight, aXPadding, aXPadding, aYPadding, aYPadding, aCornerSize,
				isRound1, isRound2, isRound3, isRound4);
	}

	/**
	 * @param aWidth
	 *            整体宽度
	 * @param aHeight
	 *            整体高度
	 * @param aPaddingLeft
	 *            左侧的padding
	 * @param aPaddingRight
	 *            右侧的padding
	 * @param aPaddingTop
	 *            上侧的padding
	 * @param aPaddingBottom
	 *            下侧的padding
	 * @param aCornerSize
	 *            圆角大小
	 * @param isRound1
	 *            1号角是否是圆角
	 * @param isRound2
	 *            2号角是否是圆角
	 * @param isRound3
	 *            3号角是否是圆角
	 * @param isRound4
	 *            4号角是否是圆角
	 * @return 提供如下的圆角矩形Path：
	 */
	// 1┌───────┐2
	//  │       │
	// 4└───────┘3
	public static Path createRoundRectPath(float aWidth, float aHeight, float aPaddingLeft,
			float aPaddingRight, float aPaddingTop, float aPaddingBottom, float aCornerSize,
			boolean isRound1, boolean isRound2, boolean isRound3, boolean isRound4) {
		Path path = new Path();
		RectF oval;

		/** 左上角起始 */
		if (isRound1) {
			path.moveTo(aPaddingLeft, aPaddingTop + aCornerSize);
			oval = new RectF(aPaddingLeft, aPaddingTop, aPaddingLeft + aCornerSize * 2, aPaddingTop
					+ aCornerSize * 2);
			path.arcTo(oval, 180, 90);
		} else {
			path.moveTo(aPaddingLeft, aPaddingTop);
		}

		/** 上边框 */
		if (isRound2) {
			path.lineTo(aWidth - aPaddingRight - aCornerSize, aPaddingTop);
			oval = new RectF(aWidth - aPaddingRight - aCornerSize * 2, aPaddingTop, aWidth - aPaddingRight,
					aPaddingTop + aCornerSize * 2);
			path.arcTo(oval, 270, 90);
		} else {
			path.lineTo(aWidth - aPaddingRight, aPaddingTop);
		}

		/** 右边框 */
		if (isRound3) {
			path.lineTo(aWidth - aPaddingRight, aHeight - aPaddingBottom - aCornerSize);
			oval = new RectF(aWidth - aPaddingRight - aCornerSize * 2, aHeight - aPaddingBottom - aCornerSize
					* 2, aWidth - aPaddingRight, aHeight - aPaddingBottom);
			path.arcTo(oval, 0, 90);
		} else {
			path.lineTo(aWidth - aPaddingRight, aHeight - aPaddingBottom);
		}

		/** 下边框 */
		if (isRound4) {
			path.lineTo(aPaddingLeft + aCornerSize, aHeight - aPaddingBottom);
			oval = new RectF(aPaddingLeft, aHeight - aPaddingBottom - aCornerSize * 2, aPaddingLeft
					+ aCornerSize * 2, aHeight - aPaddingBottom);
			path.arcTo(oval, 90, 90);
		} else {
			path.lineTo(aPaddingLeft, aHeight - aPaddingBottom);
		}

		/** 左边框 */
		path.close();

		return path;
	}
	
	/**
	 * 得到一个夜间模式变暗的ColorFilter，一般用于位图的变暗
	 * @return
	 */
	public static ColorMatrixColorFilter createNightColorFilter() {
		return createDarkerColorFilter(DEFUALT_NIGHT_DARK_RATIO);
	}

	/***
	 * 得到一个将颜色变暗的ColorFilter
	 * 
	 * @param aDarkRatio 变暗的系数，0到1之间
	 * @return
	 */
	public static ColorMatrixColorFilter createDarkerColorFilter(float aDarkRatio) {
		final float[] array = new float[] { aDarkRatio, 0, 0, 0, 0, 0, aDarkRatio, 0, 0, 0, 0, 0, aDarkRatio, 0,
				0, 0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static ColorMatrixColorFilter createRGBColorFilter(int aRed, int aGreen, int aBlue) {
		final float[] array = new float[] { 0, 0, 0, 0, aRed, 0, 0, 0, 0, aGreen, 0, 0, 0, 0,
				aBlue, 0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static ColorMatrixColorFilter createRGBColorFilter(int aRed, int aGreen, int aBlue, float aAlpha) {
		final float[] array = new float[] { 0, 0, 0, 0, aRed, 0, 0, 0, 0, aGreen, 0, 0, 0, 0,
				aBlue, 0, 0, 0, aAlpha, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static ColorMatrixColorFilter createColorFilterByColor(int aColor) {
		final int r = (aColor >> 16) & 255;
		final int g = (aColor >> 8) & 255;
		final int b = aColor & 255;
		final float[] array = new float[] { 0, 0, 0, 0, r, 0, 0, 0, 0, g, 0, 0, 0, 0,
				b, 0, 0, 0, 1, 0 };
		return new ColorMatrixColorFilter(array);
	}
	
	public static ColorMatrixColorFilter createColorFilterByColor(int aColor, float aAlpha) {
		final int r = (aColor >> 16) & 255;
		final int g = (aColor >> 8) & 255;
		final int b = aColor & 255;
		final float[] array = new float[] { 0, 0, 0, 0, r, 0, 0, 0, 0, g, 0, 0, 0, 0,
				b, 0, 0, 0, aAlpha, 0 };
		return new ColorMatrixColorFilter(array);
	}
	public static Path createLeftArrowPath(int aX, int aY, int aWidth, int aHeight) {
		Path path = new Path();

		path.moveTo(aX + aWidth, aY);
		path.lineTo(aX, aY + (aHeight >> 1));
		path.lineTo(aX + aWidth, aY + aHeight);

		return path;
	}
	
	public static void drawToCenterOfCanvas(Canvas canvas, Bitmap bp, int max_width, int max_height, Rect cacheRect) {
		final int b_w = bp.getWidth();
		final int b_h = bp.getHeight();
		if (b_w <= max_width && b_h <= max_height) { // center aligned
			canvas.drawBitmap(bp, (max_width - b_w) / 2, (max_height - b_h) /2, null);
		} else { // scaled fix given rect size
			final float s_w = 1.0f * max_width / b_w;
			final float s_h = 1.0f * max_height / b_h;
			final float f_s = Math.min(s_w, s_h);
			final int f_w = (int) (b_w * f_s);
			final int f_h = (int) (b_h * f_s);
			cacheRect.set(0, 0, f_w, f_h);
			cacheRect.offset( (max_width - f_w) / 2, (max_height - f_h) / 2);
			canvas.drawBitmap(bp, null, cacheRect, null);
		}
	}
}
