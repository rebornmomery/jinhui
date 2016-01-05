package com.android.shortvideo.recordvideo.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.shortvideo.common.utils.DeviceUtil;


import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;

@SuppressLint("NewApi")
public class BaseMediaRecorder {

	//录制时长
    public static final int TIME_LENGTH = 10000;
	
    private MediaRecorder mMediarecorder;// 录制视频的类 
	
    //保存在10秒内录制的视频片断，如果在10秒内录制到中途暂停的视频,最后在10秒结束后会对视频片断进行合成
    private static List<String> mVideoList = new ArrayList<String>();
    
    public static final String parentPath = PathUtil.getCacheDir()+ "/video";
    
    
	/**
	 * 开始录制视频
	 */
	public void startRecord(Surface surface, Camera camera){
		if(mMediarecorder == null){
			mMediarecorder = new MediaRecorder();// 创建mediarecorder对象
		}
		else{
			mMediarecorder.reset();
		}
		
		
		if(camera != null){
			
			// Step 1: Unlock and set camera to MediaRecorder
			camera.unlock();
			
			mMediarecorder.setCamera(camera);
			mMediarecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			
			// 设置录制视频源为Camera(相机)  
			mMediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
			
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4  
			mMediarecorder  
			.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
			
			mMediarecorder.setAudioEncoder(MediaRecorder.AudioSource.DEFAULT);
			
			// 设置录制的视频编码h263 h264  
			mMediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  
			
			mMediarecorder.setMaxDuration(TIME_LENGTH);
			mMediarecorder.setOrientationHint(270);
			//设置视频输出的格式和编码
			CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
			//                mMediaRecorder.setProfile(mProfile);
			
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
			mMediarecorder.setVideoSize(640, 480);  
			
			mMediarecorder.setAudioEncodingBitRate(44100);
			if (mProfile.videoBitRate > 2 * 1024 * 1024)
				mMediarecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
			else
				mMediarecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
			mMediarecorder.setVideoFrameRate(mProfile.videoFrameRate);//after setVideoSource(),after setOutFormat()
			
//		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
//		mediarecorder.setVideoFrameRate(25);  
			mMediarecorder.setPreviewDisplay(surface);
		}

		// Set output file path   
		String absolutePath;// = DeviceUtil.getSDPath();  
		if (parentPath != null) {  
			
			//创建临时文件夹保存视频
			File dir = new File(parentPath);  
			if (!dir.exists()) {
				try {
					
					dir.mkdir();  
				} catch (Exception e) {
					// TODO: handle exception
				}
			}  
			absolutePath = dir + "/" + DeviceUtil.getDate() + ".mp4";  
			
			// 设置视频文件输出的路径  
			mMediarecorder.setOutputFile(absolutePath);  
			try {  
				// 准备录制  
				mMediarecorder.prepare();  
				
				//开始录制
				mMediarecorder.start();
				Log.e("record", "开始录制");
				
				mVideoList.add(absolutePath);//DeviceUtils.getSDPath()+ "/mp4"+ "/"+DeviceUtils.getDate() + ".mp4");
				Log.e("absolutePath", absolutePath);
				
			} catch (IllegalStateException e) {  
				// TODO Auto-generated catch block  
				e.printStackTrace();  
			} catch (IOException e) {  
				// TODO Auto-generated catch block  
				e.printStackTrace();  
			}  
		}
		
	}
	
	/**
	 * 暂停录制
	 */
	public void pauseRecord(){
		if (mMediarecorder != null) {
			
			// 停止录制  
            mMediarecorder.stop();
            
        }
	}
	
	/**
	 * 结束录制
	 */
	public void stopRecord(){
		if (mMediarecorder != null) {  
            // 停止录制  
//            mMediarecorder.stop();  
//			mMediarecorder.reset();
            // 释放资源  
            mMediarecorder.release();
            mMediarecorder = null;  
            
            mergeVideo();
        }
	}
	
	/**
	 * 合成视频
	 */
	public static void mergeVideo(){
		
      if(mVideoList.size() >= 2){
    	MediaRecorderUtil.mergeVideo(mVideoList);
      }
      
      mVideoList.clear();
      
	}
	
	public static List<String> getVideoList(){
		return mVideoList;
	}
	
}
