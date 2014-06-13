package com.qylk.app.ui.player.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.TrackListAdapter;
import com.qylk.app.musicplayer.fragment.NowPlayingCursor;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.musicplayer.utils.ServiceProxy;

public class QueueView extends RelativeLayout implements OnItemClickListener {
	private View clearQueue;
	private DragSortListView listview;
	private TrackListAdapter mAdapter;

	public QueueView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		clearQueue = findViewById(R.id.queue_clean);
		clearQueue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TrackIdProvider.getInstance(null).clear();
				mAdapter.changeCursor(null);
				listview.invalidate();
				ServiceProxy.stop();
			}
		});
		listview = (DragSortListView) findViewById(R.id.list);
		mAdapter = new TrackListAdapter(getContext(),
				R.layout.track_list_item_dark, null, new String[] {},
				new int[] {});
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		DragSortController mController = buildController(listview);
		listview.setFloatViewManager(mController);
		listview.setOnTouchListener(mController);
	}

	private DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.num);// 拖拽把柄，默认每个ItemView为DragHandle
		// controller.setFlingHandleId(R.id.num);// 删除把柄
		controller.setSortEnabled(true);
		controller.setRemoveEnabled(false);
		controller.setBackgroundColor(getResources().getColor(// FloatView的背景色
				R.color.float_color));
		controller.setDragInitMode(DragSortController.ON_DRAG);
		// controller.setRemoveMode(DragSortController.FLING_REMOVE);// 滑动删除模式
		return controller;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		NowPlayingCursor cursor = (NowPlayingCursor) mAdapter.getCursor();
		TrackIdProvider.getInstance(null).setPosition(
				cursor.getOffset() + position);
		ServiceProxy.play();
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			reg();
			setCusor();
			listview.setSelectionFromTop(TrackIdProvider.getInstance(null)
					.getCurPosition() - mAdapter.getOffset(), 200);
		} else {
			mAdapter.changeCursor(null);
			getContext().unregisterReceiver(mQueueListener);
		}
	}

	@SuppressWarnings("deprecation")
	private void setCusor() {
		if (mAdapter.getCursor() == null) {
			mAdapter.changeCursor(new NowPlayingCursor(getContext()));
		} else {
			mAdapter.getCursor().requery();
			mAdapter.updateOffset();
		}
	}

	private void reg() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		f.addAction(TrackIdProvider.QUEUE_CHANGED);
		getContext().registerReceiver(mQueueListener, f);
		mQueueListener.onReceive(getContext(), new Intent(
				MediaPlaybackService.META_CHANGED).putExtra("id",
				ServiceProxy.getTrackId()));
	}

	private BroadcastReceiver mQueueListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MediaPlaybackService.META_CHANGED)) {
				int id = intent.getIntExtra("id", 0);
				if (mAdapter != null) {
					mAdapter.setPlayingId(id);
					mAdapter.notifyDataSetChanged();
				}
			} else {
				setCusor();
				mAdapter.notifyDataSetInvalidated();
				mAdapter.notifyDataSetChanged();
				listview.setSelectionFromTop(TrackIdProvider.getInstance(null)
						.getCurPosition() - mAdapter.getOffset(), 200);
			}
		}
	};

	protected void onDetachedFromWindow() {
		if (getVisibility() == View.VISIBLE)
			getContext().unregisterReceiver(mQueueListener);
		super.onDetachedFromWindow();
	};

}
