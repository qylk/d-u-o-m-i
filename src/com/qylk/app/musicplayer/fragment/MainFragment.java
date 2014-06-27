package com.qylk.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.activity.MainActivity;
import com.qylk.app.musicplayer.adapter.ListGenerAdapter;
import com.qylk.app.musicplayer.fragment.common.SimpleTrackListFragment;
import com.qylk.app.musicplayer.utils.MEDIA;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.menu.ActionBarMenuItem;

public class MainFragment extends ActionBarFragment implements
		OnItemClickListener {

	private ListAdapter mListAdapter;

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		Context context = inflater.getContext();
		LinearLayout ll = new LinearLayout(context, null);
		container.addView(ll);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.gradient_mytrack_genre));
		View searchView = inflater.inflate(R.layout.false_searchbox, null);
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle argument = new Bundle();
				argument.putString("title", "ËÑË÷");
				addFragment(SearchFragment.class, argument);
			}
		});
		ll.addView(searchView);
		ll.addView(getHeaderView("ÎÒµÄÒôÀÖ", inflater));
		View listw = inflater.inflate(R.layout.cell_list_genre, null);
		ListView mlist = (ListView) listw.findViewById(R.id.list);
		mlist.setFocusable(false);
		mListAdapter = new ListGenerAdapter(context);
		mlist.setAdapter(mListAdapter);
		mlist.setOnItemClickListener(this);
		mlist.setSelection(0);
		ll.addView(listw);

		ll.addView(getHeaderView("ÎÒµÄ¸èµ¥", inflater));
		ListView collection = new ListView(context);
		ll.addView(collection);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	private View getHeaderView(String header, LayoutInflater inflater) {
		View headerview = inflater.inflate(R.layout.cell_list_genre_header,
				null);
		((TextView) headerview.findViewById(R.id.title)).setText(header);
		return headerview;
	}
	

	@Override
	protected void onCreateActionBar(ActionBar act) {
		super.onCreateActionBar(act);
		act.setHomeIcon(getResources().getDrawable(R.drawable.actionbar_menu));
		act.setHomeClicklistener(homeClickListener);
		act.addMenuItem(new ActionBarMenuItem("É¨Ãè", getResources().getDrawable(
				R.drawable.actionbar_scan), 0));
	}

	@Override
	public void onBackPressed() {
		((MainActivity) getActivity()).toggle();
	}

	private OnClickListener homeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MainActivity a = (MainActivity) getActivity();
			a.toggle();
		}
	};

	@Override
	protected void onActionMenuSelected(ActionBarMenuItem item) {
		super.onActionMenuSelected(item);
		if (item.getId() == 0) {
			Toast.makeText(getActivity(), "É¨Ãè¿ªÊ¼", Toast.LENGTH_SHORT).show();
			getActivity().sendBroadcast(new Intent(MEDIA.SCAN_ACTION));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Class<?> fragment = null;
		Bundle argument = new Bundle();
		switch (position) {
		case 0:
			fragment = LibraryListFragment.class;
			argument.putString("selection", "_id >0 ORDER BY title_key ASC");
			argument.putString("title", "¸èÇú¿â");
			break;
		case 1:
			break;

		case 2:

			break;

		case 3:

			break;

		case 4:

			break;

		default:
			break;
		}
		addFragment(fragment, argument);
	}
}
