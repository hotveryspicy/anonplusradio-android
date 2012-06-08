/**
 * 
 */
package com.anonplusradio.android.irc;

import java.util.ArrayList;


/**
 * @author 832880
 * 
 */
public interface IIRCServiceClient
{

	/**
	 * @param chan
	 * @param user
	 * @param msg
	 */
	void onPrivateMessage(
		String sender,
		String login,
		String hostname,
		String message);



	/**
	 * 
	 */
	void onConnected();


	public void onMessage(
		String channel,
		String sender,
		String login,
		String hostname,
		String message);

	void onAction(
			String sender,
			String login,
			String hostname,
			String target,
			String action);

	void onJoin(
			String channel,
			String sender,
			String login,
			String hostname);

	void onPart(
			String channel,
			String sender,
			String login,
			String hostname);

	void onNickChange(
			String oldNick,
			String login,
			String hostname,
			String newNick);

	void onKick(String channel,
			String kickerNick,
			String kickerLogin,
			String kickerHostname,
			String recipientNick,
			String reason);

	void onQuit(String sourceNick,
			String sourceLogin,
			String sourceHostname,
			String reason);



	void onUserList(String channel,
			ArrayList<String> nickList);

}
