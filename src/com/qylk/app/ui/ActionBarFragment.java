package com.qylk.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
		onMenuItemSelectedListener {
	private ActionBarCompat actionbarView;
	private ViewGroup content;
	private int mFrameRes = R.layout.actionbar_content_frame;

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

	protected void addFragment(Class<?> fragment) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.fragmentcontent,
					Fragment.instantiate(getActivity(), fragment.getName()),
					fragment.getSimpleName());
			ft.addToBackStack(fragment.getSimpleName());
			ft.commit();
		}
	}

	public ActionBar getActionbar() {
		return actionbarView;
	}

	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
	}

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

}
