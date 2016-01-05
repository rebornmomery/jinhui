
package com.android.shortvideo.common.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;

/**
 * 对Activity基本判断的工�?
 */
public class ActivityUtil {

	/**
	 * 判断程序是否在前�?
	 */
	public static boolean isAppInForeground(Context context) {
		boolean result = false;
		String packageName = context.getPackageName();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
		if (task_list.size() > 0) {
			if (task_list.get(0).topActivity.getPackageName().trim()
					.equals(packageName)) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * 判断程序是否在前台（遍历判断RunningAppProcessInfo.importance�?
	 */
	public static boolean isAppInForegroundByImportance(Context ctx) {
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses != null) {
			String package_name = ctx.getPackageName();
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.processName.equals(package_name)
						&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �?��该Context是否在栈�?
	 * @param activity
	 * @return
	 */
	public static boolean isTop(Context activity) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) activity
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTasks = am
				.getRunningTasks(Integer.MAX_VALUE);
		if (runningTasks != null && runningTasks.size() > 0) {
			RunningTaskInfo taskInfo = runningTasks.get(0);
			String temp = taskInfo.topActivity.getClassName();
			if (activity.getClass().getName().equals(temp)) {
				return true;
			}
		}
		return isTop;
	}

	/**
	 * �?��页面是否在前�?
	 * @param ctx
	 * @param cls
	 * @return
	 */
	public static boolean isActivityForeground(Context ctx, Class<?> cls) {
		boolean result = false;
		String clsName = cls.getName();
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> task_list = activityManager.getRunningTasks(1);
		if (task_list.size() > 0) {
			String topName = task_list.get(0).topActivity.getClassName();
			if (clsName.equals(topName)) {
				result = true;
			}
		}
		return result;
	}
	
   /**
     * 判断指定程序是否在前台（遍历判断RunningAppProcessInfo.importance�?
     */
    public static boolean isSpecAppInForeground(Context ctx,final String specAppName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null) {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(specAppName)
                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

}
