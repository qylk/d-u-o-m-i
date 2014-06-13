package com.qylk.app.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.qylk.app.musicplayer.R;

public class SearchBox extends RelativeLayout implements View.OnClickListener {
	private EditText ebox;
	private ImageButton clear;
	private ImageButton submit;
	private OnQueryTextListener mOnQueryChangeListener;
	private CharSequence mOldQueryText;

	public interface OnQueryTextListener {
		boolean onQueryTextSubmit(String query);

		boolean onQueryTextChange(String newText);
	}

	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnQueryTextListener(OnQueryTextListener listener) {
		mOnQueryChangeListener = listener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ebox = (EditText) findViewById(R.id.edit);
		ebox.setInputType(InputType.TYPE_CLASS_TEXT);
		ebox.addTextChangedListener(mTextWatcher);
		ebox.setOnEditorActionListener(mOnEditorActionListener);
		ebox.setOnKeyListener(mTextKeyListener);
		ebox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		clear = (ImageButton) findViewById(R.id.del);
		clear.setOnClickListener(this);
		submit = (ImageButton) findViewById(R.id.btn);
		submit.setOnClickListener(this);
		ebox.setFocusable(true);
		ebox.requestFocus();
		setImeVisibility(true);
	}

	public void setInputType(int type) {
		ebox.setInputType(type);
	}

	public int getInputType() {
		return ebox.getInputType();
	}

	public void setSearchButtonVisible(boolean visible) {
		submit.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasWindowFocus) {
	// super.onWindowFocusChanged(hasWindowFocus);
	// if (hasWindowFocus && ebox.hasFocus() && getVisibility() == VISIBLE) {
	// InputMethodManager inputManager = (InputMethodManager) getContext()
	// .getSystemService(Context.INPUT_METHOD_SERVICE);
	// inputManager.showSoftInput(this, 0);
	// }
	// }

	private OnKeyListener mTextKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.hasNoModifiers()) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						onSubmitQuery();
						return true;
					}
				}
			}
			return false;
		}
	};

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			SearchBox.this.onTextChanged(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};

	private void onTextChanged(CharSequence newText) {
		CharSequence text = ebox.getText();
		boolean hasText = !TextUtils.isEmpty(text);
		updateClearButton(hasText);
		if (mOnQueryChangeListener != null
				&& !TextUtils.equals(newText, mOldQueryText)) {
			mOnQueryChangeListener.onQueryTextChange(newText.toString());
		}
		mOldQueryText = newText.toString();
	}

	private void updateClearButton(boolean show) {
		clear.setVisibility(show ? VISIBLE : GONE);
	}

	@Override
	public void onClick(View v) {
		if (v == clear)
			ebox.setText("");
		else if (v == submit)
			onSubmitQuery();
	}

	private void onSubmitQuery() {
		CharSequence query = ebox.getText();
		if (query != null && TextUtils.getTrimmedLength(query) > 0) {
			setImeVisibility(false);
			if (mOnQueryChangeListener == null) {
				mOnQueryChangeListener.onQueryTextSubmit(query.toString());
			}
		}
	}

	private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

		/**
		 * Called when the input method default action key is pressed.
		 */
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			onSubmitQuery();
			return true;
		}
	};

	public void setImeVisibility(final boolean visible) {
		if (visible) {
			post(mShowImeRunnable);
		} else {
			removeCallbacks(mShowImeRunnable);
			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		}
	}

	private Runnable mShowImeRunnable = new Runnable() {
		public void run() {
			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.showSoftInput(ebox, InputMethodManager.SHOW_IMPLICIT);
			}
		}
	};

}
