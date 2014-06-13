package com.qylk.app.musicplayer.utils;

import android.os.Environment;

public interface ConstantValueDef {
	public static final int ACTIVITY_LIBRARYLIST = 1;
	public static final int ACTIVITY_PLAYING = 0;
	public static final int ACTIVITY_PLAYLIST = 3;
	public static final int ACTIVITY_SEARCH = 2;

	public static final String ARTWORK_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/" + "art.zip";
}
