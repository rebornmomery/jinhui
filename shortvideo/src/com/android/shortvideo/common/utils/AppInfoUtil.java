package com.android.shortvideo.common.utils;




import com.android.shortvideo.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * 获取当前应用的基本信�?
 * PS：渠道号、产品ID�?��用到values\package_config.xml
 * PS：因为这个类与业务有�?��关系，做好后续可以再调整�?��位置
 */
public class AppInfoUtil {
	
	/**
	 * 获取客户端版本号
	 */
	private static String sVersion;
	public static String getVersion(Context context) {
		// 只获取一�?
		if (sVersion == null) {
			try {
				PackageManager pm = context.getPackageManager();
				String packageName = context.getPackageName();
				PackageInfo pinfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
				sVersion = pinfo.versionName;
			} catch (NameNotFoundException e) {
				sVersion = "1.0.0.0"; // 异常情况
			}
		}
		return sVersion;
	}

	/**
	 * 获取客户端版本号
	 */
	private static int sVersionCode;
	public static int getVersionCode(Context context) {
		// 只获取一�?
		if (sVersionCode == 0) {

			try {
				PackageManager pm = context.getPackageManager();
				String packageName = context.getPackageName();
				PackageInfo pinfo = pm.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
				sVersionCode = pinfo.versionCode;
			} catch (NameNotFoundException e) {
				sVersionCode = 0; // 异常情况
			}
		}
		return sVersionCode;
	}

	/**
	 * 获得packageName
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}
	
	/**
	 * 获取应用名称
	 */
	public static String getAppName(Context context) {
	    String appName = context.getString(R.string.app_name);
	    return appName;
	}
	
	/**
	 * 获取客户端渠道号
	 */
	private static String sChannelID;
	public static String getChannelID(Context context) {
		// 只获取一�?
//		if (sChannelID == null) {
//			sChannelID = context.getString(R.string.channelID);
//		}
		return sChannelID;
	}

	/**
	 * 获取客户端产品号
	 */
	private static String sProductIdID;
	public static String getProductID(Context context) {
		// 只获取一�?
//		if (sProductIdID == null) {
//			sProductIdID = context.getString(R.string.productID);
//		}
		return sProductIdID;
	}
	
}
