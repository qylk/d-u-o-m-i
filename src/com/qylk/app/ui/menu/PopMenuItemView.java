package com.qylk.app.ui.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ActionBar�����menu�ĵ���item��ͼ����xmlΪ��item_popmenu.xml
 *
 */
public class PopMenuItemView extends TextView {

	public PopMenuItemView(Context context, AttributeSet attribute) {
		super(context, attribute);
	}

	public final void init(String title, Drawable iconLeft) {
		setText(title);
		setCompoundDrawables(iconLeft, null, null, null);
	}
}