package com.qylk.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.TagListAdapter;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.ServiceProxy;

public class TagShowFragment extends Fragment {

	private TagListAdapter adapter;
	private TextView tagpreview;
	private OnChildClickListener tagValueToggle = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			adapter.invertBit(groupPosition, childPosition);
			updateTagPreview();
			return true;
		}
	};

	private void configHeaderBar() {
//		BaseActivity a = (BaseActivity) getActivity();
//		a.setUserButtonText("Save");
//		a.registerHeaderButtonClickedListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// do nothing
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int id = intent.getIntExtra("id", -1);
				if (id != -1) {
					adapter.setId(id);
					updateTagPreview();
				}
			}
		}
	};

	@Override
	public void onResume() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		mTrackListListener.onReceive(null,
				new Intent().putExtra("id", ServiceProxy.getTrackId()));
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(mTrackListListener);
		super.onPause();
	}

	@Override
	public void onStart() {
		configHeaderBar();
		super.onStart();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.tagshow, null);
		ExpandableListView el = (ExpandableListView) v
				.findViewById(R.id.taglist);
		tagpreview = (TextView) v.findViewById(R.id.tagpreview);
		adapter = new TagListAdapter(getActivity(), -1);
		el.setAdapter(adapter);
		el.setOnChildClickListener(tagValueToggle);
		for (int i = 0; i < adapter.getGroupCount(); i++)
			el.expandGroup(i);// 全部展开
		v.setClickable(true);// 为防止下层叠加的Fragment响应触屏动作，很重要
		return v;
	}

	private void updateTagPreview() {
		tagpreview.setText(adapter.getTagPreviewText());
	}
//	@Override
//	public void onUserButtonClicked() {
//		int[] tag = adapter.getNewTagIntArray();
//		int lib_id = adapter.getLib_Id();
//		if (MediaDatabase
//				.updateTag(getActivity(), adapter.getId(), lib_id, tag) == 1) {
//			Toast.makeText(getActivity(), "成功", Toast.LENGTH_LONG).show();
//		}
//	}

}
