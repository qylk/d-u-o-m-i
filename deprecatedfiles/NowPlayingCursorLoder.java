package com.qylk.app.musicplayer.deprecated;

import android.content.Context;
import android.content.CursorLoader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author Administrator
 * @deprecated
 *
 */
public class NowPlayingCursorLoder extends CursorLoader {

	private ContentObserver mObserver;

	public NowPlayingCursorLoder(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		super(context, uri, projection, selection, selectionArgs, sortOrder);
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = getContext().getContentResolver().query(getUri(),
				getProjection(), getSelection(), getSelectionArgs(),
				getSortOrder(), null);
		if (cursor != null) {
			cursor.getCount();
			registerContentObserver(cursor, mObserver);
		}
		return cursor;
	}

	private void registerContentObserver(Cursor cursor, Object object) {
		cursor.registerContentObserver(mObserver);
	}

}
