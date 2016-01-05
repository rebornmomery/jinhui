package com.android.shortvideo.recordvideo.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import android.media.MediaRecorder;

import com.android.shortvideo.common.utils.DeviceUtil;
import com.android.shortvideo.common.utils.FileUtil;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class MediaRecorderUtil {

	/**
	 * ƴ�Ӷ��mp4��ʽ����Ƶ
	 * @param videoList
	 */
	public static String mFinalPath; 
	
	public static void mergeVideo(List<String> videoList){
		
		List<Movie> moviesList = new LinkedList<Movie>();

		try
		{
		    for (String file : videoList)
		    {
		        moviesList.add(MovieCreator.build(file));
		    }
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
		         
		List<Track> videoTracks = new LinkedList<Track>();
		List<Track> audioTracks = new LinkedList<Track>();
		for (Movie m : moviesList)
		{
		    for (Track t : m.getTracks())
		    {
		        if (t.getHandler().equals("soun"))
		        {
		            audioTracks.add(t);
		        }
		        if (t.getHandler().equals("vide"))
		        {
		            videoTracks.add(t);
		        }
		    }
		}
		 
		Movie result = new Movie();
		 
		try
		{
		    if (audioTracks.size() > 0)
		    {
		        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
		    }
		    if (videoTracks.size() > 0)
		    {
		        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
		    }
		}
		catch (IOException e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		 
		Container out = new DefaultMp4Builder().build(result);
		mFinalPath = PathUtil.getCacheDir()+"/video/"+DeviceUtil.getDate()+".mp4";
		try
		{
		    FileChannel fc = new RandomAccessFile(mFinalPath, "rw").getChannel();
		    out.writeContainer(fc);
		    fc.close();
		}
		catch (Exception e)
		{
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		         
		moviesList.clear();
		for(String filePath:videoList){
			FileUtil.deleteFile(filePath);
		}
		
		videoList.clear();

	}
}
