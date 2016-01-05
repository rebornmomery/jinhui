/**
 * 
 */
package com.android.shortvideo.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.shortvideo.BaseFragment;
import com.android.shortvideo.R;
import com.android.shortvideo.common.widget.BannerViewpagerLayout;
import com.android.shortvideo.common.widget.CustomTitlebarLayout;

/**
 * @author YeGuangRong
 *
 */
public class DiscoverFragment extends BaseFragment{

	public static final String TAG = "DiscoverFragment";
	public static final int VIEWPAGER_MAXLENGTH = 1000;
	
	private View mDiscoverFragmentView;
	CustomTitlebarLayout mCustomTitlebarLayout;
	
	private int[] resources = {R.drawable.near_bg_1, R.drawable.near_bg_2, R.drawable.near_bg_3, R.drawable.near_bg_4, R.drawable.near_bg_5};
	//banner底部的点
	private LinearLayout mBannerBottomPointerLayout;
	
	BannerViewpagerLayout mViewpagerLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mDiscoverFragmentView = inflater.inflate(R.layout.fragment_discover_layout, container, false);
		initView();
		initListener();
		initData();
		return mDiscoverFragmentView;
	}
	
	private void initView(){
		mViewpagerLayout = (BannerViewpagerLayout) mDiscoverFragmentView.findViewById(R.id.discover_banner_layout);
		mCustomTitlebarLayout = (CustomTitlebarLayout) mDiscoverFragmentView.findViewById(R.id.fragment_discover_title_layout);
		mCustomTitlebarLayout.setTitle(R.string.discover);
	}
	
	private void initListener(){
		
	}
	
	private void initData(){
		mViewpagerLayout.setData(resources);
	}
	
	
	
}

