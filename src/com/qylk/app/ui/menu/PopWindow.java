package com.qylk.app.ui.menu;

import com.qylk.app.musicplayer.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class PopWindow {
	PopupWindow window;
	ViewGroup content;
	Context mContext;
	View anchor;
	LayoutInflater inflater;
	private boolean useAnchor;

	/**
	 * 
	 * @param context
	 * @param anchor
	 * @param useAnchor
	 *            使用anchor，否则将anchor作为parent
	 */
	public PopWindow(Context context, View anchor, boolean useAnchor) {
		this.mContext = context;
		this.anchor = anchor;
		this.useAnchor = useAnchor;
		this.inflater = LayoutInflater.from(context);
	}

	public final boolean show() {
		if (anchor != null && useAnchor) {
			window.showAsDropDown(
					this.anchor,
					this.anchor.getRight(),
					this.anchor.getTop()
							+ mContext.getResources().getDimensionPixelSize(
									R.dimen.pop_top_distance));
		} else if (anchor != null) {
			window.showAtLocation(anchor, Gravity.CENTER, 0, 0);
		} else {
			throw new NullPointerException(
					"anchor or parent view may not be null");
		}
		return true;
	}

	public final void toggle() {
		if (window.isShowing()) {
			dismiss();
			return;
		}
		show();
	}

	public final boolean dismiss() {
		window.dismiss();
		return true;
	}
}
