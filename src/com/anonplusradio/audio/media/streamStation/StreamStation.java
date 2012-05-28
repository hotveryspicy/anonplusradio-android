/**
 * 
 */
package com.anonplusradio.audio.media.streamStation;

/**
 * A class to represent Streaming media stations. Holds label, URL, and other information.
  */
public class StreamStation {
	private String mStationLabel;
	private String mStationUrl;

	/**
	 * Constructs a new StreamStation object with empty label and URL
	 */
	public StreamStation() {
		this("","");
	}
	/**
	 * Constructs a new StreamStation object with specified label and URL
	 * @param stationLabel
	 * @param stationUrl
	 */
	public StreamStation(String stationLabel, String stationUrl) {
		mStationLabel = stationLabel;
		mStationUrl = stationUrl;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StreamStation other = (StreamStation) obj;
		if (mStationLabel == null) {
			if (other.mStationLabel != null)
				return false;
		}
		else if (!mStationLabel.equals(other.mStationLabel))
			return false;
		if (mStationUrl == null) {
			if (other.mStationUrl != null)
				return false;
		}
		else if (!mStationUrl.equals(other.mStationUrl))
			return false;
		return true;
	}
	
	/**
	 * Gets the station's label as a String
	 * @return the station label
	 */
	public String getStationLabel() {
		return mStationLabel;
	}

	/**
	 * Gets the station's URL, as a String
	 * @return the URL of the station
	 */
	public String getStationUrl() {
		return mStationUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mStationLabel == null) ? 0 : mStationLabel.hashCode());
		result = prime * result
				+ ((mStationUrl == null) ? 0 : mStationUrl.hashCode());
		return result;
	}

	/**
	 * Sets a String as the station's label
	 * @param stationLabel the label to set
	 */
	public void setStationLabel(String stationLabel) {
		this.mStationLabel = stationLabel;
	}

	/**
	 * Set's a String as the station's URL
	 * @param stationUrl the URL of the Station
	 */
	public void setStationUrl(String mStationUrl) {
		this.mStationUrl = mStationUrl;
	}
	
	
}
