package com.qylk.app.ui.menu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.qylk.app.musicplayer.R;

/**
 * PopMenuView used by ActionBarCompat
 * 
 */
public class PopWindowMenu extends PopWindow implements View.OnClickListener {
	private View.OnClickListener listener;
	private int view_height;
	private int item_width = mContext.getResources().getDimensionPixelSize(
			R.dimen.popmenu_item_width);
	private int item_height = mContext.getResources().getDimensionPixelSize(
			R.dimen.popmenu_item_height);

	/**
	 * this popupView will be anchored to the corner of the anchor view
	 * 
	 * @param context
	 * @param anchor
	 */
	public PopWindowMenu(Context context, View anchor) {
		super(context, anchor, true);
		content = (ViewGroup) inflater.inflate(R.layout.popmenu_frame, null);
	}

	public void setMenuClickListener(View.OnClickListener lis) {
		this.listener = lis;
	}

	/**
	 * @param menus
	 * @param offset
	 *            Menu in the List will not show on position lower than this
	 *            value
	 */
	@SuppressWarnings("deprecation")
	public final void setMenus(SparseArray<ActionBarMenuItem> menus, int offset) {
		view_height += 2 * mContext.getResources().getDimensionPixelSize(
				R.dimen.popmenu_top_marign);
		int sum = menus.size();
		for (int i = offset; i < sum; i++) {
			ActionBarMenuItem menu = menus.get(menus.keyAt(i));
			PopMenuItemView menuItem = (PopMenuItemView) inflater.inflate(
					R.layout.popmenu_item, null);
			menuItem.init(menu.getTitle(), menu.getIcon());
			menuItem.setId(menu.getId());
			content.addView(menuItem, LayoutParams.MATCH_PARENT, item_height);
			menuItem.setOnClickListener(this);
			view_height += item_height;
			if (i != sum - 1) {
				View divider = new View(mContext);
				divider.setLayoutParams(new LinearLayout.LayoutParams(
						android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
						2));
				divider.setBackground(mContext.getResources().getDrawable(
						R.drawable.line_h_actionbar_popmenu));
				content.addView(divider);
				view_height += 2;
			}
		}
		window = new PopupWindow(content, item_width, view_height);
		window.setFocusable(true);
		window.setBackgroundDrawable(new BitmapDrawable());// 连同下面一行起作用
		window.setOutsideTouchable(true);// 触摸外边该view消失
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (listener != null)
			listener.onClick(v);
	}
}
