package com.android.shortvideo.recordvideo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore.Video;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.shortvideo.R;
import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.recordvideo.utils.CONSTANTS;
import com.android.shortvideo.recordvideo.utils.SavedFrames;
import com.android.shortvideo.recordvideo.utils.Util;
import com.android.shortvideo.recordvideo.widget.NewVideoSurfaceView;
import com.android.shortvideo.recordvideo.widget.NewVideoSurfaceView.OnVideoStateListener;
import com.android.shortvideo.recordvideo.widget.ProgressView;
import com.android.shortvideo.recordvideo.widget.ProgressView.State;

/**
 * 视频参数设置在RecorderParameters类里面
 */
@SuppressLint("NewApi")
public class FFmpegRecorderActivity extends Activity {

	private final static String CLASS_LABEL = "RecordActivity";
	private final static String LOG_TAG = CLASS_LABEL;
	private static final String TAG = "FFmpegRecorderActivity";

	private int mScreenWidth;

	private PowerManager.WakeLock mWakeLock;
	// 视频文件的存放地址
	private String strVideoPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "rec_video.mp4";
	// 视频文件对象
	private File fileVideoPath = null;
	// 视频文件在系统中存放的url
	private Uri uriVideoPath = null;
	// 判断是否需要录制，点击下一步时暂停录制
	private boolean rec = false;
	// 判断是否需要录制，手指按下继续，抬起时暂停
	boolean recording = false;
	// 判断是否开始了录制，第一次按下屏幕时设置为true
	boolean isRecordingStarted = false;
	// 是否开启闪光灯
	boolean isFlashOn = false;
	TextView txtTimer, txtRecordingSize;
	// 分别为闪光灯按钮、取消按钮、下一步按钮、转置摄像头按钮
	ImageView flashIcon = null, cancelBtn, switchCameraIcon = null;
	private TextView mNextText;
	boolean nextEnabled = false;

	int cameraSelection = 0;
	// 判断是否是前置摄像头
	private boolean isPreviewOn = false;

	// 音频的采样率，recorderParameters中会有默认值
	private int sampleRate = 44100;

	private Dialog dialog = null;

	boolean recordFinish = false;
	private Dialog creatingProgress;

	// 音频时间戳
	private volatile long mAudioTimestamp = 0L;
	private long mLastAudioTimestamp = 0L;
	private volatile long mAudioTimeRecorded;
	private long frameTime = 0L;
	// 每一幀的数据结构
	private SavedFrames lastSavedframe = new SavedFrames(null, 0L);
	// 视频时间戳
	private long mVideoTimestamp = 0L;
	// 时候保存过视频文件
	private boolean isRecordingSaved = false;
	private boolean isFinalizing = false;

	// 进度条
	private ProgressView progressView;
	// 捕获的第一幀的图片
	private String imagePath = null;
	private RecorderState currentRecorderState = RecorderState.PRESS;
	// private ImageView stateImageView;

	private byte[] firstData = null;
	// private byte[] bufferByte;

	private ImageView mRecordImage;

	private NewVideoSurfaceView mNewVideoSurfaceView;
	private RelativeLayout mBottomLayout;

	// neon库对opencv做了优化
	static {
		System.loadLibrary("checkneon");
	}

	public native static int checkNeonFromJNI();

	private boolean initSuccess = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recorder);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
				CLASS_LABEL);
		mWakeLock.acquire();
		initLayout();

		initListener();
	}

	private void initLayout() {
		mScreenWidth = DeviceUtil.getScreenWidth(FFmpegRecorderActivity.this);
		// stateImageView = (ImageView)
		// findViewById(R.id.recorder_surface_state);
		mRecordImage = (ImageView) findViewById(R.id.recorder_video_image);
		progressView = (ProgressView) findViewById(R.id.recorder_progress);
		mNewVideoSurfaceView = (NewVideoSurfaceView) findViewById(R.id.new_video_surface_view);
		mNewVideoSurfaceView.setActivity(FFmpegRecorderActivity.this);
		progressView.setTotalTime(6000);
		cancelBtn = (ImageView) findViewById(R.id.record_video_title_back);
		mNextText = (TextView) findViewById(R.id.recorder_next);
		// txtTimer = (TextView)findViewById(R.id.txtTimer);
		flashIcon = (ImageView) findViewById(R.id.record_video_title_toggle_flash);
		switchCameraIcon = (ImageView) findViewById(R.id.record_video_title_switch_camera);

		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FRONT)) {
			switchCameraIcon.setVisibility(View.VISIBLE);
		}

		mBottomLayout = (RelativeLayout) findViewById(R.id.recorder_bottom_layout);

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mNewVideoSurfaceView
				.getLayoutParams();
		layoutParams.height = mScreenWidth * 4 / 3;
		mNewVideoSurfaceView.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) mBottomLayout
				.getLayoutParams();
		layoutParams2.topMargin = mScreenWidth;
		mBottomLayout.setLayoutParams(layoutParams2);

	}

	private void initListener() {

		switchCameraIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 转换摄像头
				cameraSelection = ((cameraSelection == CameraInfo.CAMERA_FACING_BACK) ? CameraInfo.CAMERA_FACING_FRONT
						: CameraInfo.CAMERA_FACING_BACK);
				mNewVideoSurfaceView = (NewVideoSurfaceView) findViewById(R.id.new_video_surface_view);
				mNewVideoSurfaceView.setActivity(FFmpegRecorderActivity.this);
				mNewVideoSurfaceView.setCamera(cameraSelection);
				mNewVideoSurfaceView.initCameraLayout();

				if (cameraSelection == CameraInfo.CAMERA_FACING_FRONT)
					flashIcon.setVisibility(View.GONE);
				else {
					flashIcon.setVisibility(View.VISIBLE);
					if (isFlashOn) {
						mNewVideoSurfaceView
								.setFlashMode(Parameters.FLASH_MODE_TORCH);
					}
				}
			}
		});
		flashIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_CAMERA_FLASH)) {
					// showToast(this, "不能开启闪光灯");
					return;
				}
				// 闪光灯
				if (isFlashOn) {
					isFlashOn = false;
					flashIcon.setSelected(false);
					mNewVideoSurfaceView
							.setFlashMode(Parameters.FLASH_MODE_OFF);
				} else {
					isFlashOn = true;
					flashIcon.setSelected(true);
					mNewVideoSurfaceView
							.setFlashMode(Parameters.FLASH_MODE_TORCH);
				}
			}
		});

		mNextText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isRecordingStarted) {
					rec = false;
					saveRecording();
				} else
					initiateRecording(false);
			}
		});

		mRecordImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (!recordFinish) {
					if (mNewVideoSurfaceView.isWithinMaxTime()) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							isRecordingStarted = true;
							mNewVideoSurfaceView.start();
							break;
						case MotionEvent.ACTION_UP:

							Log.e(TAG, "ACTION_UP");
							mNewVideoSurfaceView.pauseRecord();
							if (rec) {
							}
							break;
						}
					} else {
						// 如果录制时间超过最大时间，保存视频
						rec = false;
						saveRecording();
					}
				}
				return true;
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (recording)
					showCancellDialog();
				else
					videoTheEnd(false);
			}
		});

		mNewVideoSurfaceView
				.setOnVideoStateListener(new OnVideoStateListener() {

					@Override
					public void onStop() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						progressView.setCurrentState(State.START);
					}

					@Override
					public void onPause(long totalTime) {
						// TODO Auto-generated method stub
						progressView.setCurrentState(State.PAUSE);
						// 将暂停的时间戳添加到进度条的队列中
						progressView.putProgressList((int) totalTime);
					}

					@Override
					public boolean canStop() {
						// TODO Auto-generated method stub
						mNextText.setEnabled(true);
						return true;
					}
				});
	}

	/**
	 * 放弃视频时弹出框
	 */
	private void showCancellDialog() {
		Util.showDialog(FFmpegRecorderActivity.this, "提示", "确定要放弃本视频吗？", 2,
				new Handler() {
					@Override
					public void dispatchMessage(Message msg) {
						if (msg.what == 1)
							videoTheEnd(false);
					}
				});
	}

	@Override
	public void onBackPressed() {
		if (recording)
			showCancellDialog();
		else
			videoTheEnd(false);
	}

	/**
	 * 结束录制
	 * 
	 * @param isSuccess
	 */
	public void videoTheEnd(boolean isSuccess) {
		releaseResources();
		if (fileVideoPath != null && fileVideoPath.exists() && !isSuccess)
			fileVideoPath.delete();

		returnToCaller(isSuccess);
	}

	/**
	 * 设置返回结果
	 * 
	 * @param valid
	 */
	private void returnToCaller(boolean valid) {
		try {
			setActivityResult(valid);
			if (valid) {
				Intent intent = new Intent(this, FFmpegPreviewActivity.class);
				intent.putExtra("path", strVideoPath);
				intent.putExtra("imagePath", imagePath);
				startActivity(intent);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			finish();
		}
	}

	private void setActivityResult(boolean valid) {
		Intent resultIntent = new Intent();
		int resultCode;
		if (valid) {
			resultCode = RESULT_OK;
			resultIntent.setData(uriVideoPath);
		} else
			resultCode = RESULT_CANCELED;

		setResult(resultCode, resultIntent);
	}

	/**
	 * 向系统注册我们录制的视频文件，这样文件才会在sd卡中显示
	 */
	private void registerVideo() {
		Uri videoTable = Uri.parse(CONSTANTS.VIDEO_CONTENT_URI);

		Util.videoContentValues.put(Video.Media.SIZE,
				new File(strVideoPath).length());
		try {
			uriVideoPath = getContentResolver().insert(videoTable,
					Util.videoContentValues);
		} catch (Throwable e) {
			uriVideoPath = null;
			strVideoPath = null;
			e.printStackTrace();
		} finally {
		}
		Util.videoContentValues = null;
	}

	/**
	 * 保存录制的视频文件
	 */
	private void saveRecording() {
		if (isRecordingStarted) {
			// runAudioThread = false;
			if (!isRecordingSaved) {
				isRecordingSaved = true;
				new AsyncStopRecording().execute();
			}
		} else {
			videoTheEnd(false);
		}
	}

	/**
	 * 停止录制
	 * 
	 * @author QD
	 * 
	 */
	public class AsyncStopRecording extends AsyncTask<Void, Integer, Void> {

		private ProgressBar bar;
		private TextView progress;

		@Override
		protected void onPreExecute() {
			isFinalizing = true;
			recordFinish = true;
			// runAudioThread = false;

			// 创建处理进度条
			creatingProgress = new Dialog(FFmpegRecorderActivity.this,
					R.style.Dialog_loading_noDim);
			Window dialogWindow = creatingProgress.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = (int) (getResources().getDisplayMetrics().density * 240);
			lp.height = (int) (getResources().getDisplayMetrics().density * 80);
			lp.gravity = Gravity.CENTER;
			dialogWindow.setAttributes(lp);
			creatingProgress.setCanceledOnTouchOutside(false);
			creatingProgress
					.setContentView(R.layout.activity_recorder_progress);

			progress = (TextView) creatingProgress
					.findViewById(R.id.recorder_progress_progresstext);
			bar = (ProgressBar) creatingProgress
					.findViewById(R.id.recorder_progress_progressbar);
			creatingProgress.show();

			// txtTimer.setVisibility(View.INVISIBLE);
			// handler.removeCallbacks(mUpdateTimeTask);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progress.setText(values[0] + "%");
			bar.setProgress(values[0]);
		}

		/**
		 * 依据byte[]里的数据合成一张bitmap， 截成480*480，并且旋转90度后，保存到文件
		 * 
		 * @param data
		 */
		private void getFirstCapture(byte[] data) {

			String captureBitmapPath = CONSTANTS.CAMERA_FOLDER_PATH;

			captureBitmapPath = Util
					.createImagePath(FFmpegRecorderActivity.this);
			YuvImage localYuvImage = new YuvImage(data, 17, 480, 480, null);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			FileOutputStream outStream = null;

			try {
				File file = new File(captureBitmapPath);
				if (!file.exists())
					file.createNewFile();
				localYuvImage
						.compressToJpeg(new Rect(0, 0, 480, 480), 100, bos);
				Bitmap localBitmap1 = BitmapFactory.decodeByteArray(
						bos.toByteArray(), 0, bos.toByteArray().length);

				bos.close();

				Matrix localMatrix = new Matrix();
				if (cameraSelection == 0)
					localMatrix.setRotate(90.0F);
				else
					localMatrix.setRotate(270.0F);

				Bitmap localBitmap2 = Bitmap.createBitmap(localBitmap1, 0, 0,
						localBitmap1.getHeight(), localBitmap1.getHeight(),
						localMatrix, true);

				ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
				localBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, bos2);

				outStream = new FileOutputStream(captureBitmapPath);
				outStream.write(bos2.toByteArray());
				outStream.close();

				localBitmap1.recycle();
				localBitmap2.recycle();

				// isFirstFrame = false;
				imagePath = captureBitmapPath;
			} catch (FileNotFoundException e) {
				// isFirstFrame = true;
				e.printStackTrace();
			} catch (IOException e) {
				// isFirstFrame = true;
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (firstData != null)
				getFirstCapture(firstData);

			// recorderThread.stopRecord(this);
			mNewVideoSurfaceView.stopRecordThread(this);
			isFinalizing = false;
			if (recording) {
				recording = false;
				releaseResources();
			}
			publishProgress(100);
			return null;
		}

		public void publishProgressFromOther(int progress) {
			publishProgress(progress);
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!isFinishing()) {
				creatingProgress.dismiss();
			}
			registerVideo();
			returnToCaller(true);
			mNewVideoSurfaceView.setRecorder();
		}

	}

	/**
	 * 求出录制的总时间
	 * 
	 * private synchronized void setTotalVideoTime(){ if(totalTime > 0)
	 * txtTimer.setText(Util.getRecordingTimeFromMillis(totalTime));
	 * 
	 * }
	 */

	/**
	 * 释放资源，停止录制视频和音频
	 */
	private void releaseResources() {
		isRecordingSaved = true;
		lastSavedframe = null;

		mNewVideoSurfaceView.releaseResources();
		// progressView.putProgressList((int) totalTime);
		// 停止刷新进度
		progressView.setCurrentState(State.PAUSE);
	}

	/**
	 * 第一次按下时，初始化录制数据
	 * 
	 * @param isActionDown
	 */
	private void initiateRecording(boolean isActionDown) {
		isRecordingStarted = true;
		mNewVideoSurfaceView.initiateRecording(isActionDown);
	}

	public enum RecorderState {
		PRESS(1), LOOSEN(2), CHANGE(3), SUCCESS(4);

		static RecorderState mapIntToValue(final int stateInt) {
			for (RecorderState value : RecorderState.values()) {
				if (stateInt == value.getIntValue()) {
					return value;
				}
			}
			return PRESS;
		}

		private int mIntValue;

		RecorderState(int intValue) {
			mIntValue = intValue;
		}

		int getIntValue() {
			return mIntValue;
		}
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// if(!initSuccess)
	// return false;
	// return super.dispatchTouchEvent(ev);
	// }

	@Override
	protected void onResume() {
		super.onResume();

		if (mWakeLock == null) {
			// 获取唤醒锁,保持屏幕常亮
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					CLASS_LABEL);
			mWakeLock.acquire();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isFinalizing)
			finish();

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Log.i("video", this.getLocalClassName()+"—destory");
		recording = false;
		// runAudioThread = false;
		mNewVideoSurfaceView.destroy();
		firstData = null;
		mNewVideoSurfaceView = null;
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
}