package com.android.shortvideo.common.http;

/**
 * JDHttpClient的回调接口
 */
public interface JDHttpClientListener {

	/**
	 * 请求成功
	 * @param responseCode		结果码
	 * @param responseContent	结果内容
	 */
	public void onSuccess(int responseCode, String responseContent);

	/**
	 * 请求失败
	 * @param responseCode		结果码
	 */
	public void onFailed(int responseCode);
}
