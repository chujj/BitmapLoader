package com.ds.ui;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.drawable.NinePatchDrawable;

public class DsBitmapUtil {

	/** return NinePatchDrawable with give id
	 * @param aContext
	 * @param aNinePatchResourceId
	 * @return
	 */
	public static NinePatchDrawable getNinePatchDrawable(Context aContext, int aNinePatchResourceId) {
		Bitmap bg = BitmapFactory.decodeResource(aContext.getResources(),
				aNinePatchResourceId);
		NinePatch np = new NinePatch(bg, bg.getNinePatchChunk(), null);
		NinePatchDrawable t9Patch = new NinePatchDrawable(aContext.getResources(), np);
		return t9Patch;
	}
	
	/** set the matrix accord paramater
	 * @param matrix 
	 * @param bitmap
	 * @param degrees
	 * @param fixedScale if fixedth scale factor of width and height. If true, the finalwidth and final finalheight ignored
	 * @param scaleFac only effect when fixedScale is true
	 * @param finalwidth
	 * @param finalheight
	 * @param centerOffsetX
	 * @param centerOffsetY
	 */
	void calcMatrixWithBitmapLoc(Matrix matrix, Bitmap bitmap,
			float degrees, boolean fixedScale, float scaleFac,
			float finalwidth, float finalheight, float centerOffsetX,
			float centerOffsetY) {
		int _w = bitmap.getWidth();
		int _h = bitmap.getHeight();
		int _h_w = _w / 2;
		int _h_h = _h / 2;

		matrix.reset();
		if (fixedScale) {
			matrix.postScale(scaleFac, scaleFac, _h_w, _h_h);
		} else {
			matrix.postScale(finalwidth / _w, finalheight / _h, _h_w, _h_h);
		}
		matrix.postRotate(degrees, _h_w, _h_h);
		matrix.postTranslate(centerOffsetX - _h_w, centerOffsetY - _h_h);
	}
	
	private static HashMap<String , Bitmap > sCacheMap = new HashMap<String, Bitmap>();

	/** load the Bitmap from assets or sdcard_dir
	 * @param context
	 * @param outsideFileName
	 * @param op
	 * @param useSdcard
	 * @param sdcard_assetdir_path
	 * @return
	 */
	public static Bitmap loadBitmap(Context context, String outsideFileName,
			BitmapFactory.Options op, boolean useSdcard,
			String sdcard_assetdir_path) {
		AssetManager am = context.getAssets();

		Bitmap retval = null;
		synchronized (sCacheMap) {
			if (sCacheMap.get(outsideFileName) != null) {
				retval = sCacheMap.get(outsideFileName);
				return retval;
			}
		}

		try {
			retval = BitmapFactory.decodeStream(am.open(outsideFileName), null, op);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (useSdcard) {
			Bitmap _tmp = BitmapFactory.decodeFile(sdcard_assetdir_path + outsideFileName, op);
			if (_tmp != null) {
				retval = _tmp;
			}
		}

		if (retval != null) {
			synchronized (sCacheMap) {
				sCacheMap.put(outsideFileName, retval);
			}

		}

		return retval;
	}


}
