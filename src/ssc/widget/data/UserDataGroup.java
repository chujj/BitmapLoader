package ssc.widget.data;

import java.io.File;

public class UserDataGroup {

	HBoard[] mHBoard;
	
	public UserDataGroup(HBoard[] boardArray) {
		mHBoard = boardArray;
	}
	
	public boolean newerThan(UserDataGroup current) {
		return true; // ZHUJJ hardcode here first
	}
	
	public void mergeNetData(UserDataGroup net_result) {
		mHBoard = net_result.mHBoard;
		
	}
	public void writeToFile(File cache) {
		
		
	}

}
