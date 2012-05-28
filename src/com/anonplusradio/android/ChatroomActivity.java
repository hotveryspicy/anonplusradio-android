/**
 * 
 */
package com.anonplusradio.android;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.schwering.irc.lib.IRCUser;
import org.schwering.irc.lib.IRCUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
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
	private String TAG							= "ChatroomActivity";
	private TextView mChatTextView;				//= (TextView) findViewById(R.id.chatTextView);
	private TextView mNickListTextView;
	private boolean mBound						= false;
	private IRCService mService;
	
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
		
		//bind to IRCService
		bindToService();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		

	}
	

	private void initializeButtons() {
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
	
	private void updateNickListView(ArrayList<String> nickList)
	{
		if (nickList != null)
		{
			for (String nick : nickList)
			{
				mNickListTextView.append(nick + "\n");
			}
			
		}
	}

 

	
	@Override
	public void onPrivmsg(String chan, IRCUser user, String msg)
	{
		StringBuilder outputLine = new StringBuilder();
		String nick = user.getNick();

		outputLine.append("<" + nick + "> " + msg + "\n");
		updateChannelView(outputLine.toString());
	}


	@Override
	public void onQuit(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onRegistered()
	{
		Log.v("TAG", "registered");
	}

	@Override
	public void onMessage(int num, String value, String msg)
	{
		StringBuilder outputLine = new StringBuilder();

		if ( num == IRCUtil.RPL_NAMREPLY) // nicklist 
		{

			StringTokenizer stValue = new StringTokenizer(value);
			stValue.nextToken(); // skip name
			stValue.nextToken(); // skip (it is a '@', '*' or '=')
			StringTokenizer stNicks = new StringTokenizer(msg);
			//String[] nicks = new String[stNicks.countTokens()];

			final TextView chatNickListTextView = (TextView) findViewById(R.id.chatNickListTextView);
			int i = 0;
			while(stNicks.hasMoreTokens())
			{
				final String nextNick = stNicks.nextToken();
				i++;
				Log.v(TAG, nextNick);
				runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{
						chatNickListTextView.append(nextNick + "\n");
					}

				});

			}

			//outputLine.append(msg);
			Log.v(TAG, "nick list size: " + i);


		}
		else if (num == IRCUtil.RPL_TOPIC) // on-join topic
		{ 
			outputLine.append("*** Topic *** \n" + msg + "\n******\n");
			StringTokenizer stValue = new StringTokenizer(value);
			stValue.nextToken(); // jump over the first (it is our name)
		}

		updateChannelView(outputLine.toString());
	}


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
        Intent intent = new Intent(this, IRCService.class);
		
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


}
