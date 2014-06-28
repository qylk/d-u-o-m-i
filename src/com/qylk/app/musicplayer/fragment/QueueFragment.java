package com.qylk.app.musicplayer.fragment;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.TrackListAdapter;
import com.qylk.app.musicplayer.fragment.common.SimpleSortableTrackListFragment;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.menu.ActionBarMenuItem;

public class QueueFragment extends SimpleSortableTrackListFragment {
	private View clearQueue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			// 使用代码修改原ActionBar不易，直接利用反射替换为兼容的ActionBar视图
			Field field = ActionBarFragment.class.getDeclaredField("mFrameRes");
			field.setAccessible(true);
			field.set(this,
					Integer.valueOf(R.layout.actionbar_content_frame_player));
		} catch (Exception e) {
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View header = inflater.inflate(R.layout.queue, null);
		clearQueue = header.findViewById(R.id.queue_clean);
		clearQueue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				TrackIdProvider.getInstance().clear();
				TrackListAdapter adapter = (TrackListAdapter) getAdapter();
				adapter.changeCursor(null);
				getListView().invalidate();
				ServiceProxy.stop();
			}
		});
		container.addView(header);
		super.onCreateContentView(inflater, container, savedInstanceState);
		setHeaderVisible(false);
	}

	@Override
	protected int getItemLayout() {
		return R.layout.track_list_item_dark;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().requestFocus();
	}

	// TODO CURSOR生成
	@Override
	protected void onCreateActionBar(ActionBar act) {
		super.onCreateActionBar(act);
		act.setHomeIcon(getResources().getDrawable(
				R.drawable.player_actionbar_bck));
		act.addMenuItem(new ActionBarMenuItem("queue", getResources()
				.getDrawable(R.drawable.player_actionbar_queue), 0));
	}
}
