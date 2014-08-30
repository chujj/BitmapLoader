package ssc.widget.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.ds.bitmaputils.BitmapTask;
import com.jakewharton.disklrucache.DiskLruCache.Editor;
import com.jakewharton.disklrucache.DiskLruCache.Snapshot;

public class ImageFile implements BitmapTask {
	public final static String KEY_KEY = "key";
	public final static String KEY_TYPE = "type";
	public final static String KEY_WIDTH  = "width";
	public final static String KEY_HEIGHT = "height";
	
	
	public ImageFile(JSONObject jsonobject) throws JSONException {
		_width = jsonobject.getInt(KEY_WIDTH);
		_height = jsonobject.getInt(KEY_HEIGHT);
		_key = jsonobject.getString(KEY_KEY);
		_type = jsonobject.getString(KEY_TYPE);

		cache_key = _key.toLowerCase();
		remote_query_url = "http://img.hb.aicdn.com/" + _key;
	}
	
	public final int _width, _height;
	public final String _key, _type;
	
	public String cache_key;
	public String remote_query_url;

	//////////////////////////////// BitmapTask ////////////////////////////////

	@Override
	public void saveNetUrl(String aUrl) {
		int a =  1 / 0;
	}

	@Override
	public void saveFileSystemPath(String aPath) {

	}

	@Override
	public Object getTaskKey() {
		return _key;
	}

	@Override
	public String getNetUrl() {
		return remote_query_url;
	}

	@Override
	public boolean useLocalStreamCache() {
		return true;
	}

	@Override
	public Bitmap decodeFromLocalStream() {
		Bitmap retval = null;
		try {
			Snapshot snapshot = UserDataManager.getInstance().mCache.get(cache_key);
			if (snapshot == null || snapshot.getLength(0) == 0) {

			} else {
				InputStream is = snapshot.getInputStream(0);
				retval = BitmapFactory.decodeStream(is);
				snapshot.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

//		if (retval != null) {
//			DsLog.e("decode success:" + _key);
//		} else {
//			DsLog.e("decode fail:" + _key);
//		}
		return retval;
	}

	@Override
	public boolean saveBitmapByTaskself() {
		return true;
	}

	@Override
	public void saveBitmap(Bitmap bitmap) {
		try {
			Editor editor = UserDataManager.getInstance().mCache.edit(cache_key);
			OutputStream os = editor.newOutputStream(0);
			bitmap.compress(CompressFormat.JPEG, 100, os);
			os.flush();
			os.close();
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getFileSystemPath() {
		return null;
	}

	@Override
	public int getBitmapMaxWidth() {
		return 0;
	}

	@Override
	public int getBitmapMaxHeight() {
		return 0;
	}

}
