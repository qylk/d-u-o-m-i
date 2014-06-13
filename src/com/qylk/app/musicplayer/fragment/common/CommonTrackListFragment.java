package com.qylk.app.musicplayer.fragment.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.listview.ExpandableListView;
import com.qylk.app.ui.listview.ExpandableListView.OnItemExpandCollapseListener;
import com.qylk.app.ui.menu.PopdownView;
import com.qylk.app.ui.menu.PopdownView.MenuItemClickedListener;
import com.qylk.app.ui.menu.TrackMenuItemView;

public abstract class CommonTrackListFragment extends ActionBarFragment
		implements OnItemExpandCollapseListener, MenuItemClickedListener {
	private ListView listview;
	private ListAdapter mAdapter;

	public ListAdapter getAdapter() {
		return mAdapter;
	}

	/**
	 * 不可重复，并且为顺序排列
	 */
	protected static int[] Default_TrackMenu = { TrackMenuItemView.LOVE,
			TrackMenuItemView.ADD, TrackMenuItemView.QUEUE,
			TrackMenuItemView.DETAILS, TrackMenuItemView.SHARE,
			TrackMenuItemView.DELETE };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		listview = (ListView) inflater.inflate(getLayout(), null);
		container.addView(listview, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mAdapter = onCreateListAdapter(savedInstanceState);
		if (listview.getAdapter() == null)
			listview.setAdapter(mAdapter);
		if (listview instanceof ExpandableListView) {
			((ExpandableListView) listview).setItemExpandCollapseListener(this);
		}
		listview.setFastScrollEnabled(true);
		super.onActivityCreated(savedInstanceState);
	}

	protected abstract ListAdapter onCreateListAdapter(Bundle savedInstanceState);

	@Override
	public void onDestroy() {
		listview.setAdapter(null);
		mAdapter = null;
		super.onDestroy();
	}

	protected int getItemLayout() {
		return R.layout.track_list_item;
	}

	protected int getLayout() {
		return R.layout.listview_expandable;
	}

	public final ListView getListView() {
		return listview;
	}

	public final ListAdapter getListAdapter() {
		return mAdapter;
	}

	@Override
	public void onExpand(View itemView, int position) {
		onInitExpandView(itemView);
		((PopdownView) itemView).setMenuItemClickedListener(this);
	}

	protected void onInitExpandView(View itemView) {
		((PopdownView) itemView).setIds(Default_TrackMenu);
	}

	@Override
	public void onClick(View v, TrackMenuItemView item) {
		int position = v.getId();
		int id = (int) mAdapter.getItemId(position);
		switch (item.getId()) {
		case TrackMenuItemView.LOVE:

			break;
		case TrackMenuItemView.ADD:

			break;
		case TrackMenuItemView.DETAILS:

			break;
		case TrackMenuItemView.QUEUE:
			TrackIdProvider.getInstance(null).addToNext(id);
			break;
		case TrackMenuItemView.SHARE:

			break;
		case TrackMenuItemView.RINGTONE:

			break;
		case TrackMenuItemView.DELETE:

			break;
		case TrackMenuItemView.ARTIST:

			break;
		case TrackMenuItemView.ALBUM:

			break;
		case TrackMenuItemView.MORE:

			break;
		default:
			break;
		}
	}

	@Override
	public void onCollapse(View itemView, int position) {
		// do nothing at the moment
	}
}
