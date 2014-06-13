package com.qylk.app.musicplayer.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.common.SimpleTrackListFragment;
import com.qylk.app.ui.widget.SearchBox;
import com.qylk.app.ui.widget.SearchBox.OnQueryTextListener;

public class SearchFragment extends SimpleTrackListFragment implements Callback {
	private static String currentSearchText;
	private Handler handler = new Handler(this);
	private StringBuilder mStrBulder = new StringBuilder();
	private SearchBox sb;

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_area, null);
		container.addView(view);
		sb = (SearchBox) view.findViewById(R.id.searchbox);
		sb.setSearchButtonVisible(false);
		sb.setOnQueryTextListener(queryListener);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		sb.setImeVisibility(false);
	}

	private OnQueryTextListener queryListener = new OnQueryTextListener() {

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
	};

	private void addQuery(String newText) {
		handler.removeMessages(0);
		handler.sendMessageDelayed(handler.obtainMessage(0, newText), 600);
	}

	@Override
	public boolean handleMessage(Message msg) {
		String queryText = (String) msg.obj;
		if (queryText != null && queryText.equals(currentSearchText)) {
			mStrBulder.delete(0, mStrBulder.length());
			mStrBulder.append("title_key like '");
			mStrBulder.append(queryText);
			mStrBulder.append("%' or artist_key like '%");
			mStrBulder.append(queryText);
			mStrBulder.append("%' or title like '%");
			mStrBulder.append(queryText);
			mStrBulder.append("%' or artist like '%");
			mStrBulder.append(queryText);
			mStrBulder.append("%'");

			Bundle argument = new Bundle();
			argument.putString("selection", mStrBulder.toString());
			getLoaderManager().restartLoader(0, argument, this);
		}
		return true;
	}

}
