package com.qylk.app.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.Checkable;

public class CheckBox extends Button implements Checkable {
	private onCheckStateChangedListener listener;
	private boolean cheched = true;

	public CheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setBackground(Drawable background) {
		super.setBackground(background);
		if (background != null)
			background.setCallback(this);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		int[] retVal = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked())
			mergeDrawableStates(retVal,
					new int[] { android.R.attr.state_checked });
		return retVal;
	}

	// @Override
	// public void setBackgroundDrawable(Drawable background) {
	// super.setBackgroundDrawable(background);
	// if (background != null)
	// background.setCallback(this);
	// }

	@Override
	public void setChecked(boolean checked) {
		if (this.cheched != checked) {
			this.cheched = checked;
			refreshDrawableState();
			if (listener != null)
				listener.onCheckStateChanged(this.cheched);
		}
	}

	public interface onCheckStateChangedListener {
		public void onCheckStateChanged(boolean checked);
	}

	public void setonCheckStateChangedListener(
			onCheckStateChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean isChecked() {
		return cheched;
	}

	@Override
	public void toggle() {
		setChecked(!this.cheched);
	}
}
