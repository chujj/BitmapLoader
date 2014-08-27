package ssc.widget.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class DataParser {
	public static final String BOARD_ENTRY_KEY = "boards";
	
	public DataParser() {

	}

	public UserDataGroup parser(File file) {
		
		String content = null;
		final int buff_size = 512;
		byte[] buff = new byte[buff_size];

		InputStream is;
		try {
			is = new FileInputStream(file);
			int length_read = 0;
			while ((length_read = is.read(buff, 0, buff_size)) != -1) {
				content += new String(buff, 0, length_read);
			}
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (content == null || content.length() == 0) {
			return null;
		} else {
			return parser(content);
		}
	}
	
	public UserDataGroup parser(String aData) {
		HBoard[] boardArray = null;
		try {
			JSONObject entry = new JSONObject(aData);
			Object boards_object = entry.get(BOARD_ENTRY_KEY);
			if (boards_object == null || !(boards_object instanceof JSONArray)) {
				return new UserDataGroup(boardArray);
			}
			
			JSONArray boards = (JSONArray) boards_object;
			int boardCount = boards.length();
			boardArray = new HBoard[boardCount];
			for (int i = 0; i < boardArray.length; i++) {
				boardArray[i] = new HBoard(boards.getJSONObject(i));
				System.out.println(boardArray[i].toString());
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UserDataGroup(boardArray);
	}
}
