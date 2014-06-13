package com.qylk.app.musicplayer.deprecated;

import java.io.Closeable;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;

public class MusicListAdapter extends BaseAdapter implements Closeable {
	class ViewHolder {
		public TextView artist;
		public ImageView indicator;
		public TextView title;
	}

	private final StringBuilder mBuilder = new StringBuilder();
	private Context mContext;
	private Cursor mCursor;
	private int mPlayingId;
	private int mSum;

	public MusicListAdapter(Context context, Cursor cur) {
		mCursor = cur;
		this.mContext = context;
		mSum = mCursor.getCount();
	}

	public void changeCursor(Cursor cursor) {
		if (mCursor != null) {
			close();
		}
		if (cursor != null) {
			this.mCursor = cursor;
			mSum = mCursor.getCount();
		}
		notifyDataSetInvalidated();
		notifyDataSetChanged();
	}

	@Override
	public void close() {
		mCursor.close();
	}

	@Override
	public int getCount() {
		return mSum;
	}

	public CharSequence getFirstChar(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getString(mCursor.getColumnIndex(Media.TITLE))
				.subSequence(0, 1);
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		mCursor.moveToPosition(position);
		return mCursor.getInt(0);
	}

	public int getsum() {
		return mSum;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.musiclist_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.ltitle);
			holder.artist = (TextView) convertView.findViewById(R.id.lartist);
			holder.indicator = (ImageView) convertView
					.findViewById(R.id.indicator_playing);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mCursor.moveToPosition(position);

		mBuilder.delete(0, mBuilder.length());
		mBuilder.append(String.valueOf(position + 1))
				.append('.')
				.append(mCursor.getString(mCursor.getColumnIndex(Media.TITLE))
						.trim());
		holder.title.setText(mBuilder.toString());
		holder.artist.setText(mCursor.getString(mCursor
				.getColumnIndex(Media.ARTIST)));
		if (mCursor.getInt(mCursor.getColumnIndex(Media._ID)) == mPlayingId)
			holder.indicator.setVisibility(View.VISIBLE);
		else
			holder.indicator.setVisibility(View.GONE);
		return convertView;
	}

	public void setIdofPlaying(int id) {
		this.mPlayingId = id;
	}
}
