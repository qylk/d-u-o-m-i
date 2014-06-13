package com.qylk.app.musicplayer.fragment;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.ServiceProxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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
			// TODO
		} else {
			// TODO
		}
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
		mlist.setText("´ý¿ª·¢");
	}

	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mTrackListListener);
	}

}
