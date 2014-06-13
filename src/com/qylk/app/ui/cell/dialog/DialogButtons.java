package com.qylk.app.ui.cell.dialog;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class DialogButtons extends LinearLayout {

	public DialogButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		ArrayList<View> list = new ArrayList<View>();
		int childCount = getChildCount();
		for (int j = 0; j < childCount; j++) {
			View child = getChildAt(j);
			if (child.getVisibility() == View.VISIBLE)
				list.add(child);
		}
		int sum = list.size();
		if (sum == 0)
			return;
		float density = getResources().getDisplayMetrics().density;
		int width = r - l - getPaddingLeft() - getPaddingRight();
		int margin = (int) (density * 4.0F);
		for (int i = 0; i < sum; i++) {
			View childv = getChildAt(i);
			ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childv
					.getLayoutParams();
			if (i == 0 || i == sum - 1) {
				params.leftMargin = margin;
				params.rightMargin = margin;
			} else {
				params.leftMargin = margin / 2;
				params.rightMargin = margin / 2;
			}
			childv.setLayoutParams(params);
			childv.measure(MeasureSpec.EXACTLY
					+ (width - margin * (2 * (sum - 1))) / sum,
					MeasureSpec.EXACTLY + params.height);
			childv.layout(0, 0, childv.getMeasuredWidth(),
					childv.getMeasuredHeight());
		}
		super.onLayout(changed, l, t, r, b);
	}
}
