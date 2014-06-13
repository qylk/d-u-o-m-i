package com.qylk.app.musicplayer.deprecated;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public final class onBarMenuItem {
	private static final Drawable defaultIcon = new ColorDrawable();
	private Drawable menuIcon = defaultIcon;
	private View mActionView;
	private int menuId;
	private String title;

	public final void setId(int id) {
		menuId = id;
	}

	public final void setIcon(Drawable icon) {
		if (icon != null)
			menuIcon = icon.getConstantState().newDrawable();
	}

	// public final void a(View v) {
	// if (listener != null)
	// listener.onClick(v);
	// }

	// public final void setOnClickListener(onClickListener listener) {
	// this.listener = listener;
	// }

	public final void a(String paramString) {
		title = paramString;
	}

	public final int getId() {
		return menuId;
	}

	public final void setActionView(View v) {
		mActionView = v;
	}

	public final Drawable c() {
		return menuIcon;
	}

	public final View getActionView() {
		return mActionView;
	}

	public final String getTitle() {
		return title;
	}
}
