package com.android.shortvideo.common.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 测试时使用的工具�?
 * @author ls
 *
 */
@SuppressLint("NewApi")
public class TestUtil {
    public static final String TAG = TestUtil.class.getSimpleName();
    /**
     * 打印文件相关信息
     * @param path
     */
    public static void printFileInfo(String path){
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            
            if (file != null && file.exists()) {
                Log.d(TAG, "File " + path + "| size :" + file.length() / 1024 +  "KB");
            }else {
                Log.d(TAG, "File " + path + " is not exist");
            }
            
        }else {
            Log.d(TAG, "File " + path + " is not exist");
        }
    }
}
