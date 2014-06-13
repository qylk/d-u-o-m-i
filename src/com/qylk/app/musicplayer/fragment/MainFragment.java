package com.qylk.app.musicplayer.fragment;

import android.content.Context;
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

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.activity.MainActivity;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.menu.ActionBarMenuItem;
import com.qylk.app.ui.player.widget.ListGenerAdapter;

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
		ll.setBackground(context.getResources().getDrawable(
				R.drawable.gradient_mytrack_genre));
		View searchView = inflater.inflate(R.layout.false_searchbox, null);
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addFragment(SearchFragment.class);
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
		View headerview = inflater.inflate(R.layout.cell_list_genre_header, null);
		((TextView) headerview.findViewById(R.id.title)).setText(header);
		return headerview;
	}

	@Override
	protected void onCreateActionBar(ActionBar act) {
		super.onCreateActionBar(act);
		act.setHomeIcon(getResources().getDrawable(R.drawable.actionbar_menu));
		act.setHomeClicklistener(homeClickListener);
		act.addMenuItem(new ActionBarMenuItem("ËÑË÷2", getResources()
				.getDrawable(R.drawable.btn_search), 0));
		act.addMenuItem(new ActionBarMenuItem("ËÑË÷3", getResources()
				.getDrawable(R.drawable.btn_search), 1));
		act.addMenuItem(new ActionBarMenuItem("ËÑË÷4", getResources()
				.getDrawable(R.drawable.btn_search), 2));
		act.addMenuItem(new ActionBarMenuItem("ËÑË÷5", getResources()
				.getDrawable(R.drawable.btn_search), 3));
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
			// Fragment frg = Fragment.instantiate(getActivity(), fname, args);
			// getFragmentManager().beginTransaction().add(getContentId(), frg,
			// frg.getClass().getSimpleName());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Class<?> fragment = null;
		switch (position) {
		case 0:
			fragment = LibraryListFragment.class;
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
		addFragment(fragment);
	}
}
