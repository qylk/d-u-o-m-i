package com.qylk.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.MusicUtils;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.ui.player.widget.RoundWheelImageView;
import com.qylk.app.ui.widget.WheelImageView;

public class ArtistArtFragment extends Fragment implements OnClickListener {
	private static final int UPDATE_VIEW = 0;
	private static final int UPDATE_ANIMATION = 1;
	private static final int ALBUM_ART_DECODED = 2;
	private RoundWheelImageView artwork;
	private WheelImageView image_rotate;
	private ImageButton collection, eq;
	protected int mArtId;
	protected boolean hasArtwork = false;
	private boolean preferAnimation = false;
	private static final int ROTATE_TIME = 5000;
	private static final long DELAY_UPDATE_VIEW = 400L;
	private static final long DELAY_UPDATE_ANIMATION = 0L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.player_artwork, null);
		artwork = (RoundWheelImageView) view.findViewById(R.id.image);
		image_rotate = (WheelImageView) view.findViewById(R.id.image_rotate);
		image_rotate.setOnClickListener(this);
		collection = (ImageButton) view.findViewById(R.id.collection);
		eq = (ImageButton) view.findViewById(R.id.eq);
		collection.setOnClickListener(this);
		eq.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.image_rotate) {
			preferAnimation = !preferAnimation;
			updateAnimation(true, 100);
		} else if (v.getId() == R.id.eq) {
			Intent i = new Intent(
					AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
			if (getActivity().getPackageManager().resolveActivity(i, 0) != null)
				startActivity(i);
		}
	}

	private void updateAnimation(boolean reset, long delay) {
		mHandler.removeMessages(UPDATE_ANIMATION);
		Message msg = mHandler.obtainMessage(UPDATE_ANIMATION, reset);
		mHandler.sendMessageDelayed(msg, delay);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_VIEW:
				doUpdateView((Integer) msg.obj);
				break;
			case UPDATE_ANIMATION:
				doUpdateAnimation(ServiceProxy.isPlaying(), (Boolean) msg.obj);
				break;
			case ALBUM_ART_DECODED:
				artwork.setImageBitmap((Bitmap) msg.obj);
				image_rotate.setImageDrawable(hasArtwork ? null
						: getResources()
								.getDrawable(R.drawable.btn_disk_rotate));
			}
		}
	};

	private void doUpdateView(int id) {
		if ((mArtId != id || id < 0)) {
			mHandler.removeMessages(ALBUM_ART_DECODED);
			Bitmap bm = MusicUtils.getOrginalArtwork(getActivity(), id);
			boolean hasArtworkLocal = (bm != null);
			// avoid load default artwork if previous artwork is already
			// default;
			if (!hasArtwork && !hasArtworkLocal) {
				return;
			}
			hasArtwork = hasArtworkLocal;
			if (bm == null) {
				bm = MusicUtils.getArtworkFromResource(getActivity(),
						R.drawable.disk);
			}
			mArtId = id;
			Message numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
			mHandler.removeMessages(ALBUM_ART_DECODED);
			mHandler.sendMessage(numsg);
		}
	}

	public void onResume() {
		super.onResume();
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		updateView(DELAY_UPDATE_VIEW);
		updateAnimation(false, DELAY_UPDATE_ANIMATION);
	}

	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mTrackListListener);
		doUpdateAnimation(false, false);
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MediaPlaybackService.META_CHANGED)) {
				updateView(DELAY_UPDATE_VIEW);
			} else {
				updateAnimation(false, DELAY_UPDATE_ANIMATION);
			}
		}
	};

	private void updateView(long delay) {
		int artId = ServiceProxy.getArtistId();
		mHandler.removeMessages(UPDATE_VIEW);
		Message msg = mHandler.obtainMessage(UPDATE_VIEW, artId);
		mHandler.sendMessageDelayed(msg, delay);
	}

	private void doUpdateAnimation(boolean playing, boolean reset) {
		if ((!playing || !preferAnimation)) {
			artwork.stopAnimation();
			if (reset) {
				artwork.resetAnimation();
			}
			if (image_rotate.inAnimation()) {
				image_rotate.stopAnimation();
				if (reset)
					image_rotate.resetAnimation();
			}
		} else if (preferAnimation && playing) {
			artwork.startAnimation(ROTATE_TIME, false);
			if (!hasArtwork)
				image_rotate.startAnimation(ROTATE_TIME, false);
		}
	}
}
