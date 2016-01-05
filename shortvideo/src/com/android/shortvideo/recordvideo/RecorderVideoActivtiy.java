package com.android.shortvideo.recordvideo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.shortvideo.R;
import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.common.widget.CustomTitlebarLayout;
import com.android.shortvideo.recordvideo.utils.BaseMediaRecorder;
import com.android.shortvideo.recordvideo.utils.MediaRecorderUtil;
import com.android.shortvideo.recordvideo.widget.ProgressView;
import com.android.shortvideo.recordvideo.widget.ProgressView.State;
import com.android.shortvideo.recordvideo.widget.VideoSurfaceView;
import com.android.shortvideo.recordvideo.widget.VideoSurfaceView.OnStateListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 摄像头预览然后可以点击录制视频
 * @author james
 *
 */
@SuppressLint("NewApi")
public class RecorderVideoActivtiy extends Activity{  
  
	private static final int STOP_RECORD  = 0;
	private static final int START_RECORD = 1;
	private static final int PAUSE_RECORD = 2;
	private static final int FINSH        = 3;
	
	private boolean isRecording = false;

	private CustomTitlebarLayout mCustomTitlebarLayout;
	
//	private ImageView mFlashImage;  //打开闪光灯
	private ImageView mStartImage;// 开始录制按钮
	
    private VideoSurfaceView mSurfaceview;// 显示视频的控件  
    
//    RelativeLayout relativeLayout;
    
    RelativeLayout mBottomLayout;
    
    private int mScreenWidth;
    
    //录制进度条
    private ProgressView mProgressView;
    private long mTotalTime;
    private long mStartTime;
    private long mCurrentTime;
  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏

        // 选择支持半透明模式,在有surfaceview的activity中使用。  
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  
        setContentView(R.layout.record_video_layout);
//        List<String> videoList = new ArrayList<String>();
//        videoList.add(DeviceUtils.getSDPath()+"/mp4"+"/20151181168.mp4");
//        videoList.add(DeviceUtils.getSDPath()+"/mp4"+"/20151181193.mp4");
//        MediaRecorderUtil.mergeVideo(videoList);
        initView();  
        initListener();
    }  
  
    private void initView() {  
    	mScreenWidth     = DeviceUtil.getScreenWidth(RecorderVideoActivtiy.this);
    	mBottomLayout    = (RelativeLayout) findViewById(R.id.video_bottom);
        mStartImage      = (ImageView) findViewById(R.id.record_video_start_image);
        mSurfaceview     = (VideoSurfaceView) findViewById(R.id.surfaceview);  
        mProgressView    = (ProgressView) findViewById(R.id.record_video_progress_view);
        mCustomTitlebarLayout = (CustomTitlebarLayout) findViewById(R.id.record_video_title_layout);
        mCustomTitlebarLayout.setLeftImageResource(R.drawable.ic_close);
        mCustomTitlebarLayout.setRightImageResource(R.drawable.ic_switch_camera);
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSurfaceview.getLayoutParams();
    	layoutParams.height = mScreenWidth * 4 / 3;
    	mSurfaceview.setLayoutParams(layoutParams);
    	RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams();
    	layoutParams2.topMargin = mScreenWidth;
    	mBottomLayout.setLayoutParams(layoutParams2);
    	
    }
    
    private void initListener(){
    	mStartImage.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch (event.getAction()) {
				
					case MotionEvent.ACTION_DOWN:
						mStartImage.setScaleX(1.2f);
						mStartImage.setScaleY(1.2f);
						mHandler.sendEmptyMessage(START_RECORD);
						
						break;
					case MotionEvent.ACTION_UP:
						mHandler.sendEmptyMessage(PAUSE_RECORD);
						break;
					default:
						break;
				
				}
				return true;
			}
		});
    	
    	mCustomTitlebarLayout.setOnLeftListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	
    	mCustomTitlebarLayout.setOnRightListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSurfaceview.switchCamera();
			}
		});
    	
    	
        mSurfaceview.setOnStopListener(new OnStateListener() {
			
			@Override
			public void onStop() {
				// TODO Auto-generated method stub
//				playImage.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPause() {
				// TODO Auto-generated method stub
//				playImage.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
//				playImage.setVisibility(View.GONE);
			}
		});

    }
  
    private Handler mHandler = new Handler(){
    	
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case START_RECORD:
				mSurfaceview.startRecord();
				mStartTime = System.currentTimeMillis();
				isRecording = true;
				//开始进度条增长
				mProgressView.setCurrentState(State.START);
				break;
			case STOP_RECORD:
				mSurfaceview.stopRecord();
				if(MediaRecorderUtil.mFinalPath != null){
					Intent intent = new Intent(RecorderVideoActivtiy.this, FFmpegPreviewActivity.class);
					intent.putExtra("path", MediaRecorderUtil.mFinalPath);
					startActivity(intent);
				}else{
					Toast.makeText(RecorderVideoActivtiy.this, "null", Toast.LENGTH_LONG).show();
				}
				break;
			case PAUSE_RECORD:
				mStartImage.setScaleX(1f);
				mStartImage.setScaleY(1f);
				mSurfaceview.pauseRecord();
				//暂停进度
				mProgressView.setCurrentState(State.PAUSE);
				mTotalTime = System.currentTimeMillis() - mStartTime + mTotalTime;
				mProgressView.putProgressList((int)mTotalTime);
				isRecording = false;
				Log.e("totalTime", mTotalTime+"");
				if(mTotalTime >= BaseMediaRecorder.TIME_LENGTH){
					mHandler.sendEmptyMessage(STOP_RECORD);
				}
				break;
			default:
				break;
			}
    	};
    };
    
    class TestVideoListener implements OnClickListener {  
  
        @Override  
        public void onClick(View v) {  
//            if (v == start) {  
//            	surfaceview.startRecord();
//            }
//            if(v == reset){
//            	surfaceview.reset();
//            }
//            if (v == stop) {  
//            	surfaceview.stopRecord();
//            }
//            if(v == switch_btn){
//            	surfaceview.switchCamera();
//            }
//            if(v == pause){
//            	surfaceview.pauseRecord();
//            }
//            
//            if(v == merge){;
//            	for(int i = 0;i < 2;i++){
//            		Log.e("tag", BaseMediaRecorder.getVideoList().get(i));
//            	}
////            	BaseMediaRecorder.mergeVideo();
//            }
        }  
  
    }  
	
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	mSurfaceview.prepare();
    }
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSurfaceview.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSurfaceview.onDestroy();
		mSurfaceview = null;  
	}
}