/**
 * 
 */
package com.android.shortvideo.common.widget;

import com.android.shortvideo.Env;
import com.android.shortvideo.R;
import com.android.shortvideo.common.utils.ResourceUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author YeGuangRong
 * 通用的titlebar
 */
public class CustomTitlebarLayout extends LinearLayout{

	private View mTitleBarLayout;
	
	private TextView mLeftView;
	private TextView mRightView;
	private TextView mTitleView;
	private Context mContext;

	public CustomTitlebarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CustomTitlebarLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context){
		mContext        = context;
		mTitleBarLayout = LayoutInflater.from(mContext).inflate(R.layout.custom_title_bar_layout, this, false);
		mLeftView       = (TextView)mTitleBarLayout.findViewById(R.id.left_text);
		mRightView      = (TextView)mTitleBarLayout.findViewById(R.id.right_text);
		mTitleView      = (TextView)mTitleBarLayout.findViewById(R.id.title_text);
		addView(mTitleBarLayout);
	}
	
	/**
	 * 左边按钮点击监听
	 * @param onClickListener
	 */
	public void setOnLeftListener(OnClickListener onClickListener){
		if(onClickListener == null){
			return;
		}
		
		mLeftView.setOnClickListener(onClickListener);
	}
	
	/**
	 * 监听右边按钮点击
	 * @param onClickListener
	 */
	public void setOnRightListener(OnClickListener onClickListener){
		if(onClickListener == null){
			return;
		}
		mRightView.setOnClickListener(onClickListener);
	}
	
	public void setTitle(int title){
		mTitleView.setText(ResourceUtil.getString(title));
	}
	
	public void setTitleBarBackgroud(int resource){
		mTitleBarLayout.setBackgroundColor(ResourceUtil.getColor(resource));
	}
	
	public void setRightImageResource(int resource){
		mRightView.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
	}
	
	public void setLeftImageResource(int resourceID){
    	mLeftView.setCompoundDrawablesWithIntrinsicBounds(resourceID, 0, 0, 0);
    }
	
}

