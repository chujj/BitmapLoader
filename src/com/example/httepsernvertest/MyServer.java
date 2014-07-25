package com.example.httepsernvertest;

import java.io.IOException;
import java.text.MessageFormat;

import com.example.httepsernvertest.MyServer.Responce;

import android.util.TimeUtils;

import fi.iki.elonen.NanoHTTPD;

public class MyServer extends NanoHTTPD {

	
	private StringBuilder mSb;
	private Responce mReponse;

	public MyServer(Responce reponse) throws IOException {
		super(8080
				//, new File("/sdcard/widget")
				);
		
		mSb = new StringBuilder();
		mReponse = reponse; 
	}

	@Override
	public Response serve(IHTTPSession session) {
		if (mSb.length() > 0) {
			mSb.delete(0, mSb.length());
		}
		
		mSb.append(MessageFormat.format("{0,date,yyyy-MM-dd-HH-mm:ss:ms} \n" ,
                                    new Object[]       {
                                        new java.sql.Date(System.currentTimeMillis())
                                    }));
		if (mReponse != null) {
			mSb.append(mReponse.getString());
		} else {
			mSb.append("A test page from bitmaputils");
		}
		return new Response(Response.Status.OK, MIME_PLAINTEXT, mSb.toString());
	}

	public interface Responce {
		public String getString();
	}
}