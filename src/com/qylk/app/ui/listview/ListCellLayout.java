package com.qylk.app.ui.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * prefered as the rootLayout of an ExpandableListItemView,but also compatible
 * with normal ListItemView
 * 
 * @see track_list_item.xml
 */
public class ListCellLayout extends LinearLayout {
	private ImageButton toggleBtn;
	private View mListItemView;
	private View mExtraView;
	private boolean hasExtraView = false;

	public ListCellLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		toggleBtn = (ImageButton) findViewById(android.R.id.toggle);
		int sum = getChildCount();
		if (sum == 1)
			mListItemView = this;
		else if (sum == 2) {
			mListItemView = (View) getChildAt(0);
			mExtraView = (View) getChildAt(1);
			hasExtraView = true;
		}
	}

	public void callTogglePopDownView() {
		toggleBtn.performClick();
	}

	public View getToggleBtn() {
		return toggleBtn;
	}

	public View getListItemView() {
		return mListItemView;
	}

	public View getExtraView() {
		return mExtraView;
	}

	public boolean hasExtraView() {
		return hasExtraView;
	}

}
