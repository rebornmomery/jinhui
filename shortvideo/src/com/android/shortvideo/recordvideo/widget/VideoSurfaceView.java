package com.android.shortvideo.recordvideo.widget;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.common.utils.StringUtil;
import com.android.shortvideo.recordvideo.utils.BaseMediaRecorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("NewApi")
public class VideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback{

	private SurfaceHolder surfaceHolder;
	
	private BaseMediaRecorder mBaseMediaRecorder;
	
	//暂停录制
	private static final int PAUSE = 1;
	
	//停止录制标志
	private static final int STOP = 2;
	
	
	
	private Camera camera;
	
    /** 摄像头类型（前置/后置），默认后置 */
	protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	/** 摄像头参数 */
	protected Camera.Parameters mParameters = null;
	/** 摄像头支持的预览尺寸集合 */
	protected List<Size> mSupportedPreviewSizes;
	/** 最大帧率 */
	public static final int MAX_FRAME_RATE = 25;
	/** 最小帧率 */
	public static final int MIN_FRAME_RATE = 15;
	/** 帧率 */
	protected int mFrameRate = MIN_FRAME_RATE;
	
	private boolean mIsSurfaceCreate = false;
	 
	private boolean mPrepared = false;
	 
    private boolean mIsStartPreview = false;   

    private OnStateListener mOnStateListener;
    
    private long startTime;
    private long stopTime;
    
	public VideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public VideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public VideoSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init(){
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		// setType必须设置，要不出错.  
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
        
      //初始化视频录制
      	mBaseMediaRecorder = new BaseMediaRecorder();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		  surfaceHolder = holder;  
		  mIsSurfaceCreate = true;
		  if(mPrepared){
			  startPreview();
		  }
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder  
        surfaceHolder = holder;  
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		surfaceHolder = null;
		mIsSurfaceCreate = false;
	}
	
	/***
	 * 开始录制
	 */
	public void startRecord(){
		mBaseMediaRecorder.startRecord(surfaceHolder.getSurface(), camera);
		
		startTime = System.currentTimeMillis();
		
//		handler.sendEmptyMessageDelayed(STOP, BaseMediaRecorder.TIME_LENGTH);
		
	}
	
//	Handler handler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//
//			
//			
//			if(msg.what == PAUSE){
//				Log.e("record", "暂停录制");
//				stopTime = System.currentTimeMillis();
//				handler.sendEmptyMessageDelayed(STOP, BaseMediaRecorder.TIME_LENGTH - (stopTime - startTime));
//			}
//			else if(msg.what == STOP){
//				if((stopTime - startTime) >= 10000){
//					stopRecord();
//				}
//				Log.e("record", "停止录制");
//			}
//			
//		};
//	};
	
	/**
	 * 暂停录制
	 */
	public void pauseRecord(){
		
		if(mBaseMediaRecorder == null){
			return;
		}

		mBaseMediaRecorder.pauseRecord();
		
//		handler.sendEmptyMessage(PAUSE);
		
		reset();
		
//		if(mOnStateListener != null){
//        	mOnStateListener.onPause();
//        }
	}
	
	
	/**
	 * 结束录制
	 */
	public void stopRecord(){
		
		if(mBaseMediaRecorder == null){
			return;
		}
		
		mBaseMediaRecorder.stopRecord();
		
		stopPreview();
	}

	/**
	 * 重置预览
	 */
	public void reset(){
		stopPreview();
		startPreview();
	}
	
	/** 开始预览 */
	public void startPreview() {
		
		if(mOnStateListener != null){
			mOnStateListener.onStart();
		}
		mPrepared = true;
		
		if (surfaceHolder == null || !mPrepared)
			return;

		try {
			if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
				camera = Camera.open();
			else
				camera = Camera.open(mCameraId);

            camera.setDisplayOrientation(90);
			try {
				camera.setPreviewDisplay(surfaceHolder);
			} catch (IOException e) {
			}

			//设置摄像头参数
			mParameters = camera.getParameters();
			mSupportedPreviewSizes = mParameters.getSupportedPreviewSizes();//	获取支持的尺寸
			prepareCameraParaments();
			camera.setParameters(mParameters);
			setPreviewCallback();
			camera.startPreview();
			mIsStartPreview = true;
			onStartPreviewSuccess();
//			if (mOnPreparedListener != null)
//				mOnPreparedListener.onPrepared();
		} catch (Exception e) {
			e.printStackTrace();
//			if (mOnErrorListener != null) {
//				mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_PREVIEW, 0);
//			}
		}
		
	}

	//停止预览
	private void stopPreview(){
		if (camera != null) {
			try {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				// camera.lock();
				camera.release();
				if(mOnStateListener != null){
					mOnStateListener.onStop();
				}
			} catch (Exception e) {
			}
			camera = null;
		}
		mIsStartPreview = false;
		
	}
	
	public void prepare(){
		mPrepared = true;
	}
	
	/**
	 * 按homm键的时候进行的处理
	 */
	public void onPause(){
		stopPreview();
		release();
	}
	
	public void release(){
		surfaceHolder = null;
		mPrepared = false;
		mIsSurfaceCreate = false;
	}
	
	public void onDestroy(){
		 surfaceHolder = null;  
		 mBaseMediaRecorder = null;  
		 camera = null;
	}
	
	
	/**
	 * 停止播放时的回调
	 * @author james
	 *
	 */
	public interface OnStateListener {
		public void onStop();
		public void onPause();
		public void onStart();
	}
	
	public void setOnStopListener(OnStateListener onStopListener){
		this.mOnStateListener = onStopListener;
	}

	/** 
	 * 预处理一些拍摄参数
	 * 注意：自动对焦参数cam_mode和cam-mode可能有些设备不支持，导致视频画面变形，需要判断一下，已知有"GT-N7100", "GT-I9308"会存在这个问题
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected void prepareCameraParaments() {
		if (mParameters == null)
			return;

		List<Integer> rates = mParameters.getSupportedPreviewFrameRates();
		if (rates != null) {
			if (rates.contains(MAX_FRAME_RATE)) {
				mFrameRate = MAX_FRAME_RATE;
			} else {
				Collections.sort(rates);
				for (int i = rates.size() - 1; i >= 0; i--) {
					if (rates.get(i) <= MAX_FRAME_RATE) {
						mFrameRate = rates.get(i);
						break;
					}
				}
			}
		}

		mParameters.setPreviewFrameRate(mFrameRate);
		// mParameters.setPreviewFpsRange(15 * 1000, 20 * 1000);
		
		//这里面的参数只能是几个特定的参数，否则会报错.(176*144,320*240,352*288,480*360,640*480) 
		mParameters.setPreviewSize(640, 480);// 3:2

		// 设置输出视频流尺寸，采样率
		mParameters.setPreviewFormat(ImageFormat.NV21);

		//设置自动连续对焦
		String mode = getAutoFocusMode();
		if (StringUtil.isNotEmpty(mode)) {
			mParameters.setFocusMode(mode);
		}

		//设置人像模式，用来拍摄人物相片，如证件照。数码相机会把光圈调到最大，做出浅景深的效果。而有些相机还会使用能够表现更强肤色效果的色调、对比度或柔化效果进行拍摄，以突出人像主体。
		//		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && isSupported(mParameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_PORTRAIT))
		//			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

		if (isSupported(mParameters.getSupportedWhiteBalance(), "auto"))
			mParameters.setWhiteBalance("auto");

		//是否支持视频防抖
		if ("true".equals(mParameters.get("video-stabilization-supported")))
			mParameters.set("video-stabilization", "true");

		//		mParameters.set("recording-hint", "false");
		//
		//		mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		if (!DeviceUtil.isDevice("GT-N7100", "GT-I9308", "GT-I9300")) {
			mParameters.set("cam_mode", 1);
			mParameters.set("cam-mode", 1);
		}
	}
	
	/** 设置回调 */
	protected void setPreviewCallback() {
		Camera.Size size = mParameters.getPreviewSize();
		if (size != null) {
			PixelFormat pf = new PixelFormat();
			PixelFormat.getPixelFormatInfo(mParameters.getPreviewFormat(), pf);
			int buffSize = size.width * size.height * pf.bitsPerPixel / 8;
			try {
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.addCallbackBuffer(new byte[buffSize]);
				camera.setPreviewCallbackWithBuffer(this);
			} catch (OutOfMemoryError e) {
			}
		} else {
			camera.setPreviewCallback(this);
		}
	}
	/** 预览调用成功，子类可以做一些操作 */
	protected void onStartPreviewSuccess() {

	}

	/** 连续自动对焦 */
	private String getAutoFocusMode() {
		if (mParameters != null) {
			//持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
			List<String> focusModes = mParameters.getSupportedFocusModes();
			if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
				return "continuous-picture";
			} else if (isSupported(focusModes, "continuous-video")) {
				return "continuous-video";
			} else if (isSupported(focusModes, "auto")) {
				return "auto";
			}
		}
		return null;
	}
	
	/** 检测是否支持指定特性 */
	private boolean isSupported(List<String> list, String key) {
		return list != null && list.contains(key);
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
	}
	
	/** 切换前置/后置摄像头 */
	private void switchCamera(int cameraFacingFront) {
		switch (cameraFacingFront) {
		case Camera.CameraInfo.CAMERA_FACING_FRONT:
		case Camera.CameraInfo.CAMERA_FACING_BACK:
			mCameraId = cameraFacingFront;
			reset();
//			stopPreview();
//			startPreview();
			break;
		}
	}

	/** 切换前置/后置摄像头 */
	public void switchCamera() {
		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
			switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
		} else {
			switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		}
	}
}
