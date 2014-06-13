package com.qylk.app.musicplayer.utils;

import java.io.File;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.qylk.app.musicplayer.utils.MEDIA.AUDIO;

public class MediaDatabase {
	public static final String[] cols = { AUDIO.FIELD_ID, AUDIO.FIELD_TITLE,
			AUDIO.FIELD_ARTIST, AUDIO.FIELD_TITLE_KEY };
	private static final String[] TAGcols = { AUDIO.TAG.FIELD_LANG,
			AUDIO.TAG.FIELD_GENRE, AUDIO.TAG.FIELD_RHYTHM,
			AUDIO.TAG.FIELD_SING_METHOD, AUDIO.TAG.FIELD_AGE,
			AUDIO.TAG.FIELD_SUBJECT, AUDIO.TAG.FIELD_INSTRUMENT,
			AUDIO.TAG.FIELD_LIB_ID, AUDIO.TAG.FIELD_TIME_MODIFIED };

	public static String getPathForId(Context context, int id) {
		Cursor c = context.getContentResolver().query(AUDIO.URI,
				new String[] { AUDIO.FIELD_PATH }, "_id=" + id, null, null);
		String path = null;
		if (c != null && c.moveToFirst()) {
			path = c.getString(0);
			c.close();
		}
		return path;
	}

	public static Cursor getLibrary(Context context) {
		return context.getContentResolver().query(AUDIO.URI, cols, null, null,
				AUDIO.FIELD_TITLE_KEY + " ASC");
	}

	public static Cursor getPlayingList(Context context, String where) {
		return context.getContentResolver().query(AUDIO.URI, cols, where, null,
				null);
	}

	public static TrackTAG getTag(Context context, int id) {
		Uri trackuri = ContentUris.withAppendedId(AUDIO.TAG.URI, id);
		Cursor c = context.getContentResolver().query(trackuri, TAGcols, null,
				null, null);
		TrackTAG tag = null;
		if (c != null && c.moveToFirst()) {
			tag = new TrackTAG(c.getInt(0), c.getInt(1), c.getInt(2),
					c.getInt(3), c.getInt(4), c.getInt(5), c.getInt(6),
					c.getInt(7));
			c.close();
		}
		return tag;
	}

	public static int updateTag(Context context, int id, int lib_id, int[] tag) {
		Uri trackuri = ContentUris.withAppendedId(AUDIO.TAG.URI, id);
		ContentValues cv = new ContentValues();
		cv.put(TAGcols[0], tag[0]);
		cv.put(TAGcols[1], tag[1]);
		cv.put(TAGcols[2], tag[2]);
		cv.put(TAGcols[3], tag[3]);
		cv.put(TAGcols[4], tag[4]);
		cv.put(TAGcols[5], tag[5]);
		cv.put(TAGcols[6], tag[6]);
		cv.put(TAGcols[8], System.currentTimeMillis() / 1000);
		return context.getContentResolver().update(trackuri, cv, null, null);
	}

	public static int[] getSearchList(Context context, String selection) {
		Cursor c = context.getContentResolver().query(AUDIO.URI,
				new String[] { AUDIO.FIELD_ID }, selection, null, null);
		int[] list = null;
		if (c != null && c.moveToFirst()) {
			int sum = c.getCount();
			list = new int[sum];
			int i = 0;
			while (i < sum) {
				list[i++] = c.getInt(0);
				c.moveToNext();
			}
			c.close();
		}
		return list;
	}

	public static Cursor getSearchCursor(Context context, String selection) {
		if (selection == null)
			return null;
		return context.getContentResolver().query(
				AUDIO.URI,
				new String[] { AUDIO.FIELD_ID, AUDIO.FIELD_TITLE,
						AUDIO.FIELD_ARTIST }, selection, null, null);
	}

	public static void updatePlayTimes(Context context, int id) {
		Uri uri = ContentUris.withAppendedId(AUDIO.TAG.URI, id);
		Cursor c = context.getContentResolver().query(uri,
				new String[] { AUDIO.TAG.FIELD_PLAY_TIMES }, null, null, null);
		if (c != null && c.moveToFirst()) {
			int play_times = c.getInt(0);
			c.close();
			ContentValues cv = new ContentValues();
			cv.put(AUDIO.TAG.FIELD_PLAY_TIMES, play_times + 1);
			context.getContentResolver().update(uri, cv, null, null);
		}
	}

	public static String ArtworkIndex(Context context, int id) {
		Uri uri = ContentUris.withAppendedId(AUDIO.ARTIST.URI, id);
		Cursor c = context.getContentResolver().query(uri,
				new String[] { AUDIO.ARTIST.FIELD_INDEX }, null, null, null);
		if (c != null && c.moveToFirst()) {
			String index = c.getString(0);
			c.close();
			return index;
		}
		return null;
	}

	public static boolean isMediaScannerScanning(Context context) {
		boolean result = false;
		Cursor cursor = context.getContentResolver().query(
				MediaStore.getMediaScannerUri(),
				new String[] { MediaStore.MEDIA_SCANNER_VOLUME }, null, null,
				null);
		if (cursor != null) {
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				result = "external".equals(cursor.getString(0));
			}
			cursor.close();
		}
		return result;
	}

	public static Uri getContentURIForPath(String path) {
		return Uri.fromFile(new File(path));
	}
}