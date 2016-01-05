package com.android.shortvideo.discover.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * @author Apathy����
 * 
 *         ViewPager������
 * */
public class ViewPagerAdapter extends PagerAdapter {

	private ImageView[][] mImageViews;

	private int[] mImageRes;

	public ViewPagerAdapter(ImageView[][] imageViews, int[] imageRes) {
		this.mImageViews = imageViews;
		this.mImageRes = imageRes;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
//		if (mImageRes.length == 1) {
//			return mImageViews[position / mImageRes.length % 2][0];
//		} else {
//		}
		((ViewPager) container).addView(mImageViews[position
		                                            / mImageRes.length % 2][position % mImageRes.length], 0);
		return mImageViews[position / mImageRes.length % 2][position
				% mImageRes.length];
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
//		if (mImageRes.length == 1) {
//			((ViewPager) container).removeView(mImageViews[position
//					/ mImageRes.length % 2][0]);
//		} else {
//		}
		((ViewPager) container).removeView(mImageViews[position
		                                               / mImageRes.length % 2][position % mImageRes.length]);
	}
}
