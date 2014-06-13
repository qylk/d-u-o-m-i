package com.qylk.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;

public class VolumeAdjustView extends RelativeLayout implements
		View.OnClickListener, SeekBar.OnSeekBarChangeListener {
	private static final int DISMISS_DELAY_FROMUSER = 3000;
	private static final int DISMISS_DELAY_FROMKEY = 1000;
	private SeekBar volBar;
	private TextView volText;
	private ImageView btnClose;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			VolumeAdjustView.this.setVisibility(View.GONE);
		}
	};

	public VolumeAdjustView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void init() {
		AudioManager am = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		volBar.setMax(max);
		volBar.setProgress(volume);
		int percent = volume * 100 / max;
		volText.setText(String.valueOf(percent));
	}

	@Override
	public void setVisibility(int visibility) {
		if ((getVisibility() != View.VISIBLE) && (visibility == View.VISIBLE)) {
			init();
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, DISMISS_DELAY_FROMUSER);
		}
		super.setVisibility(visibility);
	}

	public final void adjustVolume(int value) {
		super.setVisibility(View.VISIBLE);
		init();
		int vol;
		if (value == 0)
			vol = 0;
		else
			vol = value + volBar.getProgress();
		volBar.setProgress(vol);
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, DISMISS_DELAY_FROMKEY);
	}

	@Override
	public boolean hasFocusable() {
		return false;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		volText = ((TextView) findViewById(R.id.title));
		volBar = ((SeekBar) findViewById(R.id.progress));
		btnClose = ((ImageView) findViewById(R.id.image));
		TextView subtitle = ((TextView) findViewById(R.id.subtitle));
		TextView subtitle1 = ((TextView) findViewById(R.id.subtitle1));
		if (!isInEditMode()) {
			Typeface typeface = Typeface.createFromAsset(getContext()
					.getAssets(), "dmttf.TTF");
			if (typeface != null) {
				volText.setTypeface(typeface);
				subtitle.setTypeface(typeface);
				subtitle1.setTypeface(typeface);
			}
		}
		AudioManager am = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		btnClose.setOnClickListener(this);
		volBar.setMax(max);
		volBar.setProgress(volume);
		volBar.setOnSeekBarChangeListener(this);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) volBar
				.getLayoutParams();
		params.rightMargin = ((getResources().getDisplayMetrics().widthPixels / 2 - (int) (92.0F * getResources()
				.getDisplayMetrics().density) / 2) / 2);
		volBar.setVisibility(View.VISIBLE);
		volBar.setLayoutParams(params);
		forceLayout();
		int percent = 100 * volBar.getProgress() / max;
		volText.setText(String.valueOf(percent));
		setVisibility(View.GONE);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		AudioManager am = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int volumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (progress > volumn)
			for (int i = 0; i < progress - volumn; i++)
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE, 0);
		else
			for (int i = 0; i < volumn - progress; i++)
				am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER, 0);
		int percent = 100 * progress / max;
		volText.setText(String.valueOf(percent));
		if (fromUser) {
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, DISMISS_DELAY_FROMUSER);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		// touch events should not back to parent views,otherwise bottom layer
		// views may respond to it,so consume it here any way.
		return true;
	}
}
