package com.qylk.app.musicplayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.PlayerFragment;
import com.qylk.app.ui.FragmentBase;

@Deprecated
public class PlayerActivity extends BaseActivity {
	private Fragment player;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frame = new FrameLayout(this);
		frame.setId(R.id.content);
		frame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setContentView(frame);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		player = Fragment.instantiate(this, PlayerFragment.class.getName(),
				null);
		ft.replace(R.id.content, player, "player");
		ft.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean val = ((FragmentBase) player).onKeyDown(keyCode, event);
		if (val == true)
			return true;
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		((FragmentBase) player).onBackPressed();
		this.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
}
