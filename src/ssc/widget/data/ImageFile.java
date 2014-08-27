package ssc.widget.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageFile {
	public final static String KEY_KEY = "key";
	public final static String KEY_TYPE = "type";
	public final static String KEY_WIDTH  = "width";
	public final static String KEY_HEIGHT = "height";
	
	
	public ImageFile(JSONObject jsonobject) throws JSONException {
		_width = jsonobject.getInt(KEY_WIDTH);
		_height = jsonobject.getInt(KEY_HEIGHT);
		_key = jsonobject.getString(KEY_KEY);
		_type = jsonobject.getString(KEY_TYPE);
	}
	
	public final int _width, _height;
	public final String _key, _type;
}
