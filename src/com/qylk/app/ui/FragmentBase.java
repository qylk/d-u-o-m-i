package com.qylk.app.ui;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

public class FragmentBase extends Fragment {

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	};

	public boolean onBackPressed() {
		return getFragmentManager().popBackStackImmediate();
	};

}
