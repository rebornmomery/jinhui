package com.android.shortvideo;


import android.app.Application;

/**
 * @author YeGuangRong
 *
 */
public class BaseApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 设置程序环境（必须先设置）
		Env.setContext(this.getApplicationContext());
		Env.setAppStartTime();		
	}
}

