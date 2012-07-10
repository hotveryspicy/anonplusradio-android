/**
 * 
 */
package com.anonplusradio.android.irc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.User;

import com.anonplusradio.android.CONSTANTS;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author 832880
 * 
 */
public class IRCService
	extends
		Service
	implements
		IIrcAgentClient
{
	/*
	 * *********** FIELD MEMBERS ****************
	 */

	private String TAG = "IRCService";

	private ArrayList<String> nickList		= new ArrayList<String>();
	private String channelHistory			= "AnonPlusRadio IRC. Do Not assume this is a secure transmission. \n" +
											"Type /nick <nickname> to set a nick.";
	private String mUserNick				= "";

	private final Binder mBinder			= new IRCServiceBinder();
	private IrcAgent mIRCAgent;
	private IIRCServiceClient mClient;

	/*
	 * ************ FIELD ACCESSORS *************
	 */

	public ArrayList<String> getNickList()
	{
		Log.d(TAG, "getNickList() called");
		nickList = new ArrayList<String>();
		for (User user: mIRCAgent.getUsers("#anonplusradio"))
		{
			nickList.add(user.getNick());
		}
		Log.d(TAG, "returning niclList. Count: " + nickList.size());
		return nickList;
	}

	public String getChatHistory()
	{
		Log.d(TAG, "getChatHistory() called... length:" + channelHistory.length());
		return channelHistory;
	}

	/*
	 * ************ FIELD MUTATORS **************
	 */

	/**
	 * Sets the client using this service.
	 * 
	 * @param client
	 *            The client of this service, which implements the
	 *            IMediaPlayerServiceClient interface
	 */
	public void setClient(IIRCServiceClient client)
	{
		this.mClient = client;
	}

	/**
	 * Appends the channel history with provided amendment, removing content
	 * from the front of the history if its size exceeds the limit.
	 * 
	 * @param amendment
	 *            the String of text to add to history.
	 */
	public void appendChannelHistory(String amendment)
	{
		Log.d(TAG, "appendChannelHistory() called. Amendment: " + amendment);
		channelHistory = channelHistory.concat(amendment);
		if (channelHistory.length() > 5000)
		{
			channelHistory = new String(channelHistory.substring(0 + amendment.length(), channelHistory.length()));
		}
		
		Log.d(TAG, "channel history appended. Length: " + channelHistory.length());
	}

	/*
	 * ***** ANDROID SERVICE IMPLEMENTATION *****
	 */

	@Override
	public void onCreate()
	{
		Log.d(TAG, "onCreate() called");
		initializeConnection("irc.anonplus.com", 6667, mUserNick);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_STICKY;

	}

	/**
	 * A class for clients binding to this service. The client will be passed an
	 * object of this class via its onServiceConnected(ComponentName, IBinder)
	 * callback.
	 */
	public class IRCServiceBinder
		extends
			Binder
	{
		/**
		 * Returns the instance of this service for a client to make method
		 * calls on it.
		 * 
		 * @return the instance of this service.
		 */
		public IRCService getService()
		{
			return IRCService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return mBinder;
	}
	
	
	public void unRegister()
	{
		mClient = null;
	}

	/*
	 * ************ IRC ACTIONS *****************
	 */

	/**
	 * Tries to initialize a connection to the provided hostname and port, using
	 * the provided nick. If a NickAlreadyinUseException is caught, the nickname
	 * will be appended with random numbers for each future attempt to connect.
	 * 
	 * @param hostname
	 *            The hostname of the server to connect to.
	 * @param port
	 *            The port to connect to the irc server
	 * @param nick
	 *            The user nickname to use
	 */
	public void initializeConnection(
		String hostname,
		int port,
		String nick)
	{
		Log.d(TAG, "initializeConnection() called. Hostname: " + hostname + "; port: " + port + "; nick: " + nick);
		mUserNick = nick;
		mIRCAgent = new IrcAgent();
		mIRCAgent.addClient(this);
		mIRCAgent.changeNick(mUserNick);
		try
		{
			mIRCAgent.connect(hostname, port);
		}
		catch (NickAlreadyInUseException e)
		{
			Log.e(TAG, "Nick already in use! will retry with random number appended.");
			mUserNick = mUserNick
				+ Double.toString(Math.floor(Math.random() * 9999));
			initializeConnection(hostname, port, nick);
		}
		catch (IOException e)
		{
			Log.e(TAG, "IOException: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IrcException e)
		{
			Log.e(TAG, "IrcException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param string
	 */
	public void doPrivmsg(String user, String message)
	{
		mIRCAgent.sendMessage(user, message);
	}
	
	public void setNick(String nick)
	{
		this.mUserNick = nick;
		mIRCAgent.changeNick(mUserNick);
	}

	/*
	 * ************ IRC EVENT CALLBACKS *********
	 */

	@Override
	public void onConnect()
	{
		
		Log.d(TAG, "onConnect() called");
		//TODO set user-set nick
		
		mIRCAgent.changeNick(mUserNick);
		// join default channel
		mIRCAgent.joinChannel(CONSTANTS.CHAT_CHANNEL);

	}

	@Override
	public void onDisconnect()
	{
		Log.d(TAG, "onDisconnect() called");
		appendChannelHistory("You have been disconnected...\n");

	}

	@Override
	public void onUserList(String channel, User[] users)
	{
		Log.d(TAG, "onUserList() called");
		nickList = new ArrayList<String>();

		for (User user : users)
		{
			nickList.add(user.getNick());
		}
		mClient.onUserList(channel, nickList);
	}

	@Override
	public void onMessage(
		String channel,
		String sender,
		String login,
		String hostname,
		String message)
	{
		Log.d(TAG, "onPrivmsg() called. msg: " + message);
		appendChannelHistory("<" + sender + "> " + message + "\n");

		mClient.onMessage(channel, sender, login, hostname, message);

	}

	@Override
	public void onPrivateMessage(
		String sender,
		String login,
		String hostname,
		String message)
	{
		Log.d(TAG, "onPrivateMessage() called. msg: " + message);
		appendChannelHistory("PM: <" + sender + "> " + message + "\n");

		mClient.onPrivateMessage(sender, login, hostname, message);
	}

	@Override
	public void onAction(
		String sender,
		String login,
		String hostname,
		String target,
		String action)
	{
		Log.d(TAG, "onAction() called");
		appendChannelHistory(sender + " " + action + "\n");
		mClient.onAction(sender, login, hostname, target, action);
	}

	@Override
	public void onNotice(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String target,
		String notice)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(
		String channel,
		String sender,
		String login,
		String hostname)
	{
		Log.d(TAG, "onJoin() called");
		nickList.add(sender);
		appendChannelHistory(sender + " has joined " + channel);
		mClient.onJoin(channel, sender, login, hostname);
	}

	@Override
	public void onPart(
		String channel,
		String sender,
		String login,
		String hostname)
	{
		Log.d(TAG, "onPart() called");
		nickList.remove(sender);
		appendChannelHistory(sender + " has left " + channel);
		mClient.onPart(channel, sender, login, hostname);
	}

	@Override
	public void onNickChange(
		String oldNick,
		String login,
		String hostname,
		String newNick)
	{
		Log.d(TAG, "onNickChange() called");
		nickList.remove(oldNick);
		nickList.add(newNick);
		appendChannelHistory(oldNick + " is now known as " + newNick);
		mClient.onNickChange(oldNick, login, hostname, newNick);
	}

	@Override
	public void onKick(
		String channel,
		String kickerNick,
		String kickerLogin,
		String kickerHostname,
		String recipientNick,
		String reason)
	{
		Log.d(TAG, "onKick() called");
		nickList.remove(recipientNick);
		appendChannelHistory(recipientNick + " got his fgt ass kicked by " + kickerNick + ". - " + reason);
		mClient.onKick(channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
	}

	@Override
	public void onQuit(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String reason)
	{
		Log.d(TAG, "onQuit() called");
		nickList.remove(sourceNick);
		appendChannelHistory(sourceNick + " has quit IRC: " + reason);
		mClient.onQuit(sourceNick, sourceLogin, sourceHostname, reason);

	}

	@Override
	public void onTopic(String channel, String topic)
	{
		Log.d(TAG, "onTopic() called");

	}

	@Override
	public void onTopic(
		String channel,
		String topic,
		String setBy,
		long date,
		boolean changed)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onChannelInfo(
		String channel,
		int userCount,
		String topic)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(
		String channel,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String mode)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserMode(
		String targetNick,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String mode)
	{
		Log.d(TAG, "onUserMode() called");
		appendChannelHistory(sourceNick + " set mode " + mode
			+ " on user " + targetNick);
	}

	@Override
	public void onInvite(
		String targetNick,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String channel)
	{
		// TODO Auto-generated method stub

	}

	
}
