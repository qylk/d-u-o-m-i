package com.qylk.app.ui.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;

/**
 * MenuItemView extend TextView。 the icon draw to the left of text is determined
 * by its id，see {@link #setId(int)}; there are 9 embeded alternative icons which id froms
 * 0 to 8.
 */
public class TrackMenuItemView extends TextView {
	private int id;
	private String[] title;
	private Drawable icon;
	// 须对应arrays.xml中的字符串顺序
	public static final int LOVE = 0;
	public static final int ADD = 1;
	public static final int DETAILS = 2;
	public static final int QUEUE = 3;
	public static final int SHARE = 4;
	public static final int RINGTONE = 5;
	public static final int DELETE = 6;
	public static final int ARTIST = 7;
	public static final int ALBUM = 8;
	public static final int MORE = 9;

	public TrackMenuItemView(Context context) {
		this(context, null);
	}

	public TrackMenuItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TrackMenuItemView(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public final void setId(int id) {
		this.id = id;
		int res = 0;
		if (title == null)
			title = getResources().getStringArray(R.array.track_menu);
		switch (id) {
		case LOVE:
			res = R.drawable.popdown_collect;
			break;
		case ADD:
			res = R.drawable.popdown_add;
			break;
		case DETAILS:
			res = R.drawable.popdown_detail;
			break;
		case QUEUE:
			res = R.drawable.popdown_queue;
			break;
		case SHARE:
			res = R.drawable.popdown_share;
			break;
		case RINGTONE:
			res = R.drawable.popdown_ringtone;
			break;
		case DELETE:
			res = R.drawable.popdown_del;
			break;
		case ARTIST:
			res = R.drawable.popdown_artist;
			break;
		case ALBUM:
			res = R.drawable.popdown_album;
			break;
		case MORE:
			res = R.drawable.popdown_more;
			break;
		}
		icon = getResources().getDrawable(res);
		if (icon != null)
			icon.setBounds(0, 0, icon.getIntrinsicWidth(),
					icon.getIntrinsicHeight());
		setCompoundDrawables(null, icon, null, null);
		setText(title[id]);
	}
}
