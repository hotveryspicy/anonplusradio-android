package com.anonplusradio.android.irc;

import java.util.Vector;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import android.util.Log;

public class IrcAgent
	extends
		PircBot
{
	public IrcAgent()
	{
		
	};
	
	/*
	 * =========================================
	 * 		FIELD MEMBERS
	 * =========================================
	 */
	
	private static String TAG = "IrcAgent";
	
	private Vector<IIrcAgentClient> mClients = new Vector<IIrcAgentClient>();

	/*
	 * =========================================
	 * 		INTERNAL CLASS OPERATIONS
	 * =========================================
	 */
	
	@Override
	public void log(String line)
	{
		//override to use android logging too
		super.log(line);
		Log.i(TAG + "-internal", line);
	}

	/*
	 * ********** IRC EVENT CALLBACKS ***********
	 */
	
	@Override
	public void onConnect()
	{
		Log.d(TAG, "onConnect() called. Connection suceeded");
		for (IIrcAgentClient client : mClients)
		{
			client.onConnect();
		}
	}

	@Override
	public void onDisconnect()
	{
		Log.d(TAG, "onDisonnect() called. Connection closed");
		for (IIrcAgentClient client : mClients)
		{
			client.onDisconnect();
		}
	}
	
	@Override
	public void onUserList(String channel, User[] users)
	{
		Log.d(TAG, "onUserList() called. Channel: " + channel
			+ "; Number of users: " + users.length);
		for (IIrcAgentClient client : mClients)
		{
			client.onUserList(channel, users);
		}
	}

	@Override
	public void onMessage(
		String channel,
		String sender,
		String login,
		String hostname,
		String message)
	{
		Log.d(TAG, "onMessage() called. Message received. Channel: "
			+ channel + "; Sender: " + sender + "; Message: "
			+ message);
		for (IIrcAgentClient client : mClients)
		{
			client.onMessage(channel, sender, login, hostname, message);
		}
	}

	@Override
	public void onPrivateMessage(
		String sender,
		String login,
		String hostname,
		String message)
	{
		Log.d(TAG, "onPrivateMessage() called. Message received. Sender: "
			+ sender + "; Message: " + message);
		for (IIrcAgentClient client : mClients)
		{
			client.onPrivateMessage(sender, login, hostname, message);
		}

	}

	@Override
	public void onAction(
		String sender,
		String login,
		String hostname,
		String target,
		String action)
	{
		Log.d(TAG, "onAction() called. Message received. Sender: "
			+ sender + "; Target: " + target + "; Action: " + action);
		for (IIrcAgentClient client : mClients)
		{
			client.onAction(sender, login, hostname, target, action);
		}
	}

	@Override
	public void onNotice(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String target,
		String notice)
	{
		Log.d(TAG, "onNotice() called. Noticed received. Source: "
			+ sourceNick + "; Target: " + target + "; notice: "
			+ notice);
		for (IIrcAgentClient client : mClients)
		{
			client.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
		}
	}

	@Override
	public void onJoin(
		String channel,
		String sender,
		String login,
		String hostname)
	{
		Log.d(TAG, "onJoin() called. Someone joined. Channel: "
			+ channel + "; Sender: " + sender + "; hostname: "
			+ hostname);
		for (IIrcAgentClient client : mClients)
		{
			client.onJoin(channel, sender, login, hostname);
		}
	}

	@Override
	public void onPart(
		String channel,
		String sender,
		String login,
		String hostname)
	{
		Log.d(TAG, "onPart() called. Someone parted. Channel: "
			+ channel + "; Sender: " + sender + "; hostname: "
			+ hostname);
		for (IIrcAgentClient client : mClients)
		{
			client.onJoin(channel, sender, login, hostname);
		}
	}

	@Override
	public void onNickChange(
		String oldNick,
		String login,
		String hostname,
		String newNick)
	{
		Log.d(TAG, "onNickChange() called. " + oldNick
			+ " is now known as " + newNick + "; hostname: "
			+ hostname);
		for (IIrcAgentClient client : mClients)
		{
			client.onNickChange(oldNick, login, hostname, newNick);
		}
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
		Log.d(TAG, "onKick() called. " + recipientNick
			+ " was kicked from " + channel + " by " + kickerNick
			+ "; hostname: " + kickerHostname);
		for (IIrcAgentClient client : mClients)
		{
			client.onKick(channel, kickerNick, kickerLogin, kickerHostname, recipientNick, reason);
		}
	}

	@Override
	public void onQuit(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String reason)
	{
		Log.d(TAG, "onQuit() called. " + sourceNick
			+ " quit. Reason: " + reason + "; hostname: "
			+ sourceHostname);
		for (IIrcAgentClient client : mClients)
		{
			client.onQuit(sourceNick, sourceLogin, sourceHostname, reason);
		}
	}

	@Override
	public void onTopic(String channel, String topic)
	{
		Log.d(TAG, "onTopic() called. Topic changed in channel "
			+ channel + " to: " + topic);
		for (IIrcAgentClient client : mClients)
		{
			client.onTopic(channel, topic);
		}
	}

	@Override
	public void onTopic(
		String channel,
		String topic,
		String setBy,
		long date,
		boolean changed)
	{
		Log.d(TAG, "onTopic() called. Topic changed in channel "
			+ channel + " to: " + topic + " by: " + setBy);
		for (IIrcAgentClient client : mClients)
		{
			client.onTopic(channel, topic, setBy, date, changed);
		}
	}

	@Override
	public void onChannelInfo(
		String channel,
		int userCount,
		String topic)
	{
		Log.d(TAG, "onchannelInfo() called. Channel " + channel);
		for (IIrcAgentClient client : mClients)
		{
			client.onChannelInfo(channel, userCount, topic);
		}
	}

	@Override
	public void onMode(
		String channel,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String mode)
	{
		Log.d(TAG, "onMode() called. Mode set to: " + mode
			+ " in channel " + channel + " by " + sourceNick);
		for (IIrcAgentClient client : mClients)
		{
			client.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
		}
	}

	@Override
	public void onUserMode(
		String targetNick,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String mode)
	{
		Log.d(TAG, "onUserMode() called. User " + targetNick
			+ " set to mode " + mode + " by " + " sourceNick");
		for (IIrcAgentClient client : mClients)
		{
			client.onUserMode(targetNick, sourceNick, sourceLogin, sourceHostname, mode);
		}

	}

	@Override
	public void onInvite(
		String targetNick,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String channel)
	{
		Log.d(TAG, "onInvite() called. " + targetNick
			+ " was invited to channel " + channel + " by "
			+ sourceNick);
		for (IIrcAgentClient client : mClients)
		{
			client.onInvite(targetNick, sourceNick, sourceLogin, sourceHostname, channel);
		}

	}

	public void addClient(IIrcAgentClient client) {
		mClients.add(client);
		
	}
}