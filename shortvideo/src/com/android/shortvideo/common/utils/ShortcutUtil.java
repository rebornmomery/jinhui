package com.android.shortvideo.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;


/**
 * 用于创建快捷方式的工具类
 */
public class ShortcutUtil {

    /**
     * 
     */
    
    /**
     * 添加程序快捷方式（只添加�?���?
     * @param context       context
     * @param appName       应用�?
     * @param iconResId     图标的资源ID
     * @param packageName   程序的packageName
     * @param loadingActivityClassName  启动页面的类�?
     */
    public static void installShortcut(Context context, String appName, int iconResId, String packageName, String loadingActivityClassName) {

        SharedPreferences settings = context.getSharedPreferences("InstallShortcut", Activity.MODE_PRIVATE);
        boolean isInstall = settings.getBoolean("isInstall", false);
        if (isInstall == false) {

            Parcelable icon = Intent.ShortcutIconResource.fromContext(context, iconResId);

            // 设置快捷方式的目标intent
            Intent desIntent = new Intent();
            desIntent.setAction("android.intent.action.MAIN");
            desIntent.addCategory("android.intent.category.LAUNCHER");
            desIntent.setClassName(packageName, loadingActivityClassName);
            desIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            // 添加快捷方式
            Intent installIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, desIntent);
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
            installIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            installIntent.putExtra("duplicate", false); // 禁止重复添加，否则用户清空数据之后，再运行会出现两个图标
            context.sendBroadcast(installIntent);

            // 记录已经添加过快捷方�?
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isInstall", true);
            editor.commit();
        }
       
    }
}
