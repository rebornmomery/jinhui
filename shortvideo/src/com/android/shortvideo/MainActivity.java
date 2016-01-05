package com.android.shortvideo;

import com.android.shortvideo.common.utils.ResourceUtil;
import com.android.shortvideo.discover.DiscoverFragment;
import com.android.shortvideo.homepage.HomePageFragment;
import com.android.shortvideo.recordvideo.FFmpegRecorderActivity;
import com.android.shortvideo.recordvideo.NewFFmpegRecorderActivity;
import com.android.shortvideo.recordvideo.RecorderVideoActivtiy;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

	public static final String TAG = "MainActivity";
	
	private FragmentManager mFragmentManager;
	
	private FragmentTransaction mFragmentTransaction;
	
	private int mCurrentFragment = -1;
	
	private static final int HOMEPAGE = 0;
	
	private static final int DISCOVER = 1;

	//转载fragment
	private RelativeLayout mFragmentContainerLayout;
	
	//homepage fragment
	private HomePageFragment mHomePageFragment;
	private RelativeLayout mHomepageLayout;
	private ImageView mHomePageImage;
	private TextView mHomePageText;
	
	//DiscoverFragment
	private DiscoverFragment mDiscoverFragment;
	private RelativeLayout mDiscoverLayout;
	private ImageView mDiscoverImage;
	private TextView mDiscoverText;
	
	//录制视频
	private RelativeLayout mRecordVideoLayout;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
		initListener();
		initData();
		
	}
	
	private void initViews(){
		mFragmentContainerLayout = (RelativeLayout) findViewById(R.id.main_fragment_container);
		
		//主页部分
		mHomepageLayout          = (RelativeLayout) findViewById(R.id.homepage_fragment_button);
		mHomePageImage           = (ImageView) findViewById(R.id.homepage_fragment_image);
		mHomePageText            = (TextView) findViewById(R.id.homepage_fragment_text);
		
		//发现部分
		mDiscoverLayout          = (RelativeLayout) findViewById(R.id.disconver_fragment_button);
		mDiscoverImage           = (ImageView) findViewById(R.id.discover_fragment_image);
		mDiscoverText            = (TextView) findViewById(R.id.discover_fragment_text);
		
		//录制视频
		mRecordVideoLayout       = (RelativeLayout) findViewById(R.id.video_activity_button);
	}
	
	private void initListener(){
		mHomepageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickBottomButton(mHomepageLayout);
			}
		});
		
		mDiscoverLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickBottomButton(mDiscoverLayout);
			}
		});
		
		mRecordVideoLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				clickBottomButton(mRecordVideoLayout);
			}
		});
	}
	
	private void initData(){
		
		mFragmentManager      = getSupportFragmentManager();
		mFragmentTransaction  = mFragmentManager.beginTransaction();
		mHomePageFragment     = new HomePageFragment();
		mFragmentTransaction.add(R.id.main_fragment_container, mHomePageFragment, HomePageFragment.TAG);
		mFragmentTransaction.commit();
		mCurrentFragment = HOMEPAGE;
		
		/**
		 * 初始化默认选中homepage
		 */
		mHomePageImage.setSelected(true);
		mHomePageText.setTextColor(ResourceUtil.getColor(R.color.title_bg_color));
	}
	
	
	/**
	 * 点击按钮
	 */
	private void clickBottomButton(View view){
		
		if(mFragmentTransaction == null || view == null){
			return;
		}
		if(view == mHomepageLayout){
			if(mCurrentFragment != HOMEPAGE){
				Log.e(TAG, "Homepage");
				recoverButton();
				mFragmentTransaction  = mFragmentManager.beginTransaction();
				mFragmentTransaction.replace(R.id.main_fragment_container, new HomePageFragment(), HomePageFragment.TAG);
				mFragmentTransaction.commit();
				mCurrentFragment = HOMEPAGE;
				
				mHomePageImage.setSelected(true);
				mHomePageText.setTextColor(ResourceUtil.getColor(R.color.title_bg_color));
				
			}
		}
		else if(view == mDiscoverLayout){
			
			if(mCurrentFragment != DISCOVER){
				Log.e(TAG, "Discover");
				recoverButton();
				mFragmentTransaction  = mFragmentManager.beginTransaction();
				DiscoverFragment discoverFragment = new DiscoverFragment();
				mFragmentTransaction.replace(R.id.main_fragment_container, discoverFragment, DiscoverFragment.TAG);
				mFragmentTransaction.commit();
				mCurrentFragment = DISCOVER;
				
				mDiscoverImage.setSelected(true);
				mDiscoverText.setTextColor(ResourceUtil.getColor(R.color.title_bg_color));
			}
		}
		else if(view == mRecordVideoLayout){
			
			Intent intent = new Intent(MainActivity.this, RecorderVideoActivtiy.class);
			startActivity(intent);
			
		}
	}
	
	/**
	 * 全部回复按钮样式
	 */
	private void recoverButton(){
		mHomePageImage.setSelected(false);
		mHomePageText.setTextColor(ResourceUtil.getColor(R.color.com_text_tx2));
		
		mDiscoverImage.setSelected(false);
		mDiscoverText.setTextColor(ResourceUtil.getColor(R.color.com_text_tx2));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
