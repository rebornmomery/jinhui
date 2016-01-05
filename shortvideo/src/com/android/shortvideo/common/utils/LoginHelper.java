package com.android.shortvideo.common.utils;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.shortvideo.Env;
import com.android.shortvideo.common.http.ResponseListener;

/**
 * 登录用户信息的管理类
 */
@SuppressLint("NewApi")
public class LoginHelper {

	
	private static final String TAG = LoginHelper.class.getSimpleName();
	
	/**
	 * 用户登录状态变化回调
	 */
	public static interface LoginStateListener {
	    
	    /**
	     * 登录
	     */
	    public void onUserLogined();
	    
	    /**
	     * 退出
	     */
	    public void onUserLogouted();
	    
	    /**
	     * 被踢下线
	     */
	    public void onUserKicked();
	    
        /**
         * 登录态session过期
         */
        public void onUserSessionExpired();	
        
        
        /**
         * 当前登录用户信息修改
         */
        public void onUserInfoChanged();
        
	}
	
    /** 是否正在登录 */
    private boolean mIsLogined = false;
	
	
	/** 用户信息 */
    private UserInfo mUserInfo = new UserInfo();
    
    
	/** 登录态：sessionID，用作公共参数 */
	public String mSessionID;
	
	
	/** 登录态：sessionToken，用作公共参数 */
    public String mSessionToken;
    
    public static final int FIRST_LOGIN_NO  = 0;
    public static final int FIRST_LOGIN_YES = 1; 
    /** 
     * 表示用户是否已注册登录过（版本1.2添加），0:否，1:是
     * 为了实现自动登录，自动登录时默认不是第一次登录
     */
    public int mFirstLogin = FIRST_LOGIN_NO;
    
    public static final int INTEGRATED_NO   = 0;
    public static final int INTEGRATED_YES  = 1;
    /** 
     * 账号信息是否完整（版本1.2添加），0：不完整，1：完整 
     * 为了实现自动登录，自动登录时默认是完整的
     */
    public int mIsIntegrated = INTEGRATED_YES;
    
    
	/** 回调接口列表 */
	private ArrayList<LoginStateListener> mUserLoginStateListenerList = new ArrayList<LoginStateListener>();
	
	
    /** 实例化 */
    private LoginHelper(){

    }

    /**
     * 获取单例
     */
    private static LoginHelper sInstance = null;
    public static LoginHelper getInstance() {
    	if (sInstance == null) { // 提前预判，减少锁的范围
        	synchronized(TAG){
        		if (sInstance == null) {
        			sInstance = new LoginHelper();
        		}
        	} 
    	}
    	return sInstance;
    }
    

	/**
	 * 判断用户是否已经登录
	 */
	public boolean isLogined(){
		return mIsLogined;
	}
	
	/**
	 * 获取用户信息
	 */
	public UserInfo getUserInfo(){
	    return mUserInfo;
	}
	

	/**
	 * 获取sessionID
	 */
	public String getSessionID(){
	    return mSessionID;
	}
	
	
	/**
	 * 获取token
	 */
	public String getSessionToken(){
	    return mSessionToken;
	}
	
	/**
	 * 账号是否第一次登录
	 * @return
	 */
	public boolean isFirstLogin(){
	   return mFirstLogin == FIRST_LOGIN_YES; 
	}
	
	/**
	 * 账号信息是否完整
	 * @return
	 */
	public boolean isIntegrated(){
	    return mIsIntegrated == INTEGRATED_YES;
	}

	/**
	 * 添加广播监听：登录状态变化
	 */
	public void addUserLoginStateListener(LoginStateListener listener) {
		if (listener == null) {
			return;
		}
		for (LoginStateListener l: mUserLoginStateListenerList) {
			if (l == listener) {
				return;
			}
		}
		mUserLoginStateListenerList.add(listener);
	}
	
	
	/**
	 * 取消广播监听：登录状态变化
	 */
	public void removeUserLoginStateListener(LoginStateListener listener) {
		mUserLoginStateListenerList.remove(listener);
	}
	
	
	/**
	 * 清空用户状态：用户退出或则被踢的时候使用
	 */
	private void clearUserState(){
	    
        // 充值状态
	    mIsLogined 		= false;
	    mUserInfo  		= new UserInfo();
	    mSessionID 		= null;
	    mSessionToken 	= null;
        
        // 保存用信息到cache
	    Context context = Env.getContext();
		UserStatePrefernce.setLogined     (context, false);
		UserStatePrefernce.setUserInfoStr (context, "");
		UserStatePrefernce.setSessionID   (context, "");
	}
	
	/**
	 * 保存用户状态到内存和preference
	 */
	public void saveUserState(UserInfo userInfo, String sessionID, String sessionToken){
		
		// 保存用信息到内存
	    mIsLogined    = true;
		mUserInfo 	  = userInfo;
		mSessionID    = sessionID;
		mSessionToken = sessionToken;
		
		// 获取用户信息字符串
//		String userInfoStr = "";
//		if (userInfo != null) {
//			JSONObject jData = UserInfoUtil.convUserInfoToJson(userInfo);
//			if (jData != null) {
//				userInfoStr = jData.toString();
//			}
//		}
		
		// 保存用必须要的信息到cache
	    Context context = Env.getContext();
		UserStatePrefernce.setLogined     (context, true);
		UserStatePrefernce.setUserInfoStr (context, mUserInfo.userID);
		
//		UserStatePrefernce.setSessionID   (context, sessionID);
//		UserStatePrefernce.setSessionToken(context, sessionToken);
        
		// 打日志
//		Log.d(TAG, "saveUserState, userInfoStr  = "  + userInfoStr);
//		Log.d(TAG, "saveUserState, sessionID    = "    + sessionID);
//		Log.d(TAG, "saveUserState, sessionToken = " + sessionToken);
		
	}
	
	
	/**
	 * 通知状态：用户登录
	 */
	public void notifyUserLogined(UserInfo userInfo, String sessionID, String sessionToken){
		
        Log.d(TAG, "notifyUserLogined");
        
        // 保存用户信息
        saveUserState(userInfo, sessionID, sessionToken);
		
		// 触发回调
		for (LoginStateListener l: mUserLoginStateListenerList) {
			l.onUserLogined();
		}
	}
	
	/**
	 * 通知状态：用户注销
	 */
	public void notifyUserLogouted(){
	
        Log.d(TAG, "notifyUserLogouted");
        
	    // 清理用户信息
	    clearUserState();
	    
		// 触发回调
		for (LoginStateListener l: mUserLoginStateListenerList) {
			l.onUserLogouted();
		}
	}
    
    /**
     * 通知状态：用户的登录Session过期
     * PS：现在服务器控制session为1个月，其实基本不会出现
     * PS：互踢登录，也会导致session过期
     */
    public void notifyUserSessionExpired(boolean showToast){
        
        Log.d(TAG, "notifyUserSessionExpired");
        
        // 如果之前其他地方（如多条协议）已经通知过，这里就不再重复通知
        if (mIsLogined == false) {
        	Log.d(TAG, "notifyUserSessionExpired skip, as mIsLogined == false");
        	return;
        }
        
        // 统一提示
        if (showToast) {
//        	JDToast.showToast(R.string.account_session_expired);
        }
		
        // 清理信息
        clearUserState();
        
        // 触发回调
        for (LoginStateListener l: mUserLoginStateListenerList) {
            l.onUserSessionExpired();
        }
    }
    
    
	/**
     * 通知状态：用户信息修改
     */
    public void notifyUserInfoChanged(){
        
        Log.d(TAG, "notifyUserInfoChanged");
        
        // 触发回调
        for (LoginStateListener l: mUserLoginStateListenerList) {
            l.onUserInfoChanged();
        }
    }
    
    
    /**
     * 记录程序是否已经做过reloadUserState（从preference加载用户信息），用于控制该流程只执行一次，
     * 避免登录态过期、注销的时候回到登录页，又自动执行reloadUserState
     */
    public boolean hasDoneReloadUserState(){
    	return mHasDoneReloadUserState;
    }
    private boolean mHasDoneReloadUserState = false;
    
    /**
     * 程序重启时，如果上一次已登录，则从preference加载用户信息
     * @param notifyUserLogin 加载完之后，是否通知登录成功， 前台触发填true，后台触发填false
     * @return true 加载成功；false 加载失败；
     */
    public boolean reloadUserState(boolean notifyUserLogin){
    	
    	Log.d(TAG, "call reloadUserState");
    	
    	Context context = Env.getContext();
		boolean logined     = UserStatePrefernce.getLogined     (context);
		String userInfoStr  = UserStatePrefernce.getUserInfoStr (context);
		String sessionID    = UserStatePrefernce.getSessionID   (context);
		String sessionToken = UserStatePrefernce.getSessionToken(context);
		
		// 打日志
		Log.d(TAG, "reloadUserState, logined      = "  + logined);
		Log.d(TAG, "reloadUserState, userInfoStr  = "  + userInfoStr);
		Log.d(TAG, "reloadUserState, sessionID    = "  + sessionID);
		Log.d(TAG, "reloadUserState, sessionToken = "  + sessionToken);
		
    	
		// 如果没有之前没有登录，则退出
		if (logined == false) {
			return false;
		}
		
//		// 解析userInfo
//		UserInfo userInfo = null;
//		try {
//			userInfo = UserInfoUtil.convJsonToUserInfo(new JSONObject(userInfoStr));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//    	// 判断数据有效性
//		if ((logined == false) || userInfoStr.isEmpty() || sessionID.isEmpty() || sessionToken.isEmpty() || userInfo == null) {
//			Log.d(TAG, "skip reloadUserState as invalid state!");
//			return false;
//		}
		
		// 保存用信息到内存
	    mIsLogined    = true;
//		mUserInfo 	  = userInfo;
		mSessionID    = sessionID;
		mSessionToken = sessionToken;
		
		// 记录已经加载过
		mHasDoneReloadUserState = true;
		
		// 触发回调 (DataCenter的初始化依赖于登录回调)
		if (notifyUserLogin) {
	        for (LoginStateListener l: mUserLoginStateListenerList) {
	            l.onUserLogined();
	        }		    
		}

		
		// 返回成功
		return true;
    }
    
   /* 
    *//**
     * 程序重启时，在程序上一次已经登录的情况上，后台上报自动登录
     * PS：程序启动时，会自动登录，并同时进入首页，如果一旦检测到session过期，再返回到登录页
     *//*
    public void autoLogin(ResponseListener<HttpAutoLoginAccount.Data> listener){
    	
    	Log.d(TAG, "call autoLogin");
    	

    	HttpAutoLoginAccount.RequestParam param = new HttpAutoLoginAccount.RequestParam();
    	param.userID       = mUserInfo.userID;
    	param.sessionID    = mSessionID;
    	param.sessionToken = mSessionToken;

    	Log.d(TAG, "autoLogin, userID       = " + param.userID);
    	Log.d(TAG, "autoLogin, sessionID    = " + param.sessionID);
    	Log.d(TAG, "autoLogin, sessionToken = " + param.sessionToken);
    	
		new HttpAutoLoginAccount().request(param, listener);
    }
    
    */

    /**
     * 上一次上报地理位置的时间
     */
    private long mLatestReportLocationTime = 0;
    
//    /**
//     * 上报地理位置（需控制上报时间间隔）
//     */ 
//    public void reportLocation(){
//    	
//    	Log.d(TAG, "call reportLocation");
//    	
//    	// 程序运行内上报的最小时间间隔
//    	final long MIN_REPORT_SPAN = 1000 * 60 * 20; // 20分钟 
//    	
//    	// 判断最小的时间间隔有效
//    	long curTime = System.currentTimeMillis();
//    	long difTime = curTime - mLatestReportLocationTime;
//    	if (difTime < MIN_REPORT_SPAN) {
//    		Log.d(TAG, "skip reportLocation as difTime < MIN_REPORT_SPAN ");
//    		return;
//    	}
//    	
//    	
//    	// 判断地理位置是否有效
//    	if (BaiduMapHelper.getInstance().isLocationReady() == false) {
//    		Log.d(TAG, "skip reportLocation as difTime < MIN_REPORT_SPAN ");
//    		return;    		
//    	}
//    	
//    	// 上报地理位置
//    	LocInfo locInfo = new LocInfo();
//    	locInfo.longitude = BaiduMapHelper.getInstance().getLongitude();
//    	locInfo.latitude  = BaiduMapHelper.getInstance().getLatitude();    	
//    	locInfo.province  = BaiduMapHelper.getInstance().getProvince();
//    	locInfo.city 	  = BaiduMapHelper.getInstance().getCity();
//    	locInfo.district  = BaiduMapHelper.getInstance().getDistrict();
//    	locInfo.street 	  = BaiduMapHelper.getInstance().getStreet();
//
//    	HttpReportLocation.RequestParam param = new HttpReportLocation.RequestParam();
//    	param.locInfo = locInfo;
//		
//		new HttpReportLocation().request(param, new ResponseListener<EmptyResponseResultData>() {
//			
//			@Override
//			public void onSuccess(int responseCode,
//					ResponseResult<EmptyResponseResultData> result) {
//				// 上报成功，则更新是上报时间
//				if (result.rtn == 0) {
//					Log.d(TAG, "reportLocation success!");
//					mLatestReportLocationTime = System.currentTimeMillis();
//				}
//
//				// 检测session
//				HttpSessionUtil.checkSession(result.rtn, result.sessionStatus);
//				
//			}
//			
//			@Override
//			public void onFailed(int responseCode) {
//				Log.d(TAG, "reportLocation faile!");				
//			}
//		});
//		
//		// 日志
//		Log.d(TAG, "reportLocation, longitude = " + locInfo.longitude);
//		Log.d(TAG, "reportLocation, latitude  = " + locInfo.latitude);
//		Log.d(TAG, "reportLocation, province  = " + locInfo.province);
//		Log.d(TAG, "reportLocation, city 	  = " + locInfo.city);
//		Log.d(TAG, "reportLocation, district  = " + locInfo.district);
//		Log.d(TAG, "reportLocation, street    = " + locInfo.street);
//    }
    

//    /**
//     * 上一次上报上线通知的时间
//     */
//    private long mLatestNotifyOnlineTime = 0;
//    
//    /**
//     * 上线通知 （需控制上报时间间隔）
//     */
//    public void notifyOnline(){
//    	
//    	Log.d(TAG, "call notifyOnline");
//    	
//     	// 程序运行内上报的最小时间间隔
//    	final long MIN_REPORT_SPAN = 1000 * 60 * 3; // 3分钟 
//    	
//    	// 判断最小的时间间隔有效
//    	long curTime = System.currentTimeMillis();
//    	long difTime = curTime - mLatestNotifyOnlineTime;
//    	if (difTime < MIN_REPORT_SPAN) {
//    		Log.d(TAG, "skip notifyOnline as difTime < MIN_REPORT_SPAN ");
//    		return;
//    	}
//    	
//    	
//    	HttpNotifyOnline.RequestParam param = new HttpNotifyOnline.RequestParam();
//    	param.udid = "";
//    	new HttpNotifyOnline().request(param, new ResponseListener<EmptyResponseResultData>() {
//            
//            @Override
//            public void onSuccess(int responseCode, ResponseResult<EmptyResponseResultData> result) {
//
//                // 正常情况
//                if (result.rtn == 0) {
//                    Log.d(TAG, "notifyOnline success!");
//                    mLatestNotifyOnlineTime = System.currentTimeMillis();
//                }
//
//                // 检测session
//                HttpSessionUtil.checkSession(result.rtn, result.sessionStatus);
//            }
//            
//            @Override
//            public void onFailed(int responseCode) {
//                Log.d(TAG, "notifyOnline faile!");
//            }
//        });
//    }
    
    
    /**
     * 
     * 前台进程启动的时候，加载用户信息。并且触发登录状态的回调
     * 
     */
    public void reloadUserStateWhenForegroundBegin() {
        
        if (LoginHelper.getInstance().isLogined() == false) {
            
            LoginHelper.getInstance().reloadUserState(true);                        
        }

    }
    
    
    

}
