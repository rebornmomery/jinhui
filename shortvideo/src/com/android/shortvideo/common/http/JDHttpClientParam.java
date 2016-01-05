package com.android.shortvideo.common.http;



/**
 * JDHttpClient的查询参数
 * 
 * PS：暂时简单实现，只支持entry为String，后续可扩展参数
 * 		Header[] headers;
 * 		String contentType;
 * 		HttpEntity entity;
 * 		String cookie;
 */
public class JDHttpClientParam {

	/**
	 * HTTP查询方式，取值GET、POST
	 */
	public static final int METHOD_NONE = 0;
	public static final int METHOD_GET  = 1;
	public static final int METHOD_POST = 2;
	
	
	/**
	 * 查询方式
	 */
	public int method = METHOD_NONE;
	
	/** 
	 * 请求URL
	 */
	public String url = null;

	/**
	 * POST方式的内容
	 */
	public String content = null;
	
	/**
	 * POST方式的内容是否压缩
	 */
	public boolean contentGzip = false; // 默认压缩 // PS : 强制设置为false，服务器目前解压有问题，客户端上报压缩会解压不了
	
	
	/**
	 * 是否支持服务器返回压缩数据
	 * PS：服务器返回是否压缩，具体按照返回的Content-Encoding而定
	 */
	public boolean acceptGzip = true; // 默认压缩
	
}
