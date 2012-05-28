/**
 * 
 */
package com.anonplusradio.android.media.mediaplayer;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.anonplusradio.android.media.mediaplayer.StatefulMediaPlayer.MPStates;
import com.anonplusradio.audio.media.streamStation.StreamStation;


/**
 * @author 832880
 *
 */
public class MediaPlayerThread
	extends Thread
	implements	
			OnBufferingUpdateListener,
			OnInfoListener, 
			OnPreparedListener,
			OnErrorListener
{
	
	private StatefulMediaPlayer mMediaPlayer			= new StatefulMediaPlayer();
	private IMediaPlayerThreadClient mClient;
	
	public MediaPlayerThread(IMediaPlayerThreadClient client)
	{
		mClient = client;
	}
	
	/**
	 * Initializes a StatefulMediaPlayer for streaming playback of the provided StreamStation
	 * @param station The StreamStation representing the station to play
	 */
	public void initializePlayer(final StreamStation station)
	{
		mClient.onInitializePlayerStart();
	
		mMediaPlayer = new StatefulMediaPlayer(station);
				
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.prepareAsync();

		
	}

	/**
	 * Initializes a StatefulMediaPlayer for streaming playback of the provided stream url
	 * @param streamUrl The URL of the stream to play.
	 */
	public void initializePlayer(String streamUrl)
	{

		mMediaPlayer = new StatefulMediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try
		{
			mMediaPlayer.setDataSource(streamUrl);
		}
		catch (Exception e)
		{
			Log.e("MediaPlayerThread", "error setting data source");
			mMediaPlayer.setState(MPStates.ERROR);
		}
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.prepareAsync();
	}
	
	 /**
	 * Starts the contained StatefulMediaPlayer and foregrounds the service to support
	 * persisted background playback.
	 */
	public void startMediaPlayer()
	{
		Log.d("MediaPlayerThread","startMediaPlayer() called");

				mMediaPlayer.start();
		
	}
	
	/**
	 * Pauses the contained StatefulMediaPlayer
	 */
	public void pauseMediaPlayer()
	{
		Log.d("MediaPlayerThread","pauseMediaPlayer() called");
		mMediaPlayer.pause();

	}
	/**
	 * Stops the contained StatefulMediaPlayer.
	 */
	public void stopMediaPlayer()
	{
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}
	
	public void resetMediaPlayer()
	{
		mMediaPlayer.reset();
	}
	
	@Override
	public void onBufferingUpdate(MediaPlayer player, int percent)
	{

	}
	
	@Override
	public boolean onError(MediaPlayer player, int what, int extra)
	{
		mMediaPlayer.reset();
		mClient.onError();
		return true;
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra)
	{
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer player)
	{
		mClient.onInitializePlayerSuccess();
		
	}

	/**
	 * @return
	 */
	public StatefulMediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}
	
}

