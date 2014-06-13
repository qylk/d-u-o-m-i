package com.qylk.app.ui;

import com.qylk.app.ui.menu.ActionBarMenuItem;

import android.graphics.drawable.Drawable;
import android.view.View;

public interface ActionBar {
	public void setTitle(String title);

	public void setSubTitle(String subTitle);

	public void setTitleIcon(Drawable titleIcon);

	public void setHomeIcon(Drawable homeIcon);

	public void setBackground(Drawable background);

	public void setBackgroundColor(int color);

	void setMenuClicklistener(View.OnClickListener menulistener);

	public void setHomeClicklistener(View.OnClickListener homelistener);

	public void addMenuItem(ActionBarMenuItem item);

	public void removeAllMenuItems();

	public void setVisibility(int visible);
	
	public ActionBarMenuItem getMenu(int id);
}
