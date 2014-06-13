package com.qylk.app.ui.menu;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.qylk.app.musicplayer.R;
import com.qylk.app.ui.listview.ListCellLayout;

/**
 * Extra viewLayout that shows behind every TrackItemView
 *
 */
public class PopdownView extends LinearLayout implements View.OnClickListener {

	/**
	 * MAX num of Menus embebd in this view
	 */
	private static final int MAX = 10;
	private int[] iconRes;
	private LayoutInflater inflater;
	private final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			0, LayoutParams.MATCH_PARENT, 1.0F);
	private MenuItemClickedListener listener;

	public PopdownView(Context context) {
		this(context, null);
	}

	public PopdownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		setBackgroundColor(Color.parseColor("#ff484848"));
		this.inflater = LayoutInflater.from(context);
	}


	private void update() {
		int screenwidth = getResources().getDisplayMetrics().widthPixels;
		int itemwidth = getResources().getDimensionPixelSize(
				R.dimen.track_menu_item_width);
		int maxReplace = screenwidth / itemwidth;
		if (this.iconRes == null)
			return;
		int sum = getChildCount();
		View child;
		int m = 0;
		for (int i = 0; i < sum; i++) {
			child = getChildAt(i);
			if (Arrays.binarySearch(iconRes, child.getId()) < 0) {
				child.setVisibility(View.GONE);
			} else if (m < maxReplace - 1) {
				m++;
				child.setVisibility(View.VISIBLE);
			} else if (m < maxReplace) {
				m++;
				child.setId(TrackMenuItemView.MORE);
				child.setVisibility(View.VISIBLE);
			} else {
				child.setVisibility(View.GONE);
			}
		}
	}

	public ArrayList<View> getMenus() {
		int sum = getChildCount();
		ArrayList<View> menus = new ArrayList<View>(sum);
		for (int i = 0; i < sum; i++) {
			View child = getChildAt(i);
			if (Arrays.binarySearch(this.iconRes, child.getId()) >= 0) {
				menus.add((TrackMenuItemView) child);
			}
		}
		return menus;
	}

	public void setIds(int[] res) {
		if (this.iconRes == res)
			return;
		else
			this.iconRes = res;
		if (getVisibility() == View.VISIBLE)
			update();
	}

	@Override
	public boolean hasFocusable() {
		return false;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		for (int i = 0; i < MAX; i++) {
			TrackMenuItemView item = (TrackMenuItemView) inflater.inflate(
					R.layout.menu_popdown_item, null);
			addView(item, params);
			item.setOnClickListener(this);
			item.setId(i);
		}
	}

	public abstract interface MenuItemClickedListener {
		public abstract void onClick(View v, TrackMenuItemView item);
	}

	public final void setMenuItemClickedListener(
			MenuItemClickedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		((ListCellLayout) this.getParent()).callTogglePopDownView();
		if (this.listener != null)
			this.listener.onClick(this, (TrackMenuItemView) v);
	}
}
