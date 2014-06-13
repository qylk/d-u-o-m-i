package com.qylk.app.ui.player.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;

public class ListGenerAdapter extends BaseAdapter {

	private static final int[] icons = { R.drawable.icon_mytrack,
			R.drawable.icon_artist, R.drawable.icon_album,
			R.drawable.icon_folder, R.drawable.icon_favor };
	private final String[] titles;
	private Context mContext;

	public ListGenerAdapter(Context context) {
		mContext = context;
		titles = context.getResources().getStringArray(R.array.list_genre);
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = LayoutInflater.from(mContext).inflate(
				R.layout.cell_list_genre_item, null);
		TextView title = (TextView) item.findViewById(R.id.title);
		title.setText(titles[position]);
		Drawable icon = mContext.getResources().getDrawable(icons[position]);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(),
				icon.getIntrinsicHeight());
		title.setCompoundDrawables(icon, null, null, null);
		if (position == 0)
			item.setBackgroundColor(Color.WHITE);
		return item;
	}
}
