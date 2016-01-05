package com.android.shortvideo.common.http;

import android.os.Handler;
import android.util.Log;

import com.android.shortvideo.Env;
import com.android.shortvideo.common.utils.LoginHelper;
import com.android.shortvideo.common.utils.ProcessUtil;


/**
 * 用于检测session的非法状态，并做相应的通知处理
 * @author wuwenhua
 *
 */
public class HttpSessionUtil {

	private static final String TAG = HttpSessionUtil.class.getSimpleName();
	
	/**
	 * 登录态session出错的码
	 */
	private static final int SESSION_ERROR_RTN = 9999;

	
	/**
	 * 登录态session的状态码
	 * 	0: session正常
	 * 	1: 已经在另一台设备登录
	 * 	2: 非法的session值
	 * 	3: session过期
	 * 	4: session服务器异常
	 */
	private static final int SESSION_STATUS_OK 		 = 0;
	private static final int SESSION_STATUS_KICKED   = 1;
	private static final int SESSION_STATUS_INVALID  = 2;
	private static final int SESSION_STATUS_EXPIRED  = 3;
	private static final int SESSION_STATUS_UNKOWN   = 4;
	
	
	/**
	 * 检测session的状态
	 * @param rtn
	 * @param sessionStatus
	 * @return 0     session 正确
	 * 			其他  session 错误
	 */
	public static int checkSession(int rtn, int sessionStatus){
/*	    // 只对前台程序检查session
	    if (Env.getContext() == null || !ProcessUtil.getProcessName(Env.getContext()).equalsIgnoreCase(BrotherApplication.PROCESS_NAME_MAIN)) {
            return -1;
        }
		*/
		if ((rtn == SESSION_ERROR_RTN) && (sessionStatus != SESSION_STATUS_OK)) {
//			
//			// 被踢的情况
//			if (sessionStatus == SESSION_STATUS_KICKED) {
//				JDLog.log(TAG, "checkSession. session is kicked!");
//				notifyUserKicked();
//			} 
//			// 其他情况均视作session过期来处理
//			else {
			Log.d(TAG, "checkSession. session is expired not error!");
				notifyUserSessionExpired();			
//			}
		}
		
		Log.d(TAG, "checkSession. session is ok!");
		return 0;
		
	}
	
	
	
//	/**
//	 * 通知session被踢
//	 */
//	private static void notifyUserKicked(){
//		
//		Handler handler = new Handler(Env.getContext().getMainLooper());
//		Runnable runable = new Runnable() {
//			
//			@Override
//			public void run() {
//				LoginHelper.getInstance().notifyUserKicked(true);
//			}
//		};
//		handler.postDelayed(runable, 100); // 滞后执行，免得打断之前的数据处理流程
//	}
	
	
	/**
	 * 通知session过期
	 */
	private static void notifyUserSessionExpired(){
		
		Handler handler = new Handler(Env.getContext().getMainLooper());
		Runnable runable = new Runnable() {
			
			@Override
			public void run() {
				LoginHelper.getInstance().notifyUserSessionExpired(true);
			}
		};
		handler.postDelayed(runable, 100); // 滞后执行，免得打断之前的数据处理流程
	}
	
}
