package com.android.shortvideo.recordvideo;

import com.android.shortvideo.R;
import com.android.shortvideo.common.utils.DeviceUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


@SuppressLint("NewApi")
public class FFmpegPreviewActivity extends Activity implements TextureView.SurfaceTextureListener
	,OnClickListener,OnCompletionListener{

	private String path;
	private TextureView surfaceView;
	private MediaPlayer mediaPlayer;
	private ImageView imagePlay;
	private RelativeLayout mBottomLayout;
	private int mScreenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ffmpeg_preview);

		mScreenWidth = DeviceUtil.getScreenWidth(this);
		surfaceView = (TextureView) findViewById(R.id.preview_video);
		mBottomLayout    = (RelativeLayout) findViewById(R.id.preview_video_bottom);
		RelativeLayout preview_video_parent = (RelativeLayout)findViewById(R.id.preview_video_parent);
		LayoutParams layoutParams = (LayoutParams) preview_video_parent
				.getLayoutParams();
		layoutParams.width = mScreenWidth;
		layoutParams.height = mScreenWidth * 4 / 3;
		preview_video_parent.setLayoutParams(layoutParams);
		RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams();
		layoutParams2.topMargin = mScreenWidth;
		mBottomLayout.setLayoutParams(layoutParams2);
		
		surfaceView.setSurfaceTextureListener(this);
		surfaceView.setOnClickListener(this);
		imagePlay = (ImageView) findViewById(R.id.play_preview);
		
		path = getIntent().getStringExtra("path");
		if(path != null){
			Log.e("path", path);
		}else{
			Log.e("path", "null");
		}
		
		imagePlay.setOnClickListener(this);
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	protected void onStop() {
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
			imagePlay.setVisibility(View.GONE);
		}
		super.onStop();
	}

	private void prepare(Surface surface) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置需要播放的视频
			mediaPlayer.setDataSource(path);
			// 把视频画面输出到Surface
			mediaPlayer.setSurface(surface);
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
			mediaPlayer.seekTo(0);
		} catch (Exception e) {
		}
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
			int arg2) {
		prepare(new Surface(arg0));
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.preview_video:
			if(mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				imagePlay.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.play_preview:
			if(!mediaPlayer.isPlaying()){
				mediaPlayer.start();
			}
			imagePlay.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	private void stop(){
		mediaPlayer.stop();
		Intent intent = new Intent(this,FFmpegRecorderActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		stop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		imagePlay.setVisibility(View.VISIBLE);
	}
}
