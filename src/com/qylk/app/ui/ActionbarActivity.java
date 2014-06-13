package com.qylk.app.ui;

import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.qylk.app.musicplayer.R;
import com.qylk.app.ui.ActionBarCompat.onMenuItemSelectedListener;
import com.qylk.app.ui.menu.ActionBarMenuItem;

/**
 * Activity with an ActionBarCompat
 * 
 */
public class ActionbarActivity extends FragmentActivity implements
		onMenuItemSelectedListener {
	private FrameLayout content;
	private ActionBarCompat actionbarview;
	private int mFrameRes = R.layout.actionbar_content_frame;

	@Override
	public void setContentView(View view) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(mFrameRes);
		content = (FrameLayout) findViewById(R.id.content);
		content.addView(view);
		actionbarview = (ActionBarCompat) findViewById(R.id.actionbar);
		ActionBar actionbar = actionbarview;
		onCreateActionBar(actionbar);
		actionbarview.setonMenuItemSelectedListener(this);
	}

	public void addView(View view) {
		content.addView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;// do not show this menu
	}

	protected void onCreateActionBar(ActionBar act) {
		act.setHomeIcon(getResources().getDrawable(R.drawable.actionbar_back));
		act.setHomeClicklistener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	protected void onActionMenuSelected(ActionBarMenuItem item) {

	}

	@Override
	public void onItemSelected(ActionBarMenuItem item) {
		onActionMenuSelected(item);
	}

	public void setTitle(String title) {
		actionbarview.setTitle(title);
	}

	public void setSubTitle(String title) {
		actionbarview.setSubTitle(title);
	}

}
