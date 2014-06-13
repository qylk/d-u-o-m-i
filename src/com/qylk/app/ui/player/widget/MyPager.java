package com.qylk.app.ui.player.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.qylk.app.musicplayer.R;
import com.qylk.app.ui.SlidingIndicator;

public class MyPager extends LinearLayout implements
		ViewPager.OnPageChangeListener {
	private SlidingIndicator indicator;
	private ViewPager pager;

	public MyPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		indicator = (SlidingIndicator) findViewById(R.id.indicator);
		pager = (ViewPager) findViewById(R.id.pager);
	}

	public void setCurrentItem(int item) {
		pager.setCurrentItem(item);
	}

	public void setAdapter(PagerAdapter adapter) {
		int count = adapter.getCount();
		if (count == 0)
			throw new IllegalStateException(
					"PagerAdapter is empty befor initial indicator!!");
		pager.setAdapter(adapter);
		indicator.setCount(count);
		indicator.setCurrent(0);
		pager.setOnPageChangeListener(this);
		pager.setAnimationCacheEnabled(false);
	}

	public void setOffscreenPageLimit(int num) {
		pager.setOffscreenPageLimit(num);
	};

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		indicator.setCurrent(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

}
