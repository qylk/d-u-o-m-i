package com.qylk.app.ui.menu;

import android.graphics.drawable.Drawable;

public final class ActionBarMenuItem {
	private String title;
	private Drawable icon;
	private int id = -1;

	private ActionBarMenuItem(String title, Drawable icon) {
		this.title = title;
		this.icon = icon;
		if (this.icon != null)
			this.icon.setBounds(0, 0, this.icon.getIntrinsicWidth(),
					this.icon.getIntrinsicHeight());
	}

	public ActionBarMenuItem(String title, Drawable icon, int id) {
		this(title, icon);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public String getTitle() {
		return this.title;
	}
}
