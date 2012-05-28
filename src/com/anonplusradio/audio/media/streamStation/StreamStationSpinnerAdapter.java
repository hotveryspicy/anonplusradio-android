/**
 * 
 */
package com.anonplusradio.audio.media.streamStation;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 *
 */
public class StreamStationSpinnerAdapter extends ArrayAdapter<StreamStation> {
	int mTextViewResourceId;
	Context mContext;

	/**
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public StreamStationSpinnerAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mTextViewResourceId = textViewResourceId;
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView)(super.getView(position, convertView, parent));
		tv.setText(getItem(position).getStationLabel());
		return tv;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView tv = (TextView)(super.getDropDownView(position, convertView, parent));
		tv.setText(getItem(position).getStationLabel());
		return tv;
	}
}
