package ssc.widget.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HBoard {
	
	public final static String KEY_B_ID = "board_id";
	public final static String KEY_TITLE = "title";
	public final static String KEY_DESCRIPTION = "description";
	public final static String KEY_PIN_COUNT = "pin_count";
	public final static String KEY_PINS ="pins";
	public final static String KEY_UPDATE_TIME = "updated_at";
	
	public final static String KEY_COVER = "cover";

	private JSONObject mJsonObject;
	private Cover mCover;
	public ImageFile _cover_image;
	private HPin[] mPins;
	
	public HBoard(JSONObject jsonObject) throws JSONException {
		mJsonObject = jsonObject;
		if (mJsonObject.has(KEY_COVER)) {
			mCover = new Cover(mJsonObject.getJSONObject(KEY_COVER));
		}
		

		_id = jsonObject.getLong(KEY_B_ID);
		_title = jsonObject.getString(KEY_TITLE);
		_descript = jsonObject.getString(KEY_DESCRIPTION);
		_pin_count = jsonObject.getInt(KEY_PIN_COUNT);
		_update_timestamp = jsonObject.getLong(KEY_UPDATE_TIME);
		
		loadPins(jsonObject);
	}

	private void loadPins(JSONObject jsonObject) throws JSONException {
		JSONArray pins = jsonObject.getJSONArray(KEY_PINS);
		mPins = new HPin[pins.length()];
		for (int i = 0; i < mPins.length; i++) {
			mPins[i] = new HPin(pins.getJSONObject(i));
		}
	}

	public Cover getCover() {
		return mCover;
	}

	public class Cover {
		public final static String KEY_FILE = "file";
		public final static String KEY_RAW_TEXT = "raw_text";
		public Cover (JSONObject jsonObject) throws JSONException {
			if (jsonObject.has(KEY_FILE)) {
				_cover_image = new ImageFile(jsonObject.getJSONObject(KEY_FILE));
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(_id);
		sb.append(" | ");
		sb.append(_title);
		sb.append(" | ");
		sb.append(_descript);
		sb.append(" | ");
		sb.append(_pin_count);
		if (_cover_image != null) {
			sb.append(" | ");
			sb.append(_cover_image._key);
			sb.append(" | ");
			sb.append(_cover_image._width + "," + _cover_image._height);
		}
		return sb.toString();
	}

	final public long _id;
	final public String _title;
	final public String _descript;
	final public int _pin_count;
	final public long _update_timestamp;
	
//	private final static String QUERY_URL_1 = "http://api.huaban.com/boards/17227439/pins?limit=6&wfl=1";
	private final static String QUERY_URL = "http://api.huaban.com/boards/";
	public void loadOlderPins(long afterWhichPin) {
		String afterurl = QUERY_URL + _id + "/pins?limit=20&wfl=1&max=" + afterWhichPin;
		// ZHUJJ implement
	}
	
	public void loadNewPins(long beforeWhichPin) {
		String beforeurl = QUERY_URL + _id + "/pins?limit=20&wfl=1&min=" + beforeWhichPin;
		// ZHUJJ implement
	}
}
