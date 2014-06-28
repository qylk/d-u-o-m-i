package com.qylk.app.musicplayer.fragment.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.TrackListAdapter;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.musicplayer.utils.MEDIA;
import com.qylk.app.musicplayer.utils.MEDIA.AUDIO;
import com.qylk.app.musicplayer.utils.MediaDatabase;
import com.qylk.app.musicplayer.utils.ServiceProxy;

public class SimpleTrackListFragment extends CommonTrackListFragment implements
		OnItemClickListener,
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
	// Fragment life cycle is:
	// onAttach()->onCreate()->onCreateView()->onActivityCreated()->onStart()->onResume()->
	// Fragment Active->onPause()->onStop()->onDestroyView()->onDetach()

	private View headerview;
	private boolean headerVisible = true;
	public static final String[] cols = { AUDIO.FIELD_ID, AUDIO.FIELD_TITLE,
			AUDIO.FIELD_ARTIST, AUDIO.FIELD_TITLE_KEY };

	public boolean isHeaderViewVisible() {
		return headerVisible;
	}

	public void setHeaderVisible(boolean visible) {
		this.headerVisible = visible;
	}

	@Override
	protected int getLayout() {
		return R.layout.listview_expandable;
	}

	@Override
	protected int getItemLayout() {
		return R.layout.track_list_item;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPlayingPositionListener.onReceive(null, null);// 更新
		getListView().setOnItemClickListener(this);
		getLoaderManager().initLoader(0, getArguments(), this);
		if (getArguments() != null && getArguments().containsKey("title")) {
			setTitle(getArguments().getString("title"));
		}
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		headerview = getHeaderView(inflater);
		container.addView(headerview);
		headerview.setVisibility(View.GONE);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	/**
	 * 根据数量，决定是否显示随机播放按钮
	 */
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

	private BroadcastReceiver updateListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			getLoaderManager().restartLoader(0, getArguments(),
					SimpleTrackListFragment.this);
		}
	};

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
		TrackIdProvider.getInstance().setToPosition(adapter.getCursor(),
				position);
	}

	private void prepareList(boolean shuffle) {
		TrackListAdapter adapter = (TrackListAdapter) getAdapter();
		TrackIdProvider.getInstance().changeList(adapter.getCursor(),
				shuffle);
	}

	@Override
	protected ListAdapter onCreateListAdapter(Bundle savedInstanceState) {
		return new TrackListAdapter(getActivity(), getItemLayout(), null,
				new String[] {}, new int[] {});
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mPlayingPositionListener);
		getActivity().unregisterReceiver(updateListListener);
		super.onPause();
	}

	@Override
	public void onResume() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(mPlayingPositionListener, f);
		mPlayingPositionListener.onReceive(getActivity(), null);
		IntentFilter f2 = new IntentFilter();
		f2.addAction(MEDIA.INTENT_SCAN_DONE);
		getActivity().registerReceiver(updateListListener, f2);
		super.onResume();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), AUDIO.URI, MediaDatabase.cols,
				arg1 == null ? null : arg1.getString("selection"), null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor newCursor) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		((CursorAdapter) getAdapter()).swapCursor(newCursor);
		updateHeaderView();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		((CursorAdapter) getAdapter()).swapCursor(null);
	}
}
