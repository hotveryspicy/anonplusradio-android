package com.anonplusradio.android;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
	}
}
