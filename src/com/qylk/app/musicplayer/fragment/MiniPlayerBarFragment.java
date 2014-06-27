package com.qylk.app.musicplayer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.MusicUtils;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.musicplayer.utils.ServiceProxy.ServiceProxyRegisterListener;
import com.qylk.app.ui.FocusableFragment;

public class MiniPlayerBarFragment extends Fragment implements
		ServiceProxyRegisterListener {
	private ServiceProxy.ServiceToken mToken;
	private AlbumArtHandler mAlbumArtHandler;
	private static final long SEC = 1000;
	private static final int REFRESH = 0;
	private static final int GET_ALBUM_ART = 1;
	private static final int ALBUM_ART_DECODED = 2;
	private TextView title, artist;
	private ImageView icon;
	private Drawable progress;
	private ImageView btn_pre, btn_play, btn_next;
	private long mDuration;
	private boolean paused = true;
	private boolean serviceReady;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAlbumArtHandler = new AlbumArtHandler();
		mToken = ServiceProxy.register(getActivity(), this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View playerbar = inflater.inflate(R.layout.playerbar_mini, null);
		initView(playerbar);
		return playerbar;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	}

	private void initView(View root) {
		View playerbar = root.findViewById(R.id.minibar);
		playerbar.setOnClickListener(onBarClickedListener);
		progress = root.findViewById(R.id.miniprogress).getBackground();
		title = (TextView) root.findViewById(R.id.line1);
		artist = (TextView) root.findViewById(R.id.line2);
		icon = (ImageView) root.findViewById(R.id.albumicon);
		btn_pre = (ImageView) root.findViewById(R.id.btn_last);
		btn_play = (ImageView) root.findViewById(R.id.btn_play);
		btn_next = (ImageView) root.findViewById(R.id.btn_next);
		btn_pre.setOnClickListener(previousListener);
		btn_play.setOnClickListener(togglePlayListener);
		btn_next.setOnClickListener(nextListener);
	}

	private OnClickListener onBarClickedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToPlayer(true);
		}
	};

	@Override
	public void onDestroy() {
		ServiceProxy.unregister(mToken, null);
		mHandler.removeMessages(REFRESH);
		mHandler = null;
		super.onDestroy();
	}

	@Override
	public void onPause() {
		paused = true;
		getActivity().unregisterReceiver(mTrackListListener);
		super.onPause();
	}

	@Override
	public void onResume() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		paused = false;
		if (serviceReady)
			updatePanelView();
		super.onResume();
	}

	private void updatePanelView() {
		updateTrackInfo();
		refreshNow();
		queueNextRefresh(1);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESH:
				refreshNow();
				queueNextRefresh(SEC);
				break;
			case ALBUM_ART_DECODED:
				icon.setImageBitmap((Bitmap) msg.obj);
				break;
			}
		}
	};

	private void queueNextRefresh(long delay) {
		if (paused) {
			this.mHandler.removeMessages(REFRESH);
		} else {
			Message msg = this.mHandler.obtainMessage(REFRESH);
			this.mHandler.removeMessages(REFRESH);
			this.mHandler.sendMessageDelayed(msg, delay);
		}
	}

	private void updateTrackInfo() {
		mDuration = ServiceProxy.getDuration() / SEC;
		paused = !ServiceProxy.isPlaying();
		String title = ServiceProxy.getTrackTitle();
		this.title.setText(title);
		getActivity().setTitle(title);// action bar title
		this.artist.setText(ServiceProxy.getArtist());
		setPauseButtonImage();
		mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
		mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
				ServiceProxy.getArtistId()).sendToTarget();
	}

	private void refreshNow() {
		long position = ServiceProxy.getPosition() / SEC;
		if ((position >= 0L) && (mDuration > 0L)) {
			this.progress.setLevel((int) (10000 * position / mDuration));
			return;
		} else {
			this.progress.setLevel(0);
		}
	}

	private void setPauseButtonImage() {
		if (ServiceProxy.isPlaying()) {
			btn_play.setImageResource(R.drawable.main_pause);
		} else
			btn_play.setImageResource(R.drawable.main_play);
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MediaPlaybackService.META_CHANGED)) {
				updateTrackInfo();
				setPauseButtonImage();
				queueNextRefresh(1);
			} else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
				paused = !intent.getBooleanExtra("playing", false);
				setPauseButtonImage();
				queueNextRefresh(1);
			}
		}
	};

	class AlbumArtHandler extends Handler {
		private int mArtId = -1;

		@Override
		public void handleMessage(Message msg) {
			int id = (Integer) msg.obj;
			if ((mArtId != id || id < 0)) {
				mHandler.removeMessages(ALBUM_ART_DECODED);
				Bitmap bm = MusicUtils.getMiniArtwork(getActivity(), id,
						R.drawable.ic_launcher);
				mArtId = id;
				Message numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
				mHandler.removeMessages(ALBUM_ART_DECODED);
				mHandler.sendMessage(numsg);
			}
		}
	}

	private void switchToPlayer(boolean animation) {
		// startActivity(new Intent(getActivity(), PlayerActivity.class));
		// getActivity().overridePendingTransition(R.anim.in_from_right,
		// R.anim.out_to_left);
		Fragment player = getFragmentManager().findFragmentByTag("player");
		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.in_from_right, 0).show(player)
				.commit();
		((FocusableFragment) player).requestFragemntFocus();
		Log.v(getClass().getSimpleName(), "Startting Player Activity");
	}

	private OnClickListener previousListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ServiceProxy.pre();
		}
	};
	private OnClickListener togglePlayListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ServiceProxy.togglePlay();
		}
	};
	private OnClickListener nextListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ServiceProxy.next();
		}
	};

	@Override
	public void onServiceProxyReady() {
		serviceReady = true;
		if (isResumed())
			updatePanelView();

	}

	@Override
	public void onUnRegisterFromServiceProxy() {
		serviceReady = false;
	}

}
