/**
 * 
 */
package com.anonplusradio.android.irc;

import org.schwering.irc.lib.IRCUser;

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
	 * @param arg0
	 * @param arg1
	 */
	void onQuit(IRCUser arg0, String arg1);

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
}
