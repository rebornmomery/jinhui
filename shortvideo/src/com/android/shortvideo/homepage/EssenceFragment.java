/**
 * 
 */
package com.android.shortvideo.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shortvideo.BaseFragment;
import com.android.shortvideo.R;

/**
 * 精选
 * @author YeGuangRong
 *
 */
public class EssenceFragment extends BaseFragment{
	
	public static final String TAG = "EssenceFragment";
	
	private View mEssenceFragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mEssenceFragmentView = inflater.inflate(R.layout.fragment_essence_layout, container, false);
		return mEssenceFragmentView;
	}

}

