package com.qylk.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.qylk.app.musicplayer.R;
import com.qylk.app.ui.ActionBarCompat.onMenuItemSelectedListener;
import com.qylk.app.ui.menu.ActionBarMenuItem;

/**
 * Fragment with an ActionBarCompat
 * 
 */
public class ActionBarFragment extends FragmentBase implements
		onMenuItemSelectedListener, FocusableFragment {
	private ActionBarCompat actionbarView;
	private ViewGroup content;
	private int mFrameRes = R.layout.actionbar_content_frame;

	/**
	 * 但前占据屏幕的Fragment，单例对象
	 */
	private static Fragment focused;
	private Fragment lastFocusedFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View frameview = inflater.inflate(mFrameRes, null);
		actionbarView = (ActionBarCompat) frameview
				.findViewById(R.id.actionbar);
		actionbarView.setonMenuItemSelectedListener(this);
		onCreateActionBar(actionbarView);
		content = (ViewGroup) frameview.findViewById(R.id.content);
		onCreateContentView(inflater, content, savedInstanceState);
		frameview.setClickable(true);
		return frameview;
	}

	protected final View findViewById(int id) {
		return getView().findViewById(id);
	}

	protected void addFragment(Class<?> fragment, Bundle argument) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment frg = Fragment.instantiate(getActivity(),
					fragment.getName(), argument);
			ft.add(R.id.fragmentcontent, frg, fragment.getSimpleName());
			ft.addToBackStack(fragment.getSimpleName());
			ft.commit();
			if (frg instanceof FocusableFragment) {
				((FocusableFragment) frg).requestFragemntFocus();
			}
		}
	}

	public ActionBar getActionbar() {
		return actionbarView;
	}

	/**
	 * before finish this method，make sure call addView(your view) on param container
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState same refence to that in {@link #onCreateView}
	 */
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
	}

	/**
	 * similar to Activity.onPrepareOptionMenu(Menu menu);
	 * @param act
	 */
	protected void onCreateActionBar(ActionBar act) {
		act.setHomeIcon(getResources().getDrawable(R.drawable.actionbar_back));
		act.setHomeClicklistener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
	}

	protected void onActionMenuSelected(ActionBarMenuItem item) {

	}

	public void setTitle(String title) {
		actionbarView.setTitle(title);
	}

	public void setSubTitle(String title) {
		actionbarView.setSubTitle(title);
	}

	@Override
	public void onItemSelected(ActionBarMenuItem item) {
		onActionMenuSelected(item);
	}

	@Override
	public void onDestroy() {
		if (lastFocusedFragment != null && focused == this)
			abondenFragemntFocus();
		super.onDestroy();
	}

	public static Fragment getFoucusFragment() {
		return focused;
	}

	@Override
	public void requestFragemntFocus() {
		this.lastFocusedFragment = getFoucusFragment();
		focused = this;
		Log.v("FragmentFocus", this.getClass().getSimpleName());
	}

	@Override
	public void abondenFragemntFocus() {
		focused = this.lastFocusedFragment;
		Log.v("FragmentFocus", this.lastFocusedFragment.getClass()
				.getSimpleName());
	}
}
