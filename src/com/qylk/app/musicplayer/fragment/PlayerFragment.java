package com.qylk.app.musicplayer.fragment;

import java.lang.reflect.Field;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.ViewPagerAdapter;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.menu.ActionBarMenuItem;
import com.qylk.app.ui.player.widget.MyPager;
import com.qylk.app.ui.player.widget.PlayerBar;

/**
 * used with PlayerActivity,PlayerFragment2 is designed in MainActivity
 * 
 */
@Deprecated
public class PlayerFragment extends ActionBarFragment {
	private View player;
	private View queue;
	private MyPager content;
	private ViewPagerAdapter mPagerAdapter;
	private PlayerBar playerBar;
	private ServiceProxy.ServiceToken mToken;
	private boolean initialed;
	private ViewStub queueWapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeActionBar();
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		player = inflater.inflate(R.layout.player, null);
		container.addView(player);
		super.onCreateContentView(inflater, container, savedInstanceState);
	}

	private void initPlayerView(View v) {
		assert (initialed == false);
		this.content = (MyPager) v.findViewById(R.id.content_player);
		this.playerBar = (PlayerBar) v.findViewById(R.id.playerbar);
		queueWapper = (ViewStub) findViewById(R.id.queue);
		mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),
				getActivity());
		mPagerAdapter.addFragment("info", ArtistInfoFragment.class, null);
		mPagerAdapter.addFragment("art", ArtistArtFragment.class, null);
		content.setAdapter(mPagerAdapter);
		content.setCurrentItem(1);
	}

	@Override
	protected void onCreateActionBar(ActionBar act) {
		super.onCreateActionBar(act);
		act.setHomeIcon(getResources().getDrawable(
				R.drawable.player_actionbar_bck));
		act.addMenuItem(new ActionBarMenuItem("queue", getResources()
				.getDrawable(R.drawable.player_actionbar_queue), 0));
	}

	@Override
	protected void onActionMenuSelected(ActionBarMenuItem item) {
		super.onActionMenuSelected(item);
		toggleQueue();
	}

	private void changeActionBar() {
		try {
			// 使用代码修改原ActionBar不易，直接利用反射替换为兼容的ActionBar视图
			Field field = getClass().getSuperclass().getDeclaredField(
					"mFrameRes");
			field.setAccessible(true);
			field.set(this,
					Integer.valueOf(R.layout.actionbar_content_frame_player));
		} catch (Exception e) {
		}
	}

	private void toggleQueue() {
		if (queue == null) {
			queue = queueWapper.inflate();
		}
		if (queue.getVisibility() == View.VISIBLE) {
			TranslateAnimation ani = new TranslateAnimation(Animation.ABSOLUTE,
					0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_PARENT,
					1.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
			ani.setInterpolator(AnimationUtils.loadInterpolator(getActivity(),
					android.R.anim.decelerate_interpolator));
			ani.setDuration(300L);
			content.setVisibility(View.VISIBLE);
			content.startAnimation(ani);
			queue.postDelayed(new Runnable() {

				@Override
				public void run() {
					queue.setVisibility(View.INVISIBLE);
				}
			}, 310L);
		} else {
			TranslateAnimation ani = new TranslateAnimation(Animation.ABSOLUTE,
					0, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_PARENT, 0,
					Animation.RELATIVE_TO_PARENT, 1.0f);
			ani.setInterpolator(AnimationUtils.loadInterpolator(getActivity(),
					android.R.anim.decelerate_interpolator));
			ani.setDuration(300L);
			queue.setVisibility(View.VISIBLE);
			content.startAnimation(ani);
			content.postDelayed(new Runnable() {

				@Override
				public void run() {
					content.setVisibility(View.INVISIBLE);
				}
			}, 310L);
		}
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			setTitle(intent.getStringExtra("track"),
					intent.getStringExtra("artist"));
		}
	};

	@Override
	public void onBackPressed() {
		if (queue != null && queue.getVisibility() == View.VISIBLE) {
			toggleQueue();
		}
	}

	private void setTitle(String title, String subtitle) {
		setTitle(title);
		setSubTitle(subtitle);
	}

	@Override
	public void onPause() {
		playerBar.onPause();
		getActivity().unregisterReceiver(mTrackListListener);
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.v(getClass().getSimpleName(), "Player Activity OnResume");
		if (!initialed) {
			init();
		}
		playerBar.onResume();
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		getActivity().registerReceiver(mTrackListListener, f);
		setTitle(ServiceProxy.getTrackTitle(), ServiceProxy.getArtist());
		super.onResume();
	}

	private void init() {
		initPlayerView(player);
		initialed = true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean val = playerBar.onKeyDown(keyCode, event);
		if (val == true)
			return true;
		return super.onKeyDown(keyCode, event);
	}

}
