package com.qylk.app.musicplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.jeremyfeinstein.slidingmenu.lib.SlidingFragmentActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.NavigationFragment;

public class BaseActivity extends SlidingFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBehindContentView(R.layout.slid_menu_frame);
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidth(22);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffset(130);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.menu_frame,
						Fragment.instantiate(this,
								NavigationFragment.class.getName())).commit();
	}
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		toggle();
		return false;
	}

}
