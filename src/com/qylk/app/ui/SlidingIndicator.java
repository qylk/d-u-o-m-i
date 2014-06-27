package com.qylk.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.qylk.app.musicplayer.R;

public class SlidingIndicator extends LinearLayout {
	private int num;
	private int currentIndex;
	private View[] icons;
	private Drawable indicator = new ColorDrawable();
	private int margin = 0;

	public SlidingIndicator(Context context, AttributeSet attr) {
		super(context, attr);
		TypedArray array = context.obtainStyledAttributes(attr,
				R.styleable.indicator);
		Drawable drawable = array
				.getDrawable(R.styleable.indicator_indicator_drawable);
		if (drawable != null)
			indicator = drawable;
		margin = ((int) array.getDimension(
				R.styleable.indicator_indicator_margin, 0.0f));
		array.recycle();
		setOrientation(LinearLayout.HORIZONTAL);
	}

	public void setCount(int num) {
		int i = 0;
		this.num = num;
		removeAllViews();
		int w = indicator.getIntrinsicWidth();
		int h = indicator.getIntrinsicHeight();
		icons = new View[num];
		while (i < num) {
			icons[i] = new View(getContext());
			icons[i].setBackgroundDrawable(indicator.getConstantState().newDrawable());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,
					h);
			params.setMargins(margin, 0, margin, 0);
			params.gravity = Gravity.CENTER_VERTICAL;
			addView(icons[i], params);
			i++;
		}
	}

	public void setCurrent(int index) {
		for (int i = num - 1; i >= 0; i--)
			icons[i].setSelected(i == index);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getCount() {
		return num;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int num = getChildCount();
		int w = indicator.getIntrinsicWidth();
		int h = indicator.getIntrinsicHeight();
		for (int m = 0; m < num; m++)
			getChildAt(m).measure(
					View.MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
					View.MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
	}
}