package com.android.shortvideo.common.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * 内存相关的工具类
 * @author ls
 *
 */
public class MemoryUtil {
    
    /**
     * 获取应用能使用的运行内存(大概�?
     * 返回值单位：M
     * 
     * @param context
     * @return
     */
    public static int getAppMemory(Context context){
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getMemoryClass();
    }
    
    /**
     * 获取应用能使用的�?��运行内存(大概�?
     * 注意：需使用大堆栈的时�?才能用到这么多内存（在manifest中设置了largeHeap=true �?
     * 返回值单位：M
     * 
     * @param context
     * @return
     */
    public static int getLargerAppMemory(Context context){
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getLargeMemoryClass();
    }
}
