package com.qylk.app.musicplayer.adapter;

import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.widget.SectionIndexer;

public class IndexableTrackListAdapter extends TrackListAdapter implements
		SectionIndexer {
	private char[] chars;
	private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toLowerCase(Locale.ENGLISH);

	public IndexableTrackListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	private char[] collectRawChars(Cursor c) {
		if (c == null)
			throw new NullPointerException("cursor may not be null");
		int sum = c.getCount();
		char[] chars = new char[sum];
		c.moveToFirst();
		for (int i = 0; i < sum; i++) {
			chars[i] = c.getString(TITLE_KEY).charAt(0);
			c.moveToNext();
		}
		return chars;
	}

	@Override
	public Object[] getSections() {
		int len = alphabet.length();
		char[] sts = new char[len];
		alphabet.getChars(0, alphabet.length() - 1, sts, 0);

		Character[] st = new Character[len];
		for (int i = 0; i < len; i++)
			st[i] = Character.valueOf(sts[i]);
		return st;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= alphabet.length())
			section = alphabet.length() - 1;
		int count = getCount();
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < count; j++) {
				if (i == 0) {
					for (int k = 0; k <= 9; k++) {
						if (chars[j] == 30 + k)
							return j;
					}
				} else {
					if (chars[j] == alphabet.charAt(i))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		char s = chars[position];
		if (s >= '0' && s <= '9')
			s = '#';
		else if (s < 'a' || s > 'z') {
			return 0;
		}
		return alphabet.indexOf(s);
	}

	@Override
	public void changeCursor(Cursor cursor) {
		chars = collectRawChars(cursor);
		super.changeCursor(cursor);
	}
}
