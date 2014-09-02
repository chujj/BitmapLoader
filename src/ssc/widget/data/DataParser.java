package ssc.widget.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataParser {
	public static final String BOARD_ENTRY_KEY = "boards";
	
	public DataParser() {

	}

	public UserDataGroup parser(File file) {
		final int buff_size = 512;
		char[] buff = new char[buff_size];
		StringBuilder sb = new StringBuilder();
		sb.setLength(0);

		try {
			FileReader fr = new FileReader(file);

			int length_read = 0;
			while((length_read = fr.read(buff, 0, buff_size)) != -1) {
				sb.append(buff, 0, length_read);
			}

			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (sb.length() == 0) {
			return null;
		} else {
			return parser(sb.toString());
		}
	}
	
	public UserDataGroup parser(String aData) {
		HBoard[] boardArray = new HBoard[0];
		JSONObject entry = null;
		String commond_description = null;
		try {
			entry = new JSONObject(aData);
			Object boards_object = entry.get(BOARD_ENTRY_KEY);
			if (boards_object == null || !(boards_object instanceof JSONArray)) {
				return new UserDataGroup(boardArray, entry, commond_description);
			}
			
			JSONArray boards = (JSONArray) boards_object;
			ArrayList<HBoard> lists = new ArrayList<HBoard>();
			for (int i = 0; i < boards.length(); i++) {
				JSONObject boardObj = boards.getJSONObject(i);
				HBoard board = new HBoard(boardObj);
				if (board._title.equalsIgnoreCase("commands")) {
					commond_description = board._descript;
				} else {
					lists.add(board);
				}
			}

			boardArray = lists.toArray(boardArray);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UserDataGroup(boardArray, entry, commond_description);
	}
}
