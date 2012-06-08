/**
 * 
 */
package com.anonplusradio.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anonplusradio.android.irc.IIRCServiceClient;
import com.anonplusradio.android.irc.IRCService;
import com.anonplusradio.android.irc.IRCService.IRCServiceBinder;

/**
 * @author 832880
 *
 */
public class ChatroomActivity extends Activity implements IIRCServiceClient  {
	/*
	 * ************ FIELD MEMBERS *********
	 */
	private String TAG							= "ChatroomActivity";
	private TextView mChatTextView;				//= (TextView) findViewById(R.id.chatTextView);
	private TextView mNickListTextView;
	private boolean mBound						= false;
	private IRCService mService;
	
	/*
	 * ************ Activity Implementation *********
	 */
	
	@Override 
	protected void onCreate(android.os.Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		
		
		
		//make the textviews for nicklist and chat history scroll
		mChatTextView		= (TextView) findViewById(R.id.chatTextView);
		mNickListTextView	= (TextView) findViewById(R.id.chatNickListTextView);
		mChatTextView.setMovementMethod(new ScrollingMovementMethod());
		mNickListTextView.setMovementMethod(new ScrollingMovementMethod());
		
		//setup event listeners, etc for buttons
		initializeButtons();
		
		
	}
	
	protected void onResume()
	{
		super.onResume();
		//bind to IRCService
		bindToService();
	}
	
	protected void onStop()
	{
		super.onDestroy();
    	if (mBound)
    	{
    		mService.unRegister();
    		unbindService(mConnection);
    		mBound = false;
    	}
	}
	
	private void initializeButtons()
	{
		mChatTextView.setMovementMethod(new ScrollingMovementMethod());

		final 	Button chatSendButton = (Button) findViewById(R.id.chatSendButton);
		chatSendButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				EditText chatInput = (EditText) findViewById(R.id.chatInput);
				mService.doPrivmsg(CONSTANTS.CHAT_CHANNEL, chatInput.getText().toString());
				updateChannelView(chatInput.getText().toString() + "\n");
				chatInput.setText("");
			}

		});
	}
	
	private void updateChannelView(String outputText)
	{
		final String output = outputText;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mChatTextView.append(output);

				int scrollAmount = mChatTextView.getLayout().getLineTop(mChatTextView.getLineCount()) - mChatTextView.getHeight();
				if (scrollAmount > 0)
					mChatTextView.scrollTo(0, scrollAmount);
			}
		});
	}
	
	private void updateNickListView(final ArrayList<String> nickList)
	{
		Log.d(TAG, "updateNickListView() called. Count: " + nickList.size());
		if (nickList != null)
		{
			runOnUiThread(new Runnable(){

				@Override
				public void run()
				{
					for (String nick : nickList)
					{
						mNickListTextView.append(nick + "\n");
					}					
				}
				
			});
		}
	}
	
	private void getNickFromUser()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Title");
		alert.setMessage("Message");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.getText().toString();
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
	}
	
	/*
	 * ************ SERVICE CLIENT IMPLEMENTATION ***********
	 */

	/** 
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection mConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder serviceBinder)
		{
			Log.d(TAG,"service connected");

			//bound with Service. get Service instance
			IRCServiceBinder binder = (IRCServiceBinder) serviceBinder;
			mService = binder.getService();

			//send this instance to the service, so it can make callbacks on this instance as a client
			mService.setClient(ChatroomActivity.this);
			mBound = true;
			
			Log.d(TAG, "Chat history: " + mService.getChatHistory());
			
			//update chatHistory with what service has
			mChatTextView.append(mService.getChatHistory());
			//update nicklist view
			updateNickListView(mService.getNickList());
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			mBound = false;
		}
	};
	
	/**
	 * Binds to the instance of IRCService. If no instance of IRCService exists, it first starts
	 * a new instance of the service.
	 */
	public void bindToService()
	{
        Intent intent = new Intent(this.getApplicationContext(), IRCService.class);
		
        if (IRCServiceRunning())
        {
			// Bind to Service
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
        //no instance of service
		else
		{
			//start service and bind to it
			startService(intent);
	        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	/**
	 * @return
	 */
	private boolean IRCServiceRunning()
	{
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        
    	for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.anonplusradio.android.irc.IRCService".equals(service.service.getClassName()))
            {
                return true;
            }
        }
    	
        return false;
	}
 
	/*
	 * ************ IRC EVENT CALLBACKS *********
	 */

	@Override
	public void onPrivateMessage(String sender, String login, String hostname,
			String message)
	{
		StringBuilder outputLine = new StringBuilder();
		outputLine.append("PM: <" + sender + "> " + message + "\n");
		updateChannelView(outputLine.toString());
	}

	@Override
	public void onConnected()
	{
		updateChannelView("Connection established! \n");
	}

	@Override
	public void onMessage(String channel, String sender, String login,
			String hostname, String message)
	{
		StringBuilder outputLine = new StringBuilder();;
		outputLine.append("<" + sender + "> " + message + "\n");
		updateChannelView(outputLine.toString());
		
	}

	@Override
	public void onAction(String sender, String login, String hostname,
			String target, String action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJoin(String channel, String sender, String login,
			String hostname)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPart(String channel, String sender, String login,
			String hostname)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNickChange(String oldNick, String login, String hostname,
			String newNick) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKick(String channel, String kickerNick, String kickerLogin,
			String kickerHostname, String recipientNick, String reason)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onQuit(String sourceNick, String sourceLogin,
			String sourceHostname, String reason)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserList(String channel, ArrayList<String> nickList)
	{
		updateNickListView(nickList);
		
	}


}
