package com.qylk.app.musicplayer.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class MusicUtils implements ConstantValueDef {

	private static final int COMPRESS_MINI_ARTWORK = 8;
	private static final int COMPRESS_ORIGINAL_ARTWORK = 0;

	public static void activateActivity(Activity activity, int id) {
		Intent intent = new Intent("android.intent.action.PICK");
		switch (id) {
		case ACTIVITY_PLAYING:
			intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/player");
			return;
		case ACTIVITY_LIBRARYLIST:
			intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/library");
			break;
		case ACTIVITY_SEARCH:
			intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/filter");
			break;
		case ACTIVITY_PLAYLIST:
			intent.setDataAndType(Uri.EMPTY, "vnd.android.cursor.dir/playlist");
			break;
		default:
			break;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		activity.finish();
		activity.overridePendingTransition(0, 0);
	}

	public static boolean getBooleanPref(Context context, String name,
			boolean defvalue) {
		return context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE).getBoolean(name, defvalue);
	}

	public static int getIntPref(Context context, String name, int defvalue) {
		return context.getSharedPreferences(context.getPackageName(), 0)
				.getInt(name, defvalue);
	}

	public static String getString(Context context, String name) {
		return context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE).getString(name, null);
	}

	public static void setBooleanPref(Context context, String name, boolean val) {
		SharedPreferences.Editor ed = context.getSharedPreferences(
				context.getPackageName(), 0).edit();
		ed.putBoolean(name, val);
		ed.commit();
	}

	public static void setIntPref(Context context, String name, int val) {
		SharedPreferences.Editor ed = context.getSharedPreferences(
				context.getPackageName(), 0).edit();
		ed.putInt(name, val);
		ed.commit();
	}

	public static Bitmap getArtworkFromResource(Context context, int resid) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		return BitmapFactory.decodeStream(context.getResources()
				.openRawResource(resid), null, opts);
	}

	/**
	 * 艺术家图片
	 * 
	 * @param context
	 * @param id
	 *            艺术家Id
	 * @param compress
	 *            缩放值，>=1,越大，缩放程度越大
	 * @return
	 */
	private static Bitmap getArtwork(Context context, int id, int compress) {
		String index = MediaDatabase.ArtworkIndex(context, id);
		if (index != null) {
			try {
				ZipFile zf = new ZipFile(ARTWORK_PATH);
				ZipEntry ze = zf.getEntry(index);
				InputStream is = zf.getInputStream(ze);
				Bitmap bm = bitampoScale(compress, is);
				is.close();
				return bm;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Bitmap getMiniArtwork(Context context, int id,
			int defaultResId) {
		if (id < 0)
			return getArtworkFromResource(context, defaultResId);
		else {
			Bitmap bitmap = getArtwork(context, id, COMPRESS_MINI_ARTWORK);
			if (bitmap != null)
				return bitmap;
			else
				return getArtworkFromResource(context, defaultResId);
		}
	}

	public static Bitmap getMiniArtwork(Context context, int id) {
		if (id < 0)
			return null;
		else {
			return getArtwork(context, id, COMPRESS_MINI_ARTWORK);
		}
	}

	public static Bitmap getOrginalArtwork(Context context, int id,
			int defaultResId) {
		if (id < 0)
			return getArtworkFromResource(context, defaultResId);
		else {
			Bitmap bitmap = getArtwork(context, id, COMPRESS_ORIGINAL_ARTWORK);
			if (bitmap != null)
				return bitmap;
			else
				return getArtworkFromResource(context, defaultResId);
		}
	}

	public static Bitmap getOrginalArtwork(Context context, int id) {
		if (id < 0)
			return null;
		else {
			return getArtwork(context, id, COMPRESS_ORIGINAL_ARTWORK);
		}
	}

	/**
	 * 图片缩放
	 * 
	 * @param compress
	 *            缩放值 >=0
	 * @param is
	 * @return
	 */
	public static Bitmap bitampoScale(int compress, InputStream is) {
		if (is == null)
			return null;
		if (compress < 0)
			throw new IllegalArgumentException(
					"compress should be positive or zero");
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = compress;
		return BitmapFactory.decodeStream(is, null, options);
	}

	public static void shuffleArray(int[] array, int start, int end) {
		Random random = new Random();
		for (int i = end - 1; i >= start; i--) {
			int index = random.nextInt(i + 1);
			int t = array[i];
			array[i] = array[index];
			array[index] = t;
		}
	}

}
