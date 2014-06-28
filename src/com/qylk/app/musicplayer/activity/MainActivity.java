package com.qylk.app.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.MainFragment;
import com.qylk.app.musicplayer.fragment.MiniPlayerBarFragment;
import com.qylk.app.musicplayer.fragment.PlayerFragment2;
import com.qylk.app.musicplayer.utils.MEDIA;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.musicplayer.utils.ServiceProxy.ServiceProxyRegisterListener;
import com.qylk.app.ui.ActionBarFragment;
import com.qylk.app.ui.FocusableFragment;
import com.qylk.app.ui.FragmentBase;

public class MainActivity extends BaseActivity implements
		ServiceProxyRegisterListener {

	private ServiceProxy.ServiceToken mToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mToken = ServiceProxy.register(this, this);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment mListGenreFragment = Fragment.instantiate(this,
				MainFragment.class.getName(), null);
		Fragment mPlayerBarFragment = Fragment.instantiate(this,
				MiniPlayerBarFragment.class.getName(), null);

		Fragment player = Fragment.instantiate(this,
				PlayerFragment2.class.getName(), null);
		ft.replace(R.id.fragmentcontent, mListGenreFragment, "genre");
		ft.replace(R.id.minibar, mPlayerBarFragment, "playerbar");
		ft.add(android.R.id.content, player, "player");
		ft.hide(player);
		((FocusableFragment) mListGenreFragment).requestFragemntFocus();
		ft.commit();
	}

	private BroadcastReceiver updateListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(MainActivity.this, "…®√ËÕÍ±œ", Toast.LENGTH_LONG).show();
		}
	};

	protected void onResume() {
		super.onResume();
		IntentFilter f = new IntentFilter();
		f.addAction(MEDIA.INTENT_SCAN_DONE);
		registerReceiver(updateListListener, f);
	};

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(updateListListener);
	}

	@Override
	protected void onDestroy() {
		ServiceProxy.unregister(this.mToken, null);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Fragment frg = ActionBarFragment.getFoucusFragment();
		if (frg instanceof PlayerFragment2) {
			if (getSlidingMenu().isMenuShowing())
				toggle();
			else
				((FragmentBase) frg).onBackPressed();
		} else {
			((FragmentBase) frg).onBackPressed();
		}
	}

	// dispatch KeyEvent to fragments
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Fragment frg = ActionBarFragment.getFoucusFragment();
		boolean val = ((FragmentBase) frg).onKeyDown(keyCode, event);
		if (val == true)
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onServiceProxyReady() {

	}

	@Override
	public void onUnRegisterFromServiceProxy() {

	}
}
