package com.qylk.app.musicplayer.service;

import java.util.Arrays;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;

import com.qylk.app.musicplayer.utils.MEDIA.AUDIO;
import com.qylk.app.musicplayer.utils.MediaDatabase;
import com.qylk.app.musicplayer.utils.MyInteger;

public class NowPlayingCursor extends AbstractCursor {
	private static final int MAX_LOAD = 50;
	private Context mContext;
	private Cursor mCurrentPlaylistCursor; // updated in onMove
	private int[] mCursorIdxs;
	private int[] mNowPlaying;
	private MyInteger offset = new MyInteger(0);
	private int mSize; // size of the queue

	public NowPlayingCursor(Context context) {
		mContext = context;
		makeNowPlayingCursor();
	}

	public int getOffset() {
		return offset.intValue();
	}

	@Override
	public String[] getColumnNames() {
		return MediaDatabase.cols;
	}

	@Override
	public int getCount() {
		return mSize;
	}

	@Override
	public double getDouble(int column) {
		return mCurrentPlaylistCursor.getDouble(column);
	}

	@Override
	public float getFloat(int column) {
		return mCurrentPlaylistCursor.getFloat(column);
	}

	@Override
	public int getInt(int column) {
		try {
			return mCurrentPlaylistCursor.getInt(column);
		} catch (Exception ex) {
			onChange(true);
			return 0;
		}
	}

	@Override
	public long getLong(int column) {
		try {
			return mCurrentPlaylistCursor.getLong(column);
		} catch (Exception ex) {
			onChange(true);
			return 0;
		}
	}

	@Override
	public short getShort(int column) {
		return mCurrentPlaylistCursor.getShort(column);
	}

	@Override
	public String getString(int column) {
		try {
			return mCurrentPlaylistCursor.getString(column);
		} catch (Exception ex) {
			onChange(true);
			return "";
		}
	}

	@Override
	public int getType(int column) {
		return mCurrentPlaylistCursor.getType(column);
	}

	@Override
	public boolean isNull(int column) {
		return mCurrentPlaylistCursor.isNull(column);
	}

	private void makeNowPlayingCursor() {
		mNowPlaying = TrackIdProvider.getInstance(null).getCopyList(offset,
				MAX_LOAD);
		mSize = mNowPlaying.length;
		if (mSize == 0) {
			return;
		}
		StringBuilder where = new StringBuilder();
		where.append(AUDIO.FIELD_ID + " IN (");
		for (int i = 0; i < mSize; i++) {
			where.append(mNowPlaying[i]);
			if (i < mSize - 1) {
				where.append(",");
			}
		}
		where.append(")");
		if (mCurrentPlaylistCursor != null)
			mCurrentPlaylistCursor.close();
		mCurrentPlaylistCursor = MediaDatabase.getPlayingList(mContext,
				where.toString());
		if (mCurrentPlaylistCursor == null) {
			mSize = 0;
			return;
		}
		mCurrentPlaylistCursor.moveToFirst();
		mCursorIdxs = new int[mSize];
		System.arraycopy(mNowPlaying, 0, mCursorIdxs, 0, mSize);
		Arrays.sort(mCursorIdxs);// ascending order
	}

	public void moveItem(int from, int to) {
		doMoveLocal(from, to);
		from += offset.intValue();
		to += offset.intValue();
		TrackIdProvider idp = TrackIdProvider.getInstance(null);
		idp.moveItems(from, to);
		onMove(-1, mCurrentPlaylistCursor.getPosition()); // update the
															// underlying cursor
	}

	private void doMoveLocal(int from, int to) {
		int tmp = mNowPlaying[from];
		if (from < to) {
			for (int i = from; i < to; i++) {
				mNowPlaying[i] = mNowPlaying[i + 1];
			}
		} else {
			for (int i = from; i > to; i--) {
				mNowPlaying[i] = mNowPlaying[i - 1];
			}
		}
		mNowPlaying[to] = tmp;
	}

	@Override
	public boolean onMove(int oldPosition, int newPosition) {
		if (oldPosition == newPosition)
			return true;
		if (mNowPlaying == null || mCursorIdxs == null || newPosition >= mSize) {
			return false;
		}
		int newid = mNowPlaying[newPosition];
		int crsridx = Arrays.binarySearch(mCursorIdxs, newid);
		mCurrentPlaylistCursor.moveToPosition(crsridx);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void deactivate() {
		if (mCurrentPlaylistCursor != null)
			mCurrentPlaylistCursor.deactivate();
	}

	public boolean removeItem(int which) {
		doRemoveLocal(which);
		which += offset.intValue();
		TrackIdProvider idp = TrackIdProvider.getInstance(null);
		if (!idp.remove(which, which)) {
			return false; // delete failed
		}
		onMove(-1, mCurrentPlaylistCursor.getPosition());
		return true;
	}

	private void doRemoveLocal(int which) {
		mSize--;
		int i = which;
		while (i < mSize) {
			mNowPlaying[i] = mNowPlaying[i + 1];
			i++;
		}
		// no need to remove it from mCursorIdx,dose not matter ListView
	}

	@Override
	public boolean requery() {
		makeNowPlayingCursor();
		return true;
	}

	@Override
	public void close() {
		if (mCurrentPlaylistCursor != null)
			mCurrentPlaylistCursor.close();
		super.close();
	}
}