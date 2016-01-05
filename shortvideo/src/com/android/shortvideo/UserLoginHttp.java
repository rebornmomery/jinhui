/**
 * 
 */
package com.android.shortvideo;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.shortvideo.common.http.AbstractHttpClient;
import com.android.shortvideo.common.http.BaseRequestParam;
import com.android.shortvideo.common.http.BaseResponseResultData;
import com.android.shortvideo.common.http.HttpClientUtil;
import com.android.shortvideo.common.http.JDHttpClientParam;
import com.android.shortvideo.common.utils.UserInfo;

/**
 * @author YeGuangRong
 * 
 *         用户注册登录http
 */
public class UserLoginHttp extends AbstractHttpClient<UserLoginHttp.RequestParam, UserLoginHttp.Data> {
	/**
	 * 参数结构
	 */
	public static class RequestParam extends BaseRequestParam {
		// 昵称
		public String nickname;

		// 真名
		public String realname;

		// 密码
		public String pwd;

		// 登录或注册
		public String Tag;
	}

	public static class Data extends BaseResponseResultData {

		// 用户ID
//		public String aid;
		//用户信息
		public UserInfo userInfo = new UserInfo();
		// 响应
		public String msg;

	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public JDHttpClientParam convRequestParam(RequestParam param) {
		// TODO Auto-generated method stub
		// 获取URL
		StringBuilder sb = new StringBuilder();
		//拿域名的方法
//		sb.append(HttpClientUtil.getMainDomain());
		sb.append("/user/");
		sb.append(param.Tag);
//		sb.append("?");
//		sb.append("nickname=").append(param.nickname);
//
//		sb.append("&pswd=").append(param.pwd);
//		if (param.Tag.equals("registerAPI")) {
//			sb.append("&realname=").append(param.realname);
//		}

		// 签名URL
		String url = sb.toString();

		JDHttpClientParam clientParam = new JDHttpClientParam();
		clientParam.method = JDHttpClientParam.METHOD_POST;
		clientParam.url = url;
		String temp;
		if(param.Tag.equals("registerAPI")){
			temp = "nickname="+param.nickname+"&password="+param.pwd+
					"&realname="+param.realname;
		}
		else{
			temp = "nickname="+param.nickname+"&password="+param.pwd;
		}
		
		clientParam.content = temp;
		Log.e(TAG, "response : " + url+"?"+clientParam.content);
		return clientParam;
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public Data convResponseResult(String jsonString) throws JSONException {
		// TODO Auto-generated method stub
		Data data = new Data();
		JSONObject jsonObject = new JSONObject(jsonString);
		data.userInfo.userID = jsonObject.optString("aid");
		return data;
	}

}
