package com.android.shortvideo.common.http;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.shortvideo.common.utils.GZipUtil;


/**
 * 简单实现http请求协议，支持GET、POST
 * PS：
 * 	1、需要在主线程创建JDHttpClient
 * 	2、回调方法均在主线程执行
 *  3、每次新的查询都创建一个新的JDHttpClient对象
 *  
 *  PS：后续优化，可以加入线程池管理
 */
public class JDHttpClient {

	
	private static final String TAG = JDHttpClient.class.getSimpleName();
	

	/**
	 * 查询参数
	 */
	private JDHttpClientParam mParam = null;
	
	/**
	 * 回调接口
	 */
	private JDHttpClientListener mListener = null;

	
	/**
	 * HTTP连接返回的respondCode，200代表连接成功
	 */
	private int mResponseCode = 0;
	
	/**
	 * 返回成功的标志
	 */
	
	public static String SUCCESS = "success";
	
	/**
	 * 服务器返回结果
	 */
	private String mResponseContent = "";
	
	/**
	 * 查询线程
	 */
	private RequestThread mRequestThread = null;
	
	
	/**
	 * 执行查询，回调在主线程执行
	 */
	public void request(JDHttpClientParam param, JDHttpClientListener listener) {
		mParam = param;
		mListener = listener;
		int rtn = start();
		
		// 针对参数有误的情况，执行错误回调
//		if (rtn != 0) {
//			mResponseCode = rtn;
//			Message msg = mReportHandler.obtainMessage();
//			msg.sendToTarget();
//		}
	}

	
	/**
	 * 启动查询线程
	 */
	public int start(){
		
		// 判断参数有效性
//		if ((mParam.url == null) || (mParam.url.isEmpty())){
//			return -1;
//		
//		} else if ((mParam.method == JDHttpClientParam.METHOD_POST) && (mParam.content == null)) {
//			return -2;
//		}
//		
//		// 判断线程是否正在运行
//		if ((mRequestThread != null) && (mRequestThread.isAlive())){
//			return -3;
//		}
		
		// 启动查询线程
		mRequestThread = new RequestThread();
		mRequestThread.start();	
		return 0;
	}
	
	
	/**
	 * 查询线程
	 */		
	private class RequestThread extends Thread{

		/**
		 * 线程运行函数
		 */
		@Override
		public void run() {
            try 
            {
            	// 设置请求属性
	            URL url = new URL(mParam.url);
	            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	            conn.setConnectTimeout(60*1000);// 请求超时
            	conn.setDoInput(true);
	            conn.setUseCaches(false);
            	if (mParam.acceptGzip) {
            		conn.setRequestProperty("Accept-Encoding", "gzip");
            	}
            	
	            // GET方式请求
	            if (mParam.method == JDHttpClientParam.METHOD_GET){
	            	
	            	conn.setReadTimeout(60*1000);
	            }
	            // POST方式请求
	            else {
	            	conn.setRequestMethod("POST");
	            	conn.setDoOutput(true);
	            	conn.setReadTimeout(60*1000);
	            	conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	            	conn.setRequestProperty("User-Agent", "Mozilla/4.0");
	            	//conn.setRequestProperty("connection", "keep-alive");

	            	// 非压缩方式
	            	if (mParam.contentGzip == false) {
	            		
	            	    // 获取字节数组
	            	    byte[] data =mParam.content.getBytes();
	            	    
	            		// 设置连接属性
	            		conn.setRequestProperty("Content-Length", data.length + "");
			            
	            		// POST数据
	            		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			            dos.write(data, 0, data.length);
			            dos.flush();
			            dos.close();
	            	}
	            	// 压缩方式
	            	else {

	            		// 压缩数据
	            		byte[] gzipBytes = GZipUtil.gzipStringToByte(mParam.content);
	            				
	            		// 设置连接属性
			            conn.setRequestProperty("Content-Encoding","gzip");
			            conn.setRequestProperty("Content-Length", gzipBytes.length + "");			            

			            // POST数据
			            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			            dos.write(gzipBytes, 0, gzipBytes.length);
			            dos.flush();
			            dos.close();
	            	}	
	            }
	            
	            // 获取结果码
	            mResponseCode = conn.getResponseCode();

	            // 获取响应结果
	            // 200 - 正常查询
	            // 500 - 服务器异常，也把数据提出来方便定位
	            if ((mResponseCode == 200) || (mResponseCode == 500)){
	            	
	            	// 读取返回内容
	                InputStream is = conn.getInputStream();

	                // 如果需要则解压
	                String header = conn.getHeaderField("Content-Encoding");
	                if (header != null && header.toLowerCase().indexOf("gzip") > -1) {
	                	is = new GZIPInputStream(is);
	                }
                
	                // 字符串格式返回结果
	                mResponseContent = JDHttpClientTool.convertStreamToString(is);
	                is.close();
	            }
	            // 服务器返回错误
	            else {
	            	// ...
	            }
	            
	            // 关闭连接
	            conn.disconnect();
            }
            // 连接异常
            catch (Exception e){
                // 存在跑出e为null的情况，具体原因未知
            	if (e != null) {
            	    e.printStackTrace();
                }
            	// 三目运算符的优先级低于 ‘+’，这里需要加上括号
            	Log.d(TAG, "Exception : " + ( e == null ? "null" : e.getMessage() ));
                mResponseCode = -1;
            }
            Log.e("sunThreadId", "id = "+Thread.currentThread().getId());
            // 切换到主线程返回结果
			Message msg = mReportHandler.obtainMessage();
			msg.sendToTarget();
		}
	} 
	
	
//	/**
//	 * 处理查询结果的handler（主线程）
//	 */
	//Env.getContext().getMainLooper()
	
	private Handler mReportHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.e("handlerThreadId", "id = "+Thread.currentThread().getId());
			if (mListener != null) {
			    // 打日志
				Log.d(TAG, String.format("requestUrl = %s", mParam.url));
			    if (mParam.method == JDHttpClientParam.METHOD_POST) {
			    	Log.d(TAG, String.format("postContent = %s", mParam.content));
			    }
			    
				// 成功
				if (mResponseCode == 200) {
					Log.d(TAG, String.format("onSuccess, responseCode = %d", mResponseCode));
					Log.d(TAG, String.format("onSuccess, responseContent = %s", mResponseContent));				    
					mListener.onSuccess(mResponseCode, mResponseContent);
				}
				// 失败
				else {
					Log.d(TAG, String.format("onFailed, responseCode = %d", mResponseCode));
					Log.d(TAG, String.format("onFailed, responseContent = %s", mResponseContent));
					mListener.onFailed(mResponseCode);
				}
				
			}
			
		}
	};
//	
	
	
	
}
