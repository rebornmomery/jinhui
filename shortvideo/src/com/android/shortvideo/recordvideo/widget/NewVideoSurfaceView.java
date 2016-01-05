/**
 * 
 */
package com.android.shortvideo.recordvideo.widget;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.shortvideo.recordvideo.FFmpegRecorderActivity;
import com.android.shortvideo.recordvideo.FFmpegRecorderActivity.AsyncStopRecording;
import com.android.shortvideo.recordvideo.FFmpegRecorderActivity.RecorderState;
import com.android.shortvideo.recordvideo.utils.CONSTANTS;
import com.android.shortvideo.recordvideo.utils.NewFFmpegFrameRecorder;
import com.android.shortvideo.recordvideo.utils.RecorderParameters;
import com.android.shortvideo.recordvideo.utils.RecorderThread;
import com.android.shortvideo.recordvideo.utils.SavedFrames;
import com.android.shortvideo.recordvideo.utils.Util;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * @author YeGuangRong
 *
 */
@SuppressLint("NewApi")
public class NewVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback{

	private SurfaceHolder mHolder;

	//视频文件的存放地址
	private String strVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "rec_video.mp4";
	//视频文件对象
	private File fileVideoPath = null;
	//视频文件在系统中存放的url
	private Uri uriVideoPath = null;
	private Camera mCamera;
	//判断是否是前置摄像头
	private boolean isPreviewOn = false;
	Parameters cameraParameters = null;
	
	//音频的采样率，recorderParameters中会有默认值
	private int sampleRate = 44100;
	//预览的宽高和屏幕宽高
	private int previewWidth = 480, screenWidth = 480;
	private int previewHeight = 480, screenHeight = 800;
	//音频时间戳
	private volatile long mAudioTimestamp = 0L;
	private long mLastAudioTimestamp = 0L;
	private volatile long mAudioTimeRecorded;
	private long frameTime = 0L;
	private IplImage yuvIplImage = null;
	//第一次按下屏幕时记录的时间
	long firstTime = 0;
	//手指抬起是的时间
	long startPauseTime = 0;
	//每次按下手指和抬起之间的暂停时间
	long totalPauseTime = 0;
	//手指抬起是的时间
	long pausedTime = 0;
	//总的暂停时间
	long stopPauseTime = 0;
	//录制的有效总时间
	long mTotalTime = 0;
	//视频帧率
	private int frameRate = 30;
	//录制的最长时间
	private int recordingTime = 6000;
	//录制的最短时间
	private int recordingMinimumTime = 6000;
	//提示换个场景
	private int recordingChangeTime = 3000;
	//当前录制的质量，会影响视频清晰度和文件大小
	private int currentResolution = CONSTANTS.RESOLUTION_MEDIUM_VALUE;

	//判断是否需要录制，手指按下继续，抬起时暂停
	boolean recording = false;
	//判断是否开始了录制，第一次按下屏幕时设置为true
	boolean	isRecordingStarted = false;
	//判断是否需要录制，点击下一步时暂停录制
	private boolean rec = false;
	//每一幀的数据结构
	private SavedFrames lastSavedframe = new SavedFrames(null,0L);
	//获取第一幀的图片
	private boolean isFirstFrame = true;
	private byte[] firstData = null;
	private byte[] bufferByte;
	boolean nextEnabled = false;
	private RecorderState currentRecorderState = RecorderState.PRESS;
	//录制视频和保存音频的类
	private volatile NewFFmpegFrameRecorder videoRecorder;
	//调用系统的录制音频类
	private AudioRecord audioRecord; 
	//录制音频的线程
	private AudioRecordRunnable audioRecordRunnable;
	private Thread audioThread;
	private RecorderThread recorderThread;
	//开启和停止录制音频的标记
	volatile boolean runAudioThread = true;
	//视频时间戳
	private long mVideoTimestamp = 0L;
	//分别为 默认摄像头（后置）、默认调用摄像头的分辨率、被选择的摄像头（前置或者后置）
	int defaultCameraId = -1, defaultScreenResolution = -1 , mCameraSelection = 0;
	
	private Context mContext;
	private Activity mActivity;
	private Handler mHandler;
	
	private boolean initSuccess = false;
	//摄像头以及它的参数
	private Camera cameraDevice;

	//时候保存过视频文件
	private boolean isRecordingSaved = false;
	
	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NewVideoSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @param context
	 * @param attrs
	 */
	public NewVideoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setActivity(Activity activity){
		this.mActivity = activity;
		init(activity);
	}

	
	public void init(Context context){
		mContext = context;
		initHandler();
		setCamera(0);
		mCamera = cameraDevice;
		cameraParameters = mCamera.getParameters();
		mHolder = getHolder();
		mHolder.addCallback(NewVideoSurfaceView.this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mCamera.setPreviewCallbackWithBuffer(NewVideoSurfaceView.this)    ;
		initCameraLayout();
	}
	
	public void initCameraLayout(){
		if(!initSuccess){
			initVideoRecorder();
			startRecording();
			
			initSuccess = true;
		}
		if(cameraDevice == null){
			return;
		}
		
		handleSurfaceChanged();
		if(recorderThread == null) {
			recorderThread = new RecorderThread(yuvIplImage, videoRecorder, previewHeight * previewWidth * 3 / 2,frameRate*(recordingTime/1000));
			recorderThread.start();
		}
		//设置surface的宽高
		RelativeLayout.LayoutParams layoutParam1 = new RelativeLayout.LayoutParams(screenWidth,(int) (screenWidth*(previewWidth/(previewHeight*1f))));
		layoutParam1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
	}
	public void setCamera(int cameraSelection)
	{
		mCameraSelection = cameraSelection;
		try
		{
			if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
			{
				int numberOfCameras = Camera.getNumberOfCameras();
				
				CameraInfo cameraInfo = new CameraInfo();
				for (int i = 0; i < numberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (cameraInfo.facing == mCameraSelection) {
						defaultCameraId = i;
					}
				}
			}
			stopPreview();
			if(mCamera != null)
				mCamera.release();
			
			if(defaultCameraId >= 0)
				cameraDevice = Camera.open(defaultCameraId);
			else
				cameraDevice = Camera.open();

		}
		catch(Exception e)
		{	
		}
	}
	private void initVideoRecorder() {
		strVideoPath = Util.createFinalPath(mContext);//Util.createTempPath(tempFolderPath);
		
		RecorderParameters recorderParameters = Util.getRecorderParameter(currentResolution);
		sampleRate = recorderParameters.getAudioSamplingRate();
		frameRate = recorderParameters.getVideoFrameRate();
		frameTime = (1000000L / frameRate);
		
		fileVideoPath = new File(strVideoPath); 
		videoRecorder = new NewFFmpegFrameRecorder(strVideoPath, 480, 480, 1);
		videoRecorder.setFormat(recorderParameters.getVideoOutputFormat());
		videoRecorder.setSampleRate(recorderParameters.getAudioSamplingRate());
		videoRecorder.setFrameRate(recorderParameters.getVideoFrameRate());
		videoRecorder.setVideoCodec(recorderParameters.getVideoCodec());
		videoRecorder.setVideoQuality(recorderParameters.getVideoQuality()); 
		videoRecorder.setAudioQuality(recorderParameters.getVideoQuality());
		videoRecorder.setAudioCodec(recorderParameters.getAudioCodec());
		videoRecorder.setVideoBitrate(recorderParameters.getVideoBitrate());
		videoRecorder.setAudioBitrate(recorderParameters.getAudioBitrate());
		
		audioRecordRunnable = new AudioRecordRunnable();
		audioThread = new Thread(audioRecordRunnable);
	}

	public void startRecording() {

		try {
			videoRecorder.start();
			audioThread.start();

		} catch (NewFFmpegFrameRecorder.Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			stopPreview();
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder  holder, int format, int width, int height) {
		if (isPreviewOn)
			mCamera.stopPreview();
		handleSurfaceChanged();
		startPreview();  
		mCamera.autoFocus(null);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			mHolder.addCallback(null);
			mCamera.setPreviewCallback(null);
			
		} catch (RuntimeException e) {
		}
	}

	public void startPreview() {
		if (!isPreviewOn && mCamera != null) {
			isPreviewOn = true;
			mCamera.startPreview();
		}
	}

	public void stopPreview() {
		if (isPreviewOn && mCamera != null) {
			isPreviewOn = false;
			mCamera.stopPreview();
		}
	}
	private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight)
	{

		final byte [] yuv = new byte[previewWidth*previewHeight*3/2];
		// Rotate the Y luma
		int i = 0;
		for(int x = 0;x < imageWidth;x++)
		{
			for(int y = imageHeight-1;y >= 0;y--)
			{
				yuv[i] = data[y*imageWidth+x];
				i++;
			}

		}
		// Rotate the U and V color components
		i = imageWidth*imageHeight*3/2-1;
		for(int x = imageWidth-1;x > 0;x=x-2)
		{
			for(int y = 0;y < imageHeight/2;y++)
			{
				yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+x];
				i--;
				yuv[i] = data[(imageWidth*imageHeight)+(y*imageWidth)+(x-1)];
				i--;
			}
		}
		return yuv;
	}

	private byte[] rotateYUV420Degree180(byte[] data, int imageWidth, int imageHeight)
	{
		byte [] yuv = new byte[imageWidth*imageHeight*3/2];
		int i = 0;
		int count = 0;

		for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
			yuv[count] = data[i];
			count++;
		}

		i = imageWidth * imageHeight * 3 / 2 - 1;
		for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
				* imageHeight; i -= 2) {
			yuv[count++] = data[i - 1];
			yuv[count++] = data[i];
		}
		return yuv;
	}

	private byte[] rotateYUV420Degree270(byte[] data, int imageWidth, int imageHeight)
	{
		final byte [] yuv = new byte[previewWidth*previewHeight*3/2];
		int wh = 0;
		int uvHeight = 0;
		if(imageWidth != 0 || imageHeight != 0)
		{
			wh = imageWidth * imageHeight;
			uvHeight = imageHeight >> 1;//uvHeight = height / 2
		}

		//旋转Y
		int k = 0;
		for(int i = 0; i < imageWidth; i++) {
			int nPos = 0;
			for(int j = 0; j < imageHeight; j++) {
				yuv[k] = data[nPos + i];
				k++;
				nPos += imageWidth;
			}
		}

		for(int i = 0; i < imageWidth; i+=2){
			int nPos = wh;
			for(int j = 0; j < uvHeight; j++) {
				yuv[k] = data[nPos + i];
				yuv[k + 1] = data[nPos + i + 1];
				k += 2;
				nPos += imageWidth;
			}
		}
		return rotateYUV420Degree180(yuv,imageWidth,imageHeight);
	}

	public byte[] cropYUV420(byte[] data,int imageW,int imageH,int newImageH){
		int cropH;
		int i,j,count,tmp;
		byte[] yuv = new byte[imageW*newImageH*3/2];

		cropH = (imageH - newImageH)/2;

		count = 0;
		for(j=cropH;j<cropH+newImageH;j++){
			for(i=0;i<imageW;i++){
				yuv[count++] = data[j*imageW+i];
			}
		}

		//Cr Cb
		tmp = imageH+cropH/2;
		for(j=tmp;j<tmp + newImageH/2;j++){
			for(i=0;i<imageW;i++){
				yuv[count++] = data[j*imageW+i];
			}
		}

		return yuv;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		//计算时间戳
		long frameTimeStamp = 0L;
		if(mAudioTimestamp == 0L && firstTime > 0L) {
			frameTimeStamp = 1000L * (System.currentTimeMillis() - firstTime);
		}else if (mLastAudioTimestamp == mAudioTimestamp) {
			frameTimeStamp = mAudioTimestamp + frameTime;
		}else{
			long l2 = (System.nanoTime() - mAudioTimeRecorded) / 1000L;
			frameTimeStamp = l2 + mAudioTimestamp;
			mLastAudioTimestamp = mAudioTimestamp;
		}

		//录制视频
		if (recording && rec){
			if(lastSavedframe != null
				&& lastSavedframe.getFrameBytesData() != null && yuvIplImage != null) {
				//保存某一幀的图片
				if (isFirstFrame) {
					isFirstFrame = false;
					firstData = data;
				}
				//超过最低时间时，下一步按钮可点击
				mTotalTime = System.currentTimeMillis() - firstTime - pausedTime - ((long) (1.0 / (double) frameRate) * 1000);
				if (!nextEnabled && mTotalTime >= recordingChangeTime) {
					nextEnabled = true;
					if(mOnVideoStateListener != null){
						mOnVideoStateListener.canStop();
					}
//					mNextText.setEnabled(true);
				}

				if (nextEnabled && mTotalTime >= recordingMinimumTime) {
					mHandler.sendEmptyMessage(5);
				}

				if (currentRecorderState == RecorderState.PRESS && mTotalTime >= recordingChangeTime) {
					currentRecorderState = RecorderState.LOOSEN;
					mHandler.sendEmptyMessage(2);
				}

				mVideoTimestamp += frameTime;
				if (lastSavedframe.getTimeStamp() > mVideoTimestamp) {
					Log.e("", "mVideoTimestamp");
					mVideoTimestamp = lastSavedframe.getTimeStamp();
				}

				recorderThread.putByteData(lastSavedframe);
			}
			byte[] tempData = rotateYUV420Degree90(data, previewWidth, previewHeight);
			if(mCameraSelection == 1)
				tempData = rotateYUV420Degree270(data, previewWidth, previewHeight);
			lastSavedframe = new SavedFrames(tempData,frameTimeStamp);
		}

		mCamera.addCallbackBuffer(bufferByte);
	}
	
	private void handleSurfaceChanged()
	{
		if(mCamera == null){
			//showToast(this, "无法连接到相机");
//			finish();
			return;
		}
		
		//获取摄像头的所有支持的分辨率
		List<Camera.Size> resolutionList = Util.getResolutionList(mCamera);
		if(resolutionList != null && resolutionList.size() > 0){
			Collections.sort(resolutionList, new Util.ResolutionComparator());
			Camera.Size previewSize =  null;	
			if(defaultScreenResolution == -1){
				boolean hasSize = false;
				//如果摄像头支持640*480，那么强制设为640*480
				for(int i = 0;i<resolutionList.size();i++){
					Size size = resolutionList.get(i);
					if(size != null && size.width==640 && size.height==480){
						previewSize = size;
						hasSize = true;
						break;
					}
				}
				//如果不支持设为中间的那个
				if(!hasSize){
					int mediumResolution = resolutionList.size()/2;
					if(mediumResolution >= resolutionList.size())
						mediumResolution = resolutionList.size() - 1;
					previewSize = resolutionList.get(mediumResolution);
				}
			}else{
				if(defaultScreenResolution >= resolutionList.size())
					defaultScreenResolution = resolutionList.size() - 1;
				previewSize = resolutionList.get(defaultScreenResolution);
			}
			//获取计算过的摄像头分辨率
			if(previewSize != null ){
				previewWidth = previewSize.width;
				previewHeight = previewSize.height;
				cameraParameters.setPreviewSize(previewWidth, previewHeight);
				if(videoRecorder != null)
				{
					videoRecorder.setImageWidth(previewWidth);
					videoRecorder.setImageHeight(previewHeight);
				}

			}
		}

		bufferByte = new byte[previewWidth*previewHeight*3/2];

		mCamera.addCallbackBuffer(bufferByte);

		//设置预览帧率
		cameraParameters.setPreviewFrameRate(frameRate);
		//构建一个IplImage对象，用于录制视频
		//和opencv中的cvCreateImage方法一样
		yuvIplImage = IplImage.create(previewHeight, previewWidth,IPL_DEPTH_8U, 2);

		//系统版本为8一下的不支持这种对焦
		if(Build.VERSION.SDK_INT >  Build.VERSION_CODES.FROYO)
		{
			mCamera.setDisplayOrientation(Util.determineDisplayOrientation(mActivity, defaultCameraId));
			List<String> focusModes = cameraParameters.getSupportedFocusModes();
			if(focusModes != null){
				Log.i("video", Build.MODEL);
				 if (((Build.MODEL.startsWith("GT-I950"))
						 || (Build.MODEL.endsWith("SCH-I959"))
						 || (Build.MODEL.endsWith("MEIZU MX3")))&&focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
						
					 cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
				 }else if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
					cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				}else
					cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
			}
		}
		else
			mCamera.setDisplayOrientation(90);
		mCamera.setParameters(cameraParameters);

	}

	private void initHandler(){
		mHandler = new Handler(){
			@Override
			public void dispatchMessage(Message msg) {
				switch (msg.what) {
				case 3:
					
					if(!recording)
						initiateRecording(true);
					else{
						//更新暂停的时间
						stopPauseTime = System.currentTimeMillis();
						totalPauseTime = stopPauseTime - startPauseTime - ((long) (1.0/(double)frameRate)*1000);
						pausedTime += totalPauseTime;
					}
					rec = true;
					if(mOnVideoStateListener != null){
						//开始进度条增长
//						progressView.setCurrentState(State.START);
						mOnVideoStateListener.onStart();
					}
				break;
				case 4:
					if(mOnVideoStateListener != null){
						mOnVideoStateListener.onPause(mTotalTime);
						//设置进度条暂停状态
//						progressView.setCurrentState(State.PAUSE);
						//将暂停的时间戳添加到进度条的队列中
//						progressView.putProgressList((int) mTotalTime);
					}
					rec = false;
					startPauseTime = System.currentTimeMillis();
					if(mTotalTime >= recordingMinimumTime){
						currentRecorderState = RecorderState.SUCCESS;
						mHandler.sendEmptyMessage(2);
					}else if(mTotalTime >= recordingChangeTime){
						currentRecorderState = RecorderState.CHANGE;
						mHandler.sendEmptyMessage(2);
					}
					break;
				case 5:
					currentRecorderState = RecorderState.SUCCESS;
					mHandler.sendEmptyMessage(2);
					break;
				default:
					break;
				}
			}
		};
	}
	/**
	 * 录制音频的线程
	 * @author QD
	 *
	 */
	class AudioRecordRunnable implements Runnable {
		
		int bufferSize;
		short[] audioData;
		int bufferReadResult;
		private final AudioRecord audioRecord;
		public volatile boolean isInitialized;
		private int mCount =0;
		private AudioRecordRunnable()
		{
			bufferSize = AudioRecord.getMinBufferSize(sampleRate, 
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, 
					AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,bufferSize);
			audioData = new short[bufferSize];
		}

		/**
		 * shortBuffer包含了音频的数据和起始位置
		 * @param shortBuffer
		 */
		private void record(ShortBuffer shortBuffer)
		{
			try{
				if (videoRecorder != null)
				{
					this.mCount += shortBuffer.limit();
					videoRecorder.record(0,new Buffer[] {shortBuffer});
				}
			}catch (FrameRecorder.Exception localException){

			}
				return;
		}
		
		/**
		 * 更新音频的时间戳
		 */
		private void updateTimestamp()
		{
			if (videoRecorder != null)
			{
				int i = Util.getTimeStampInNsFromSampleCounted(this.mCount);
				if (mAudioTimestamp != i)
				{
					mAudioTimestamp = i;
					mAudioTimeRecorded =  System.nanoTime();
				}
			}
		}

		public void run()
		{
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			this.isInitialized = false;
			if(audioRecord != null)
			{
				//判断音频录制是否被初始化
				while (this.audioRecord.getState() == 0)
				{
					try
					{
						Thread.sleep(100L);
					}
					catch (InterruptedException localInterruptedException)
					{
					}
				}
				this.isInitialized = true;
				this.audioRecord.startRecording();
				while (((runAudioThread) || (mVideoTimestamp > mAudioTimestamp)) && (mAudioTimestamp < (1000 * recordingTime)))
				{
					updateTimestamp();
					bufferReadResult = this.audioRecord.read(audioData, 0, audioData.length);
					if ((bufferReadResult > 0) && ((recording && rec) || (mVideoTimestamp > mAudioTimestamp)))
						record(ShortBuffer.wrap(audioData, 0, bufferReadResult));
				}
				this.audioRecord.stop();
				this.audioRecord.release();
			}
		}
	}
	
	/**
	 * 第一次按下时，初始化录制数据
	 * @param isActionDown
	 */
	public void initiateRecording(boolean isActionDown)
	{
		isRecordingStarted = true;
		firstTime = System.currentTimeMillis();
	
		recording = true;
		totalPauseTime = 0;
		pausedTime = 0;
	}
	
	public void setFlashMode(String mode){
		cameraParameters.setFlashMode(mode);
		mCamera.setParameters(cameraParameters);
	}
	
	/**
	 * 释放资源，停止录制视频和音频
	 */
	public void releaseResources(){
		recorderThread.finish();
		isRecordingSaved = true;
		try {
			if(videoRecorder != null)
			{
			videoRecorder.stop();
			videoRecorder.release();
			}
		} catch (com.googlecode.javacv.FrameRecorder.Exception e) {
			e.printStackTrace();
		}
		
		yuvIplImage = null;
		videoRecorder = null;
		lastSavedframe = null;
		
		//progressView.putProgressList((int) totalTime);
		//停止刷新进度
//		progressView.setCurrentState(State.PAUSE);
	}
	
	public void stopRecordThread(AsyncStopRecording stopRecording){
		recorderThread.stopRecord(stopRecording);
	}
	
	public void setRecorder(){
		videoRecorder = null;
	}
	
	public void destroy(){
		runAudioThread = false;
		releaseResources();
		stopPreview();
		if(cameraDevice != null){
			cameraDevice.setPreviewCallback(null);
			cameraDevice.release();
		}
		cameraDevice = null;
		firstData = null;
		mCamera = null;
		
	}
	
	public void start(){
		//如果MediaRecorder没有被初始化
		//执行初始化
		mHandler.removeMessages(3);
		mHandler.removeMessages(4);
		mHandler.sendEmptyMessageDelayed(3,300);
	}
	
	public void pauseRecord(){
		mHandler.removeMessages(3);
		mHandler.removeMessages(4);
		mHandler.sendEmptyMessage(4);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isWithinMaxTime(){
		return mTotalTime < recordingTime ? true : false;
	}
	
	private OnVideoStateListener mOnVideoStateListener;
	
	public void setOnVideoStateListener(OnVideoStateListener onVideoStateListener){
		this.mOnVideoStateListener = onVideoStateListener;
	}
	
	public interface OnVideoStateListener{
		public void onStart();
		public void onPause(long totalTime);
		public boolean canStop();
		public void onStop();
	}

}

