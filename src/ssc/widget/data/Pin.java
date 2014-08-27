package ssc.widget.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Pin {
	private final static String KEY_PIN_ID = "pin_id";
	private final static String KEY_FILE = "file";
	private final static String KEY_RAW_TEXT = "raw_text";

	
	public Pin(JSONObject jsonobject) throws JSONException {
		_id = jsonobject.getLong(KEY_PIN_ID);
		_img = new ImageFile(jsonobject.getJSONObject(KEY_FILE));
		_raw_text = jsonobject.getString(KEY_RAW_TEXT);
	}
	
	public long _id;
	public ImageFile _img;
	public String _raw_text;
}
