/**
 * 
 */
package com.android.shortvideo.homepage;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.shortvideo.BaseFragment;
import com.android.shortvideo.Env;
import com.android.shortvideo.R;
import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.common.utils.DipPixelUtil;

/**
 * @author YeGuangRong
 *
 */
public class HomePageFragment extends BaseFragment{

	public static final String TAG = "HomePageFragment";
	
	private View mHomePageView;
	
	private ViewPager mViewPager;
	
	//精选
	private TextView mEssenceText;
	private RelativeLayout mEssenceLayout;
	//同城
	private TextView mCrossTownText;
	private RelativeLayout mCrossTownLayout;
	//Viewpager导航线
	private ImageView mGuideLine;
	private LinearLayout mLineLayout;
	
	private ArrayList<Fragment> mFragments;
	
	private int mGuideLineWidth;
	
	//当前选中的fragment
	private int mCurrentFragment;
	
	//动态设置导航线的长度
	LayoutParams mLayoutParams;
	
	
	private int mTextLeft;
	private int mEssenceWidth;
	private float mLastOffset = -1;
	private float mScrollX;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		mHomePageView = inflater.inflate(R.layout.fragment_homepage_layout, container, false);
		
		initView();
		initListener();
		initData();
		
		return mHomePageView;
	}
	
	private void initView(){
		mEssenceLayout      = (RelativeLayout) mHomePageView.findViewById(R.id.essence_fragment_layout);
		mCrossTownLayout    = (RelativeLayout) mHomePageView.findViewById(R.id.crosstown_fragment_layout);
		mLineLayout         = (LinearLayout) mHomePageView.findViewById(R.id.homepage_guide_line_layout);
		mViewPager          = (ViewPager) mHomePageView.findViewById(R.id.homepage_viewpager);
		mEssenceText        = (TextView)  mHomePageView.findViewById(R.id.essence_fragment_text);
		mCrossTownText      = (TextView)  mHomePageView.findViewById(R.id.crosstown_fragment_text);
		mGuideLine          = (ImageView) mHomePageView.findViewById(R.id.homepage_guide_line_view);
	}
	
	private void initListener(){
		mEssenceText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(0);
				
			}
		});
		
		mCrossTownText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(1);
				
			}
		});
	}
	
	private void initData(){
		//动态设置导航线的长度
		mGuideLineWidth              = DeviceUtil.getScreenWidth(Env.getContext()) / 2;
		mLayoutParams = (LayoutParams) mGuideLine.getLayoutParams();
		
		//将精选和同城的两个fragment放进ArrayList容器
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new EssenceFragment());
		mFragments.add(new FollowFragment());
		
		//设置设配器
		mViewPager.setAdapter(new MyFragmentStatePagerAdapter(getChildFragmentManager(), mFragments));
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		mViewPager.setCurrentItem(0);
//		resetTextColor();
//		mEssenceText.setTextSize(18);
//		mEssenceText.setTextColor(getActivity().getResources().getColor(R.color.title_bg_color));
	}
	
	class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter{

		ArrayList<Fragment> mFragments;
		
		public MyFragmentStatePagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}
		
		public MyFragmentStatePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
			super(fm);
			// TODO Auto-generated constructor stub
			this.mFragments = fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFragments.size();
		}
	}
	
	/**
	 * 监听ViewPager滑动
	 */
	class MyPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int position, float offset, int arg2) {
			// TODO Auto-generated method stub
			if(mTextLeft == 0){
				mTextLeft = mEssenceText.getLeft();
				mEssenceWidth = mEssenceText.getMeasuredWidth();
				Log.e("left", "mTextLeft = "+mTextLeft);
				mLayoutParams.leftMargin     = mTextLeft;
				mLayoutParams.width          = mEssenceWidth;
				mGuideLine.setLayoutParams(mLayoutParams);
				Log.e("left", "mTextLeft = "+mTextLeft+" mEssenceWidth = "+mEssenceWidth);
			}
			else{
				//左滑offset 从 0.0到0.99
				if(offset > mLastOffset){
					mScrollX = (int)(offset * (DipPixelUtil.dip2px(Env.getContext(), 20)+mEssenceWidth));
//					mLayoutParams.leftMargin = (int)(mTextLeft + offset * (DipPixelUtil.dip2px(Env.getContext(), 20)+mEssenceWidth));
					
				}else if(offset > 0){
					mScrollX = (int)(offset * (DipPixelUtil.dip2px(Env.getContext(), 20)+mEssenceWidth));
//					mLayoutParams.leftMargin = (int)(mTextLeft + offset * (DipPixelUtil.dip2px(Env.getContext(), 20)+mEssenceWidth));
				}
				mLastOffset = offset;
//			    mGuideLine.setLayoutParams(mLayoutParams);
				mLineLayout.scrollTo((int) -mScrollX, 0);
			}
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
//			resetTextColor();
//			switch (position) {
//			case 0:
//				mEssenceText.setTextSize(18);
//				break;
//			case 1:
//				mCrossTownText.setTextSize(18);
//				break;
//			default:
//				break;
//			}
			mCurrentFragment = position;
		}
	}
	
	
	/**
	 * 重置title颜色
	 */
//	private void resetTextColor(){
//		mEssenceText.setTextSize(17);
//		mCrossTownText.setTextSize(17);
//	}
	
	public static void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
//		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//		int widthMeasureSpec  = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
//		child.measure(widthMeasureSpec, heightMeasureSpec);
	}
}

