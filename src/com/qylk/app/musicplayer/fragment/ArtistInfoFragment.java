package com.qylk.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.common.SimpleTrackListFragment;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.ui.FocusableFragment;

public class ArtistInfoFragment extends Fragment implements OnClickListener {
	private TextView artist;
	private TextView album;
	private TextView mlist;
	private TextView detail;
	private ImageView protrait;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.player_artist_intro, null);
		artist = (TextView) view.findViewById(R.id.artsit);
		album = (TextView) view.findViewById(R.id.album);
		mlist = (TextView) view.findViewById(R.id.mlist);
		detail = (TextView) view.findViewById(R.id.detail);
		protrait = (ImageView) view.findViewById(R.id.image);

		artist.setOnClickListener(this);
		album.setOnClickListener(this);
		mlist.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.artsit) {
			query(true, artist.getText());
		} else if (v.getId() == R.id.album) {
			query(false, album.getText());
		}
	}

	private void query(boolean artist, CharSequence tip) {
		Bundle argument = new Bundle();
		if (artist)
			argument.putString("selection", "artist like '%" + tip + "%'");
		else
			argument.putString("selection", "album like '%" + tip + "%'");
		argument.putString("title", tip.toString());
		Fragment frg = Fragment.instantiate(getActivity(),
				SimpleTrackListFragment.class.getName(), argument);
		// android.R.id.content为根View，不再此Fragmnet视图内，必须使用getParentFragment().getFragmentManager()操作
		getParentFragment()
				.getFragmentManager()
				.beginTransaction()
				.add(android.R.id.content, frg,
						SimpleTrackListFragment.class.getSimpleName())
				.addToBackStack(SimpleTrackListFragment.class.getSimpleName())
				.commit();
		((FocusableFragment) frg).requestFragemntFocus();
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			doUpdateView();
		}
	};

	public void onResume() {
		super.onResume();
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		doUpdateView();
	}

	private void doUpdateView() {
		artist.setText(ServiceProxy.getArtist());
		album.setText(ServiceProxy.getAlbum());
		mlist.setText("待开发");
	}

	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mTrackListListener);
	}

}
