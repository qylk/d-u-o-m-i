package com.qylk.app.musicplayer.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * FragmentStatePagerAdapter 区别于FramentPagerAdapter 前者在不可见时会销毁fragment，后者则不会这样做
 * 前者适合于大量的子Fagement
 * 
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
	private final ArrayList<FragmentInfo> mTabs = new ArrayList<FragmentInfo>();
	private final ArrayList<String> titles = new ArrayList<String>();
	private Context mContext;

	static final class FragmentInfo {
		private final Class<?> clss;
		private final Bundle args;
		private Fragment fragment;

		FragmentInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles.get(position);
	}

	public ViewPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mContext = context;
		mTabs.clear();
		titles.clear();
	}

	public void addFragment(String title, Class<?> clss, Bundle args) {
		FragmentInfo info = new FragmentInfo(clss, args);
		mTabs.add(info);
		titles.add(title);
	}

	@Override
	public Fragment getItem(int position) {
		FragmentInfo info = mTabs.get(position);
		if (info.fragment == null) {
			info.fragment = Fragment.instantiate(mContext, info.clss.getName(),
					info.args);
		}
		return info.fragment;
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

}
