/**
 * 
 */
package com.android.shortvideo.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.shortvideo.BaseFragment;
import com.android.shortvideo.Env;
import com.android.shortvideo.R;

import de.greenrobot.event.EventBus;

/**
 * 关注
 * @author YeGuangRong
 *
 */
public class FollowFragment extends BaseFragment{

public static final String TAG = "CrosstownFragment";
	
	private View mCrosstownFragment;
	TextView mTextView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mCrosstownFragment = inflater.inflate(R.layout.fragment_follow_layout, container, false);
		initUI();
		return mCrosstownFragment;
	}
	
	private void initUI(){
		mTextView = (TextView) mCrosstownFragment.findViewById(R.id.test_text);
		mTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EventBus.getDefault().post("广荣");
			}
		});
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}

