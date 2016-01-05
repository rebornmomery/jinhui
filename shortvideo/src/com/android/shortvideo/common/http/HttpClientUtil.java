package com.android.shortvideo.common.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.shortvideo.common.utils.AppInfoUtil;
import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.common.utils.LoginHelper;
import com.android.shortvideo.common.utils.Md5Util;

/**
 * 网络请求的工具类
 */
@SuppressLint("NewApi")
public class HttpClientUtil {

	private static final String TAG = HttpClientUtil.class.getSimpleName();
	

	/**
	 * 获取公共参数
	 * @param context
	 * @return
	 */
	public static String getCommonParam(Context context) {
		
		String 	version	 	= AppInfoUtil.getVersion(context);
		int 	versionCode	= AppInfoUtil.getVersionCode(context);
		String 	imei		= DeviceUtil.getIMEI(context);
		String 	os			= "android";
		String  sysVersion  = ""; // ios才用

		StringBuilder sb = new StringBuilder();
//		sb.append(      "productID="	+ productID);
		sb.append("&" + "version="		+ version);
		sb.append("&" + "versionCode="	+ versionCode);
//		sb.append("&" + "channelID="	+ channelID);
		sb.append("&" + "imei="			+ imei);
		sb.append("&" + "os="			+ os);
		sb.append("&" + "sysVersion="	+ sysVersion);
		
		if (LoginHelper.getInstance().isLogined()) {
			String 	userID		 = LoginHelper.getInstance().getUserInfo().userID; 
			String  sessionID    = LoginHelper.getInstance().getSessionID();
			String  sessionToken = LoginHelper.getInstance().getSessionToken();
			
			sb.append("&" + "userID="	    + userID);
			sb.append("&" + "sessionID="    + sessionID);
			sb.append("&" + "sessionToken=" + sessionToken);
		}

		return sb.toString();
		
	}
	
	
	/**
	 * 针对POST请求，往Json对象中填入通用参数
	 * @param jsonObject
	 */
	public static void fillCommonParam(Context context, JSONObject jObj){
		
		if (jObj == null) {
			return;
		}
		
		try {
//			String 	productID   = AppInfoUtil.getProductID(context);
			String 	version	 	= AppInfoUtil.getVersion(context);
			int 	versionCode	= AppInfoUtil.getVersionCode(context);
//			String 	channelID	= AppInfoUtil.getChannelID(context);
			String 	imei		= DeviceUtil.getIMEI(context);
			String 	os			= "android";
			String  sysVersion  = ""; // ios才用
	
//			jObj.put("productID", 	productID);
			jObj.put("version", 	version);
			jObj.put("versionCode", versionCode);
//			jObj.put("channelID", 	channelID);
			jObj.put("imei", 		imei);
			jObj.put("os", 			os);
			jObj.put("sysVersion", 	sysVersion);
			
			//如果登陆成功，填入用户信息
			if (LoginHelper.getInstance().isLogined()) {
				String 	userID		 = LoginHelper.getInstance().getUserInfo().userID; 
	            String  sessionID    = LoginHelper.getInstance().getSessionID();
	            String  sessionToken = LoginHelper.getInstance().getSessionToken();
	            
				jObj.put("userID", 		 userID);
				jObj.put("sessionID", 	 sessionID);
				jObj.put("sessionToken", sessionToken);
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 对URL添加校验码
	 * 假设，添加校验参数前的url为http://domain?aa=A&bb=BB&cc=CC
	 * 那么，添加校验参数后的url为http://domain?aa=A&bb=BB&cc=CC&key=KEY
	 * 其中，KEY等于GET_KEY(aa=A&bb=BB&cc=CC)
	 * GET_KEY(param) = MD5(MD5(param) + MD5(“joyodream_123456”))
	 * 
	 * PS：MD5统一返回大写
	 */
	public static String signUrl(String url) {
		if ((url == null) || (url.isEmpty())) {
			return url;
		}
		
		if (url.indexOf("?") == -1){
			return url;
		}
		
		// 提取参数部分
		String param = url.substring(url.indexOf("?") + 1);
		if (param.isEmpty()) {
			return url;
		}
		
		String key = getSignKey(param);
		
		String newUrl = url + "&" + "key=" + key;
		return newUrl;
	}
	
	/**
	 * 对字符串进行签名
	 * PS：注意传入的参数不要为空，否则会返回null
	 * 
	 * @param param
	 * @return 
	 */
	public static String getSignKey(String param)
	{
		if (param == null || param.isEmpty()) {
			return null;
		}
		
		final String SALT = "joyodream_123456";
		
		// 计算key
		String s1  = Md5Util.md5(param);
		String s2  = Md5Util.md5(SALT);
		String signKey = Md5Util.md5(s1 + s2);
		
		return signKey;
	}
	

	
}
