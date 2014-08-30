package ssc.widget.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ds.io.DsLog;

public class UserDataGroup {

	HBoard[] mHBoard;
	JSONObject mEntry;
	boolean isSthMerged;
	
	public UserDataGroup(HBoard[] boardArray, JSONObject entry) {
		mHBoard = boardArray;
		mEntry = entry;
		isSthMerged = false;
	}

	/** merge src into dst. If successed set dst's merge_flag
	 * @param src
	 * @param dst
	 * @return
	 */
	public static UserDataGroup mergeNetData(UserDataGroup src, UserDataGroup dst) { // ZHUJJ implement
		if (dst == null) {
			dst = src;
			dst.isSthMerged = true;
		} else if (src == null) {
			
		} else {
			dst.merge(src);
		}
		return dst;
	}
	
	private void merge(UserDataGroup src) {
		// normals merge
		if (mHBoard == null || mEntry == null) {
			mHBoard = src.mHBoard;
			mEntry = src.mEntry;
			isSthMerged = true;
			return;
		} else if (src.mHBoard == null || src.mEntry == null) {
			return;
		}
		
		// compare merge: compare board's last_update_timestamp, directly switch to newest
		boolean merged = false;
		HBoard[] lsrc = src.mHBoard;
		HBoard[] ldst = mHBoard;
		HBoard[] brave_new_list = new HBoard[Math.max(lsrc.length, ldst.length)];
		int brave_new_list_append = 0;

		for (int i = 0; i < lsrc.length; i++) {
			boolean exist = false;
			for (int j = 0; j < ldst.length; j++) {
				if (ldst[j]._id == lsrc[i]._id) { // exist 
					exist = true;
					if (ldst[j]._update_timestamp < lsrc[i]._update_timestamp) {
						ldst[j] = lsrc[i];
						merged = true;
					}
					break;
				}
			}
			if (!exist) { // remeber in cache list
				brave_new_list[brave_new_list_append++] = lsrc[i];
				merged = true;
			}
		}
		
		// merge to list
		if (brave_new_list_append != 0) {
			HBoard[] newList = new HBoard[ldst.length + brave_new_list_append];
			System.arraycopy(ldst, 0, newList, 0, ldst.length);
			System.arraycopy(brave_new_list, 0, newList,  ldst.length, brave_new_list_append);
			ldst = newList;
		}
		
		// finally clean up
		if (merged) {
			mHBoard = ldst;
			mEntry = src.mEntry;
			isSthMerged = true;
		}
	}

	public void writeToFile(File cache) { // ZHUJJ implement
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(cache);
			fos.write(mEntry.toString().getBytes());
			fos.flush();
			fos.close();
			isSthMerged = false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
//	final static String boadrs_ping_url = "http://api.huaban.com/boards/17227439/pins?limit=6&wfl=1";
//	http://api.huaban.com/boards/17227439/pins?wfl=1
	private static String buildQueryPinsUrl(long boardId) {
		String retval = "http://api.huaban.com/boards/";
		retval += boardId;
		retval += "/pins?wfl=1";
		return retval;
	}
	
	public void queryAllPinsInBoards() {
		for (int i = 0; i < mHBoard.length; i++) {
			HBoard board = mHBoard[i];
			if (board._pin_count == 0 || (board.mPins.length >= board._pin_count)) {
				continue;
			}
			
			HPin[] pins = null;
			String data = UserDataManager.queryNet(buildQueryPinsUrl(board._id));
			JSONObject pinsobj = null;
			try {
				pinsobj = new JSONObject(data);
				pins = HBoard.loadPins(pinsobj);
				if (pins == null || pins.length != board._pin_count) {
					DsLog.e("query pins failed! for board: " + board._id + " get count: " + (pins == null ? "null" : pins.length) + " hope: " + board._pin_count);
					continue;
				}

				board.switchPinsData(pinsobj, pins);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
