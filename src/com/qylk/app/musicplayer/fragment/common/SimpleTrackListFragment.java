package com.qylk.app.musicplayer.fragment.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.TrackListAdapter;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.musicplayer.utils.MediaDatabase;
import com.qylk.app.musicplayer.utils.ServiceProxy;

public class SimpleTrackListFragment extends CommonTrackListFragment implements
		OnItemClickListener {
	// Fragment life cycle is:
	// onAttach()->onCreate()->onCreateView()->onActivityCreated()->onStart()->onResume()->
	// Fragment Active->onPause()->onStop()->onDestroyView()->onDetach()

	private View headerview;

	private boolean headerVisible = true;
	public static final int TYPE_LIBRARY = 0;
	public static final int TYPE_SEARCH = 1;
	public static final int TYPE_PLAYING = 2;
	public static final int TYPE_STATIC_PLAYLIST = 3;
	private int mType = TYPE_LIBRARY;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ListAdapter adapter = getAdapter();
			if (adapter instanceof CursorAdapter) {
				((CursorAdapter) adapter).changeCursor(onCreateCursor());
			}
		}
	};

	public boolean isHeaderViewVisible() {
		return headerVisible;
	}

	public void setHeaderVisible(boolean visible) {
		this.headerVisible = visible;
		if (headerview != null)
			updateHeaderView();
	}

	@Override
	protected int getLayout() {
		return R.layout.listview_expandable;
	}

	public void setListType(int type) {
		this.mType = type;
	}

	public int getListType() {
		return this.mType;
	}

	@Override
	protected int getItemLayout() {
		return R.layout.track_list_item;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListAdapter adapter = getAdapter();
		if (adapter instanceof CursorAdapter) {
			((CursorAdapter) adapter).changeCursor(onCreateCursor());
		}
		mPlayingPositionListener.onReceive(null, null);// ¸üÐÂ
		getListView().setOnItemClickListener(this);

	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		headerview = getHeaderView(inflater);
		container.addView(headerview);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	public void updateHeaderView() {
		if (!headerVisible || getAdapter().getCount() < 2) {
			headerview.setVisibility(View.GONE);
		} else {
			headerview.setVisibility(View.VISIBLE);
		}
	}

	protected View getHeaderView(LayoutInflater inflater) {
		View header = inflater.inflate(R.layout.listview_header, null);
		header.setOnClickListener(mShufflePlay);
		return header;
	}

	private BroadcastReceiver mPlayingPositionListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int id;
			if (intent == null)
				id = ServiceProxy.getTrackId();
			else
				id = intent.getIntExtra("id", 0);
			if (getAdapter() != null) {
				TrackListAdapter adapter = (TrackListAdapter) getAdapter();
				adapter.setPlayingId(id);
				adapter.notifyDataSetChanged();
			}
		}
	};
	private OnClickListener mShufflePlay = new OnClickListener() {

		@Override
		public void onClick(View v) {
			prepareList(true);
			ServiceProxy.play();
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		prepareForPosition(position);
		ServiceProxy.play();
	}

	private void prepareForPosition(int position) {
		TrackListAdapter adapter = (TrackListAdapter) getAdapter();
		TrackIdProvider.getInstance(null).setToPosition(adapter.getCursor(),
				position);
	}

	private void prepareList(boolean shuffle) {
		TrackListAdapter adapter = (TrackListAdapter) getAdapter();
		TrackIdProvider.getInstance(null).changeList(adapter.getCursor(),
				shuffle);
	}

	@Override
	protected ListAdapter onCreateListAdapter(Bundle savedInstanceState) {
		return new TrackListAdapter(getActivity(), getItemLayout(), null,
				new String[] {}, new int[] {});
	}

	protected Cursor onCreateCursor() {
		return MediaDatabase.getLibrary(getActivity());
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mPlayingPositionListener);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Adapter adapter = getAdapter();
		if (adapter != null) {
			if (adapter instanceof CursorAdapter)
				((CursorAdapter) adapter).getCursor().close();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(mPlayingPositionListener, f);
		mPlayingPositionListener.onReceive(getActivity(), null);
		super.onResume();
	}
}
