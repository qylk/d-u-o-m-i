package com.qylk.app.musicplayer.fragment;

import com.qylk.app.musicplayer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class NavigationFragment extends Fragment implements OnClickListener {
	private TextView setting, sleep, exit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.app_navigation, null);
		setting = (TextView) view.findViewById(R.id.setting);
		sleep = (TextView) view.findViewById(R.id.sleep);
		exit = (TextView) view.findViewById(R.id.exit);

		setting.setOnClickListener(this);
		sleep.setOnClickListener(this);
		exit.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.exit)
			getActivity().finish();
	}
}
