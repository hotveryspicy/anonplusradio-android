package com.anonplusradio.android.irc;

import org.jibble.pircbot.User;

public interface IIrcAgentClient
{
	public void onConnect();

	public void onDisconnect();

	public void onUserList(String channel, User[] users);

	public void onMessage(
		String channel,
		String sender,
		String login,
		String hostname,
		String message);

	public void onPrivateMessage(
		String sender,
		String login,
		String hostname,
		String message);

	public void onAction(
		String sender,
		String login,
		String hostname,
		String target,
		String action);

	public void onNotice(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String target,
		String notice);

	public void onJoin(
		String channel,
		String sender,
		String login,
		String hostname);

	public void onPart(
		String channel,
		String sender,
		String login,
		String hostname);

	public void onNickChange(
		String oldNick,
		String login,
		String hostname,
		String newNick);

	public void onKick(
		String channel,
		String kickerNick,
		String kickerLogin,
		String kickerHostname,
		String recipientNick,
		String reason);

	void onQuit(
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String reason);

	public void onTopic(String channel, String topic);

	public void onTopic(
		String channel,
		String topic,
		String setBy,
		long date,
		boolean changed);

	public void onChannelInfo(
		String channel,
		int userCount,
		String topic);

	public void onMode(
		String channel,
		String sourceNick,
		String sourceLogin,
		String sourceHostname,
		String mode);
	
	public void onUserMode(String targetNick,
        String sourceNick,
        String sourceLogin,
        String sourceHostname,
        String mode);
	
	public void onInvite(String targetNick,
        String sourceNick,
        String sourceLogin,
        String sourceHostname,
        String channel);
}
