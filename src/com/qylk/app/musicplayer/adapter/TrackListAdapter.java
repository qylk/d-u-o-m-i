package com.qylk.app.musicplayer.adapter;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.NowPlayingCursor;

public class TrackListAdapter extends SimpleCursorAdapter implements
		DragSortListener {
	protected static final int _ID = 0;
	protected static final int TITLE_KEY = 3;
	private int playingId;
	private final StringBuilder mBuilder = new StringBuilder();
	private int offset;

	static class ViewHolder {
		TextView num;
		TextView line1;
		TextView line2;
		char[] buffer1;
		int color;
		CharArrayBuffer buffer2;
	}

	@SuppressWarnings("deprecation")
	public TrackListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = super.newView(context, cursor, null);
		ViewHolder vh = new ViewHolder();
		vh.num = (TextView) v.findViewById(R.id.num);
		vh.line1 = (TextView) v.findViewById(R.id.line1);
		vh.line2 = (TextView) v.findViewById(R.id.line2);
		vh.buffer1 = new char[20];
		vh.buffer2 = new CharArrayBuffer(20);
		vh.color = vh.line1.getCurrentTextColor();
		v.setTag(vh);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder vh = (ViewHolder) view.getTag();
		if (playingId == cursor.getInt(_ID)) {
			int color = Color.parseColor("#ff78DD0a");
			vh.line1.setTextColor(color);
			vh.line2.setTextColor(color);
			vh.num.setTextColor(color);
		} else {
			vh.line1.setTextColor(vh.color);
			vh.num.setTextColor(vh.color);
			vh.line2.setTextColor(Color.GRAY);
		}
		final StringBuilder builder = mBuilder;
		builder.delete(0, builder.length());
		vh.num.setText(String.valueOf(cursor.getPosition() + 1 + offset));
		builder.append(cursor.getString(1));
		int len = builder.length();
		if (vh.buffer1.length < len) {
			vh.buffer1 = new char[len];
		}
		builder.getChars(0, len, vh.buffer1, 0);
		vh.line1.setText(vh.buffer1, 0, len);
		cursor.copyStringToBuffer(2, vh.buffer2);
		vh.line2.setText(vh.buffer2.data, 0, vh.buffer2.sizeCopied);
	}

	public void setPlayingId(int id) {
		this.playingId = id;
		notifyDataSetChanged();
	}

	@Override
	public void drop(int from, int to) {
		NowPlayingCursor c = (NowPlayingCursor) getCursor();
		c.moveItem(from, to);
		notifyDataSetInvalidated();
		notifyDataSetChanged();
	}

	@Override
	public void drag(int from, int to) {
	}

	@Override
	public void remove(int which) {
		NowPlayingCursor c = (NowPlayingCursor) getCursor();
		c.removeItem(which);
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		Cursor c = getCursor();
		if (c != null) {
			c.moveToPosition(position);
			return c.getInt(_ID);
		} else
			return -1;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		if (cursor != null && cursor instanceof NowPlayingCursor)
			offset = ((NowPlayingCursor) cursor).getOffset();
	}

	public void updateOffset() {
		Cursor cursor = getCursor();
		if (cursor != null && cursor instanceof NowPlayingCursor)
			offset = ((NowPlayingCursor) cursor).getOffset();
	}
}
