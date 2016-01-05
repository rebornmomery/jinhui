/**
 * 
 */
package com.android.shortvideo;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author YeGuangRong
 *
 */
public class BaseActivity extends FragmentActivity{
	
	private static final String TAG = "BaseActivity";
	
	/**
	 * 记录当前最顶部的Activity
	 */
	private static BaseActivity sTopActivity = null;

	/**
	 * 获取顶部的Activity
	 */
	public static BaseActivity getTopActivity() {
		return sTopActivity;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
	}

}

