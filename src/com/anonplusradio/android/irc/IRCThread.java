/**
 * 
 */
package com.anonplusradio.android.irc;

import java.io.IOException;

import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventListener;
import org.schwering.irc.lib.IRCModeParser;
import org.schwering.irc.lib.IRCUser;

import com.anonplusradio.android.CONSTANTS;

import android.util.Log;


/**
 * @author 832880
 *
 */
public class IRCThread
	extends
			Thread
	implements
			IRCEventListener
{
	private IIRCThreadClient mClient;
	private IRCConnection mIRCConnection;

	
	public IRCThread(IIRCThreadClient client)
	{
		mClient = client;
	}
	
	public void run()
	{
		initializeConnection(CONSTANTS.CHAT_HOST, new int[] {6667} , "" , "anon-droiduser" , "anon-droiduser", "anon-droiduser");

	}
	
	public void initializeConnection(String host, int[] ports, String string1, String name1, String name2, String name3 )
	{
		mIRCConnection = new IRCConnection(host, ports, string1, name1, name2, name3);
		mIRCConnection.addIRCEventListener(this);
		mIRCConnection.setEncoding("UTF-8");
		mIRCConnection.setPong(true);
		mIRCConnection.setColors(false);
		try
		{
			mIRCConnection.connect();
		}
		catch (IOException e)
		{
			Log.e("chatthread", "initalizeConnection failed! " + e.getMessage());
			e.printStackTrace();
		}	
	}
	
	public void doJoin(String channel)
	{
		mIRCConnection.doJoin(channel);
	}
	
	public void doPrivMsg(String channel, String msg)
	{
		mIRCConnection.doPrivmsg(channel, msg);
	}
	
	@Override
	public void onDisconnected()
	{
		//TODO auto-generated method stub
	}

	@Override
	public void onError(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvite(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJoin(String arg0, IRCUser user) {
		//TODO
	}

	@Override
	public void onKick(String arg0, IRCUser arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(String arg0, IRCUser arg1, IRCModeParser arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMode(IRCUser arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNick(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotice(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPart(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPing(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrivmsg(String chan, IRCUser user, String msg)
	{
		mClient.onPrivmsg(chan, user, msg);
	}

	@Override
	public void onQuit(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegistered()
	{
		Log.v("chatthread", "registered");
		mIRCConnection.doJoin(CONSTANTS.CHAT_CHANNEL);
	}

	@Override
	public void onReply(int num, String value, String msg)
	{
		mClient.onReply(num, value, msg);
	}



	@Override
	public void onTopic(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unknown(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}
}
