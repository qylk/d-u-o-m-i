package com.qylk.app.musicplayer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.qylk.app.musicplayer.utils.ConstantValueDef;
import com.qylk.app.musicplayer.utils.MusicUtils;

public class LeadActivity extends Activity implements ConstantValueDef {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MusicUtils.activateActivity(this, MusicUtils.getIntPref(this,
				"lastActiveActivity", ACTIVITY_LIBRARYLIST));
	}
}
