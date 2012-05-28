/**
 * 
 */
package com.anonplusradio.android.media.mediaplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.anonplusradio.android.MainActivity;
import com.anonplusradio.audio.media.streamStation.StreamStation;


/**
 * An extension of android.app.Service class which provides management to a MediaPlayerThread.<br>
 * @see com.anonplusradio.android.media.mediaplayer.StatefulMediaPlayer
 * @see com.anonplusradio.android.media.mediaplayer.MediaPlayerThread 
 */
public class MediaPlayerService
	extends Service
	implements IMediaPlayerThreadClient
{
	private MediaPlayerThread mMediaPlayerThread		= new MediaPlayerThread(this);
	private final Binder mBinder						= new MediaPlayerBinder();
	private IMediaPlayerServiceClient mClient;
	private StreamStation mCurrentStation;
	
	@Override
	public void onCreate()
	{
		mMediaPlayerThread.start();
	}
	
	/**
	 * A class for clients binding to this service. The client will be passed an object of this class
	 * via its onServiceConnected(ComponentName, IBinder) callback.
	 */
	public class MediaPlayerBinder extends Binder
	{
		/**
		 * Returns the instance of this service for a client to make method calls on it.
		 * @return the instance of this service.
		 */
		public MediaPlayerService getService()
		{
			return MediaPlayerService.this;
		}
		
	}

	/**
	 * Returns the contained StatefulMediaPlayer
	 * @return
	 */
	public StatefulMediaPlayer getMediaPlayer()
	{
		return mMediaPlayerThread.getMediaPlayer();
	}
	

	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return mBinder;
	}


	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;
	}
	


	/**
	 * Sets the client using this service.
	 * @param client The client of this service, which implements the IMediaPlayerServiceClient interface
	 */
	public void setClient(IMediaPlayerServiceClient client)
	{
		this.mClient = client;
	}
	
	
	
	
	
	public void initializePlayer(final StreamStation station)
	{
		mCurrentStation = station;
		mMediaPlayerThread.initializePlayer(station);
	}
	
	public void startMediaPlayer()
	{
		Context context = getApplicationContext();
		
		//set to foreground
		Notification notification = new Notification(android.R.drawable.ic_media_play, "Anon+ Radio",
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		
		CharSequence contentTitle = "Anon+ Radio Is Playing";
		CharSequence contentText = mCurrentStation.getStationLabel();
		notification.setLatestEventInfo(context, contentTitle,
		        contentText, pendingIntent);
		startForeground(1, notification);
		notification.setLatestEventInfo(context, "Anon+ Radio is  Playing",
		        "Anon+ Radio", pendingIntent);
		
		mMediaPlayerThread.startMediaPlayer();
	}
	
	/**
	 * Pauses playback
	 */
	public void pauseMediaPlayer()
	{
		Log.d("MediaPlayerService","pauseMediaPlayer() called");
		mMediaPlayerThread.pauseMediaPlayer();
		stopForeground(true);

	}
	/**
	 * Stops playback
	 */
	public void stopMediaPlayer()
	{
		stopForeground(true);
		mMediaPlayerThread.stopMediaPlayer();
	}
	
	public void resetMediaPlayer()
	{
		stopForeground(true);
		mMediaPlayerThread.resetMediaPlayer();
	}
	

	
	@Override
	public void onError()
	{
		mClient.onError();
	}

	@Override
	public void onInitializePlayerStart()
	{
		mClient.onInitializePlayerStart();
	}
	@Override
	public void onInitializePlayerSuccess()
	{
		startMediaPlayer();

		mClient.onInitializePlayerSuccess();
		
	}



	/**
	 * 
	 */
	public void resetMediaPlaer() {
		// TODO Auto-generated method stub
		
	}
	
	public void unRegister()
	{
		this.mClient = null;
	}

}