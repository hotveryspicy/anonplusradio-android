/**
 * 
 */
package com.anonplusradio.android;

import com.anonplusradio.audio.media.streamStation.StreamStation;

/**
 * @author 832880
 *
 */
public class CONSTANTS {
	public static final String CHAT_URL				= "irc.anonplus.com";
	public static final String CHAT_HOST			= "irc.anonplus.com";
	public static final String CHAT_CHANNEL			= "#anonplusradio";
	public static final String SITE_URL				= "http://site.anonplusradio.com";
	
	public static final String STREAM_1_URL			= "http://anonplusradio.com:192/";

	
	public static final String STREAM_1_LABEL		= "anon.plus.radio";

	public static final StreamStation[] ALl_STATIONS =
		{
			new StreamStation(STREAM_1_LABEL, STREAM_1_URL)
		};
	public static final StreamStation DEFAULT_STREAM_STATION = ALl_STATIONS[0];
}
