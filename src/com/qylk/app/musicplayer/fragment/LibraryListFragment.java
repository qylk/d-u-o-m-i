package com.qylk.app.musicplayer.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.IndexableTrackListAdapter;
import com.qylk.app.musicplayer.fragment.common.SimpleTrackListFragment;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.menu.ActionBarMenuItem;

public class LibraryListFragment extends SimpleTrackListFragment {
	private static int mLastListPosCourse = -1;
	private static int mLastListPosFine = -1;

	@Override
	protected ListAdapter onCreateListAdapter(Bundle savedInstanceState) {
		return new IndexableTrackListAdapter(getActivity(), getItemLayout(),
				null, new String[] {}, new int[] {});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (mLastListPosCourse >= 0)
			getListView().setSelectionFromTop(mLastListPosCourse,
					mLastListPosFine);
	}

	@Override
	public void onDestroy() {
		ListView lv = getListView();
		mLastListPosCourse = lv.getFirstVisiblePosition();
		View cv = lv.getChildAt(0);
		if (cv != null) {
			mLastListPosFine = cv.getTop();
		}
		super.onDestroy();
	}

	@Override
	protected int getLayout() {
		return R.layout.listview_indexable;
	}

	@Override
	protected void onCreateActionBar(ActionBar act) {
		act.addMenuItem(new ActionBarMenuItem("ËÑË÷0", getResources()
				.getDrawable(R.drawable.actionbar_edit), 0));
		super.onCreateActionBar(act);
	}
}
