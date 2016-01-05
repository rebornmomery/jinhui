/**
 * 
 */
package com.android.shortvideo.recordvideo.utils;

import com.android.shortvideo.Env;

import android.content.Context;
import android.os.Environment;

/**
 * @author YeGuangRong
 *
 */
public class PathUtil {

	/**
	 * 获取cache根目录
	 */
	public static String getCacheDir(){
		Context context = Env.getContext();
		final String cachePath;
		boolean externalStorageAvailable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if(externalStorageAvailable){
			cachePath = context.getExternalCacheDir().getAbsolutePath();
		}else{
			cachePath = context.getCacheDir().getAbsolutePath();
		}
		return cachePath;
	}
}

