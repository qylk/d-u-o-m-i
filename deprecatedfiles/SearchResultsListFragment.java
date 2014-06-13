package com.qylk.app.musicplayer.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.activity.MainActivity;
import com.qylk.app.musicplayer.adapter.TrackListAdapter;
import com.qylk.app.musicplayer.fragment.common.SimpleSortableTrackListFragment;
import com.qylk.app.musicplayer.utils.MediaDatabase;

public class SearchResultsListFragment extends SimpleSortableTrackListFragment
		implements OnQueryTextListener, Callback {
	private static String currentSearchText;
	public Handler handler = new Handler(this);

	// private ScheduledExecutorService scheduledExecutor = Executors
	// .newScheduledThreadPool(3);

	@Override
	protected DragSortController onBuildController(DragSortListView dslv) {
		DragSortController controller = super.onBuildController(dslv);
		controller.setSortEnabled(false);
		controller.setRemoveEnabled(false);
		return controller;
	}

//	@Override
//	protected Cursor onCreateCursor() {
//		String selection = "title_key like '" + currentSearchText// 第一个%没有，不匹配中间字符
//				+ "%' or artist_key like '%" + currentSearchText + "%'";
//		return createCursor(selection);
//	}

	private Cursor createCursor(String sel) {
		return MediaDatabase.getSearchCursor(getActivity(), sel);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setBackgroundResource(R.drawable.bg_masks_listview);
		MainActivity a = (MainActivity) getActivity();
		SearchView sv = a.getSearchView();
		sv.setOnQueryTextListener(this);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText != null && newText.length() > 0) {
			currentSearchText = newText;
			addQuery(newText);
		}
		return true;
	}

	private void addQuery(String newText) {
		handler.sendMessageDelayed(handler.obtainMessage(0, newText), 600);
	}

	@Override
	public boolean handleMessage(Message msg) {
		String queryText = (String) msg.obj;
		if (queryText != null && queryText.equals(currentSearchText)) {
			String selection = "title_key like '" + currentSearchText// 第一个%没有，不匹配中间字符
					+ "%' or artist_key like '%" + currentSearchText + "%'";
			TrackListAdapter adapter = (TrackListAdapter) getListAdapter();
			adapter.changeCursor(createCursor(selection));
			adapter.notifyDataSetChanged();
			
		}
		return true;
	}
}
