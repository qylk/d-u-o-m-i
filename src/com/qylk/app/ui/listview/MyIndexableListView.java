package com.qylk.app.ui.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;

public class MyIndexableListView extends ExpandableListView implements
		ExpandableListView.OnScrollListener {
	private IndexScroller mScroller = null;
	private boolean mIsFastScrollEnabled;
	private boolean show;

	public MyIndexableListView(Context context) {
		this(context, null);
	}

	public MyIndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (enabled) {
			setVerticalScrollBarEnabled(false);
			if (mScroller == null)
				mScroller = new IndexScroller(getContext(), this);
			setOnScrollListener(this);
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mScroller != null && mScroller.isVisible())
			return true;
		else
			return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		show = scrollState != SCROLL_STATE_IDLE;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (show && mScroller != null) {
			mScroller.show();
			mScroller.setHighLightPosition((firstVisibleItem + 2));
		}
	}

}