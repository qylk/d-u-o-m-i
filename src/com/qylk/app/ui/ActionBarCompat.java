package com.qylk.app.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.ui.menu.ActionBarMenuItem;
import com.qylk.app.ui.menu.PopWindow;
import com.qylk.app.ui.menu.PopWindowMenu;

public class ActionBarCompat extends RelativeLayout implements
		View.OnClickListener, ActionBar {
	private static final int MAX_ONBAR = 2;
	public TextView title, subtitle;
	private ImageButton actionbar_nav;
	private TextView actionbar_count;
	private LinearLayout extraLayout;
	private View actionbar_title_group;
	private View actionbar_title_point;
	private ImageView new_icon;

	private SparseArray<ActionBarMenuItem> menus = new SparseArray<ActionBarMenuItem>();

	private onMenuItemSelectedListener mlistener;

	interface onMenuItemSelectedListener {
		public void onItemSelected(ActionBarMenuItem item);
	}

	void setonMenuItemSelectedListener(onMenuItemSelectedListener lis) {
		mlistener = lis;
	}

	private OnClickListener mMoreMenuClickListener = new View.OnClickListener() {
		private PopWindow popwindow;

		@Override
		public void onClick(View v) {
			if (popwindow == null) {
				PopWindowMenu window = new PopWindowMenu(getContext(), v);
				window.setMenus(menus, MAX_ONBAR);
				window.setMenuClickListener(ActionBarCompat.this);
				popwindow = window;
				popwindow.show();
			} else {
				popwindow.toggle();
			}
		}
	};

	public ActionBarCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		actionbar_nav = ((ImageButton) findViewById(R.id.actionbar_nav));
		actionbar_count = ((TextView) findViewById(R.id.actionbar_count));
		extraLayout = ((LinearLayout) findViewById(R.id.extraLayout));
		subtitle = (TextView) findViewById(R.id.subtitle);
		actionbar_title_group = findViewById(R.id.actionbar_title_group);
		title = ((TextView) findViewById(R.id.title));
		actionbar_title_point = findViewById(R.id.actionbar_title_point);
		new_icon = ((ImageView) findViewById(R.id.new_icon));
	}

	@Override
	public void onClick(View v) {
		if (mlistener != null)
			mlistener.onItemSelected(menus.get(v.getId()));
	}

	void applyActionMenus() {
		extraLayout.removeAllViews();
		if (menus != null && menus.size() != 0) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			int size = menus.size();
			for (int i = 0; i < size; i++) {
				ActionBarMenuItem item = menus.get(i);
				inflater.inflate(R.layout.actionbar_item, extraLayout);
				ImageButton btn = (ImageButton) extraLayout.getChildAt(i);
				if (i < MAX_ONBAR) {
					btn.setImageDrawable(item.getIcon());
					btn.setOnClickListener(this);
				} else {
					btn.setImageDrawable(getResources().getDrawable(
							R.drawable.actionbar_more));
					btn.setOnClickListener(this);
					break;
				}
			}
		}
	}

	@Override
	public void setTitle(String title) {
		this.title.setText(title);
	}

	@Override
	public void setSubTitle(String subtitle) {
		if (this.subtitle.getVisibility() != View.VISIBLE)
			this.subtitle.setVisibility(View.VISIBLE);
		this.subtitle.setText(subtitle);
	}

	@Override
	public void setTitleIcon(Drawable titleIcon) {
		title.setCompoundDrawables(titleIcon, null, null, null);
	}

	@Override
	public void setHomeIcon(Drawable homeIcon) {
		actionbar_nav.setImageDrawable(homeIcon);
	}

	@Override
	public void setMenuClicklistener(OnClickListener menulistener) {

	}

	@Override
	public void setHomeClicklistener(OnClickListener homelistener) {
		actionbar_nav.setOnClickListener(homelistener);
	}

	@Override
	public void addMenuItem(ActionBarMenuItem item) {
		menus.put(Integer.valueOf(item.getId()), item);
		if (menus.size() > MAX_ONBAR + 1) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.actionbar_item, extraLayout);
		ImageButton btn = (ImageButton) extraLayout.getChildAt(extraLayout
				.getChildCount() - 1);
		if (menus.size() <= MAX_ONBAR) {
			btn.setImageDrawable(item.getIcon());
			btn.setId(menus.size() - 1);
			btn.setOnClickListener(this);
		} else {
			btn.setImageDrawable(getResources().getDrawable(
					R.drawable.actionbar_more));
			btn.setOnClickListener(mMoreMenuClickListener);
		}
	}

	@Override
	public void removeAllMenuItems() {
		menus.clear();
		extraLayout.removeAllViews();
	}

	@Override
	public ActionBarMenuItem getMenu(int id) {
		return menus.get(id);
	}

}
