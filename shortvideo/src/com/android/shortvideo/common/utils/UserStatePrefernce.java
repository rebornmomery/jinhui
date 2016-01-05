package com.android.shortvideo.common.utils;

import android.content.Context;

public class UserStatePrefernce {
	
	private static boolean  Logined;
	private static String UserInfoStr;
	private static String SessionID;
	private static String SessionToken;
	
	
	public static void setLogined(Context context, boolean b) {
		// TODO Auto-generated method stub
		Logined = b ;
	}

	public static void setUserInfoStr(Context context, String string) {
		// TODO Auto-generated method stub
		UserInfoStr = string ;
	}

	public static void setSessionID(Context context, String string) {
		// TODO Auto-generated method stub
		SessionID = string ;
	}
	
	public void setSessionToken(Context context, String string) {
		// TODO Auto-generated method stub
		SessionToken = string ;
	}
	public static boolean getLogined(Context context) {
		// TODO Auto-generated method stub
		return false;
	}

	public static String getUserInfoStr(Context context) {
		// TODO Auto-generated method stub
		return UserInfoStr;
	}

	public static String getSessionID(Context context) {
		// TODO Auto-generated method stub
		return SessionID;
	}

	public static String getSessionToken(Context context) {
		// TODO Auto-generated method stub
		return SessionToken;
	}

}
