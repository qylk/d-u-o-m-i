package com.qylk.app.musicplayer.activity;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.adapter.ViewPagerAdapter;
import com.qylk.app.musicplayer.fragment.ArtistArtFragment;
import com.qylk.app.musicplayer.fragment.InfoFragment;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.ui.ActionBar;
import com.qylk.app.ui.ActionbarActivity;
import com.qylk.app.ui.menu.ActionBarMenuItem;
import com.qylk.app.ui.player.widget.MyPager;
import com.qylk.app.ui.player.widget.PlayerBar;

public class PlayerActivity extends ActionbarActivity {
	private View player;
	private View queue;
	private MyPager content;
	private ViewPagerAdapter mPagerAdapter;
	private PlayerBar playerBar;
	private ServiceProxy.ServiceToken mToken;
	private boolean initialed;
	private ViewStub queueWapper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changeActionBar();
		player = LayoutInflater.from(this).inflate(R.layout.player, null);
		setContentView(player);
	}

	private void initPlayerView() {
		this.content = (MyPager) findViewById(R.id.content_player);
		this.playerBar = (PlayerBar) findViewById(R.id.playerbar);
		queueWapper = (ViewStub) findViewById(R.id.queue);
		mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this);
		mPagerAdapter.addFragment("art", ArtistArtFragment.class, null);
		mPagerAdapter.addFragment("info", InfoFragment.class, null);
		content.setAdapter(mPagerAdapter);
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
			field.set(this, Integer.valueOf(R.layout.contentplayingframe));
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
			ani.setInterpolator(AnimationUtils.loadInterpolator(this,
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
			ani.setInterpolator(AnimationUtils.loadInterpolator(this,
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

	@Override
	public void onBackPressed() {
		if (queue != null && queue.getVisibility() == View.VISIBLE) {
			toggleQueue();
			return;
		} else {
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			initialed = false;
		}
	}

	@Override
	public void onPause() {
		playerBar.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		if (!initialed) {
			init();
		}
		playerBar.onResume();
		super.onResume();
	}

	private void init() {
		initPlayerView();
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
