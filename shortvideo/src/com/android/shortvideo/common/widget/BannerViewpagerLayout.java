/**
 * 
 */
package com.android.shortvideo.common.widget;

import java.util.ArrayList;

import com.android.shortvideo.Env;
import com.android.shortvideo.R;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author YeGuangRong
 *
 */
public class BannerViewpagerLayout extends RelativeLayout{

	private View mBannerView;
	
	private Context mContext;
	
	private ArrayList<ImageView> mImageViews;
	private ArrayList<ImageView> mPointImageViews;
	
	private int mCurrentIndex;
	
	/**
	 * banner展示的容器
	 */
	private ViewPager mViewPager;
	
	/**
	 * banner底部的导航点
	 */
	private LinearLayout mLinearLayout;
	
	private boolean isTouch;
	
	//图片数据
//	private ArrayList<Integer> mImagePaths;
	private int[] mImagePaths;
	
	public BannerViewpagerLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public BannerViewpagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public BannerViewpagerLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context){
		mContext      = context;
		mBannerView   = LayoutInflater.from(context).inflate(R.layout.banner_viewpager_layout, this, false);
		mViewPager    = (ViewPager) mBannerView.findViewById(R.id.discover_banner_viewpager);
		mLinearLayout = (LinearLayout) mBannerView.findViewById(R.id.discover_banner_pointer_layout);
		addView(mBannerView);
	}
	
	public void setData(int[] imagePaths){
		this.mImagePaths =imagePaths;
		mImageViews = new ArrayList<ImageView>();
		mPointImageViews = new ArrayList<ImageView>();
		for(int i = 0; i<mImagePaths.length; i++){
			ImageView imageView = new ImageView(Env.getContext());
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mImageViews.add(imageView);
			mImageViews.get(i).setImageResource(mImagePaths[i]);
			ImageView imageView2 = new ImageView(Env.getContext());
			mPointImageViews.add(imageView2);
			imageView2.setPadding(10, 0, 10, 0);
			imageView2.setImageResource(R.drawable.page_control_point_white);
			mLinearLayout.addView(imageView2);
		}
		
		mPointImageViews.get(0).setImageResource(R.drawable.page_control_point_blue);
		mCurrentIndex = 0;
		setAdapter();
		mViewPager.setCurrentItem(imagePaths.length*100);
		mViewPager.setOnTouchListener(new OnTouchListener() {
			
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mHandler.removeMessages(mCurrentIndex);
					
					mHandler.sendEmptyMessage(-1);
					isTouch = true;
					break;
				case MotionEvent.ACTION_MOVE:
					
					break;
				case MotionEvent.ACTION_UP:
					break;
				default:
					break;
				}
				
				return false;
			}
		});
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what != -1){
//				mCurrentIndex = msg.what;
				mCurrentIndex = mViewPager.getCurrentItem();
				mCurrentIndex++;
				
				mViewPager.setCurrentItem(mCurrentIndex, true);
				mHandler.removeMessages(msg.what);
				mHandler.sendEmptyMessageDelayed(mCurrentIndex, 3000);
			}
		};
	};
	
	private void setAdapter(){
		mViewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public Object instantiateItem(View container, int position) {
				// TODO Auto-generated method stub
				Log.e("positon", position+"");
				((ViewPager)container).addView(mImageViews.get(position % mImageViews.size()), 0);
				return mImageViews.get(position % mImageViews.size());
			}
			
			@Override
			public boolean isViewFromObject(View view, Object object) {
				// TODO Auto-generated method stub
				return view == object;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return Integer.MAX_VALUE; //这样可以实现循环播放的效果
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				// TODO Auto-generated method stub
				((ViewPager)container).removeView((View)object);;
			}
		});
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				mCurrentIndex = position; 
				resetPoint();
				mPointImageViews.get(position % mImageViews.size()).setImageResource(R.drawable.page_control_point_blue);
				if(isTouch){
					isTouch = false;
					mHandler.sendEmptyMessageDelayed(mCurrentIndex, 3000);
				}
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
			}
		});
		mHandler.sendEmptyMessageDelayed(mCurrentIndex, 3000);
	}
	
	/**
	 * 重置底部的点
	 */
	private void resetPoint(){
		for(int i = 0; i<mPointImageViews.size(); i++){
			mPointImageViews.get(i).setImageResource(R.drawable.page_control_point_white);
		}
	}

}

