package com.qylk.app.musicplayer.deprecated;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.activity.MainActivity;
import com.qylk.app.musicplayer.adapter.ViewPagerAdapter;
import com.qylk.app.musicplayer.fragment.LibraryListFragment;
import com.qylk.app.musicplayer.fragment.PlayerFragment;
import com.qylk.app.musicplayer.fragment.common.SimpleTrackListFragment;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.MusicUtils;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.musicplayer.utils.ServiceProxy.ServiceConnectionListener;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.menu.ActionBarMenuItem;
import com.qylk.app.ui.player.widget.PagerSlidingTabStrip;

public class TabHostFragment extends ActionBarFragment implements
		OnPageChangeListener, OnBackStackChangedListener,
		ServiceConnectionListener {
	private static final long SEC = 1000;
	private static final int REFRESH = 0;
	private static final int GET_ALBUM_ART = 1;
	private static final int ALBUM_ART_DECODED = 2;
	private ViewPager mViewPager;
	private ViewPagerAdapter mPagerAdapter;
	private ServiceProxy.ServiceToken mToken;
	private boolean musicReady = false;
	private TextView title, artist;
	private ImageView icon;
	private Drawable progress;
	private ImageView btn_pre, btn_play, btn_next;
	private boolean playing;
	private long mDuration;
	private boolean paused = true;
	private FragmentManager fm;
	private int fragmentCnt;
	private AlbumArtHandler mAlbumArtHandler;
	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MediaPlaybackService.META_CHANGED)) {
				updateTrackInfo();
				setPauseButtonImage();
				queueNextRefresh(1);
				if (!musicReady) {
					musicReady = true;
					getView().findViewById(R.id.controlpanel)
							.setClickable(true);
				}
			} else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
				playing = intent.getBooleanExtra("playing", false);
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

	private OnClickListener onPanelClickedListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToPlayer(true);
		}
	};

	private void switchToPlayer(boolean animation) {
		FragmentTransaction ft = fm.beginTransaction();
		if (animation)
			ft.setCustomAnimations(R.anim.in_from_bottom, 0, 0,
					R.anim.out_to_bottom);
		Fragment fragment = Fragment.instantiate(getActivity(),
				PlayerFragment.class.getName(), null);
		ft.add(R.id.realtabcontent, fragment, "player");
		ft.addToBackStack("addplayer");// 动作加入堆栈
		ft.commit();
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
	private OnClickListener homeClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			MainActivity a = (MainActivity) getActivity();
			a.toggle();
		}
	};

	protected View createTabIndication(TabHost paramTabHost,
			String paramString, int paramInt) {
		ImageView icon = (ImageView) LayoutInflater.from(getActivity())
				.inflate(R.layout.tabitem, null, false);
		icon.setImageResource(paramInt);
		return icon;
	}

	private void initTabSpec() {
		mPagerAdapter.addFragment("歌曲库", LibraryListFragment.class, null);
		mPagerAdapter.addFragment("艺术家", SimpleTrackListFragment.class, null);
		mPagerAdapter.addFragment("专辑", SimpleTrackListFragment.class, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mToken = ServiceProxy.connectToService(getActivity(), this);
		fm = getFragmentManager();
		fragmentCnt = fm.getBackStackEntryCount();
		mAlbumArtHandler = new AlbumArtHandler();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onCreateActionbar(ActionBar act) {
		super.onCreateActionbar(act);
		act.setTitle("本地列表");
		act.setHomeIcon(getResources().getDrawable(R.drawable.actionbar_menu));
		act.setHomeClicklistener(homeClickListener);
		act.addMenuItem(new ActionBarMenuItem("搜索0", getResources()
				.getDrawable(R.drawable.actionbar_edit), 0));
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View content = inflater.inflate(R.layout.fragmentpagerview, container);
		mViewPager = (ViewPager) content.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(1);
		content.setClickable(true);// 为防止下层叠加的Fragment响应触屏动作，很重要
		initView(content);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	private void initView(View root) {
		View controlpanel = root.findViewById(R.id.controlpanel);
		progress = root.findViewById(R.id.simpleprogress).getBackground();
		progress.setLevel(5000);
		title = (TextView) root.findViewById(R.id.display_title1);
		artist = (TextView) root.findViewById(R.id.display_artist1);
		icon = (ImageView) root.findViewById(R.id.albumicon);
		btn_pre = (ImageView) root.findViewById(R.id.btn_last);
		btn_play = (ImageView) root.findViewById(R.id.btn_play);
		btn_next = (ImageView) root.findViewById(R.id.btn_next);
		btn_pre.setOnClickListener(previousListener);
		btn_play.setOnClickListener(togglePlayListener);
		btn_next.setOnClickListener(nextListener);
		controlpanel.setOnClickListener(onPanelClickedListener);
		controlpanel.setClickable(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPagerAdapter = new ViewPagerAdapter(getFragmentManager(),
				getActivity());
		initTabSpec();
		mViewPager.setAdapter(mPagerAdapter);
		PagerSlidingTabStrip pts = (PagerSlidingTabStrip) getView()
				.findViewById(R.id.tabs);
		pts.setViewPager(mViewPager);
		// mViewPager.setOnPageChangeListener(this);
		String str = getActivity().getIntent().getType();
		if (str.equals("vnd.android.cursor.dir/library")) {
			mViewPager.setCurrentItem(0);
		} else if (str.equals("vnd.android.cursor.dir/search")) {
			mViewPager.setCurrentItem(1);
		} else if (str.equals("vnd.android.cursor.dir/playlist"))
			mViewPager.setCurrentItem(2);
		mPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		ServiceProxy.disConnectFromService(mToken, null);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if (paused) {
			super.onPause();
			return;
		}
		paused = true;
		getActivity().unregisterReceiver(mTrackListListener);
		super.onPause();
	}

	@Override
	public void onResume() {
		if (!paused) {
			super.onResume();
			return;
		}
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		paused = false;
		updatePanelView();
		super.onResume();
	}

	private void updatePanelView() {
		updateTrackInfo();
		refreshNow();
		queueNextRefresh(1);
	}

	private final Handler mHandler = new Handler() {

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
		if (paused || !playing) {
			this.mHandler.removeMessages(REFRESH);
		} else {
			Message msg = this.mHandler.obtainMessage(REFRESH);
			this.mHandler.removeMessages(REFRESH);
			this.mHandler.sendMessageDelayed(msg, delay);
		}
	}

	private void updateTrackInfo() {
		mDuration = ServiceProxy.getDuration() / SEC;
		playing = ServiceProxy.isPlaying();
		String title = ServiceProxy.getTrackTitle();
		this.title.setText(title);
		getActivity().setTitle(title);// action bar title
		String artist = ServiceProxy.getArtist();
		this.artist.setText(artist);
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
		if (playing) {
			btn_play.setImageResource(R.drawable.main_pause);
		} else
			btn_play.setImageResource(R.drawable.main_play);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mViewPager.setCurrentItem(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onBackStackChanged() {
		if (fm.getBackStackEntryCount() != fragmentCnt) {
			if (!paused)
				onPause();
		} else if (paused)
			onResume();
	}

	@Override
	public void ServiceConnected() {
		updatePanelView();
		if (ServiceProxy.MediaPlayerHasInitialized())
			getView().findViewById(R.id.controlpanel).setClickable(true);
		fm.addOnBackStackChangedListener(this);// 在onResume()执行前不监听(因为)，放到这里
	}

	@Override
	public void ServiceDisConnected() {
		// TODO Auto-generated method stub

	}
}
