package com.qylk.app.ui.player.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.service.TrackIdProvider;
import com.qylk.app.musicplayer.service.TrackIdProvider.MODE;
import com.qylk.app.musicplayer.utils.ConstantValueDef;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.musicplayer.utils.TimeUtils;
import com.qylk.app.ui.VolumeAdjustView;

public class PlayerBar extends FrameLayout implements View.OnClickListener,
		OnSeekBarChangeListener, Callback {
	private static final int INIT = 0;
	private static final int UPDATE = 1;
	private static final int REFLESH = 2;
	private ImageButton mode, play, pre, next, vol;
	private SeekBar progressbar;
	private TextView time;
	private TextView duration;
	private Handler mHandler = new Handler(this);
	private TextView progressTip;
	private ViewStub volume;
	private VolumeAdjustView volumeAdj;
	private boolean paused;
	private RelativeLayout.LayoutParams params;

	public PlayerBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHandler.sendEmptyMessageDelayed(INIT, 200);
	}

	private void collectViews() {
		mode = (ImageButton) findViewById(R.id.mode);
		play = (ImageButton) findViewById(R.id.play);
		pre = (ImageButton) findViewById(R.id.pre);
		next = (ImageButton) findViewById(R.id.next);
		vol = (ImageButton) findViewById(R.id.vol);
		time = (TextView) findViewById(R.id.time);
		duration = (TextView) findViewById(R.id.duration);
		progressTip = (TextView) findViewById(R.id.poptxt);
		progressbar = (SeekBar) findViewById(R.id.progressbar);
		mode.setOnClickListener(this);
		play.setOnClickListener(this);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		vol.setOnClickListener(this);
		progressbar.setOnSeekBarChangeListener(this);
		volume = (ViewStub) findViewById(R.id.volume);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (volumeAdj == null)
			volumeAdj = ((VolumeAdjustView) volume.inflate());
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			volumeAdj.adjustVolume(1);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			volumeAdj.adjustVolume(-1);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
			volumeAdj.adjustVolume(0);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				&& volumeAdj.getVisibility() == View.VISIBLE) {
			volumeAdj.setVisibility(View.INVISIBLE);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mode:
			MODE mode = TrackIdProvider.getInstance().setNextMode();
			if (mode == MODE.SHUFFLE)
				getContext().sendBroadcast(
						new Intent(ConstantValueDef.QUEUE_CHANGED));
			updateModeDrawable(mode);
			break;
		case R.id.pre:
			ServiceProxy.pre();
			break;
		case R.id.play:
			ServiceProxy.togglePlay();
			setPauseButtonImage();
			break;
		case R.id.next:
			ServiceProxy.next();
			break;
		case R.id.vol:
			if (volumeAdj == null)
				volumeAdj = ((VolumeAdjustView) volume.inflate());
			volumeAdj.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void setPauseButtonImage() {
		if (ServiceProxy.isPlaying()) {
			this.play.setImageResource(R.drawable.player_pause_lock);
		} else
			this.play.setImageResource(R.drawable.player_play_lock);
	}

	private void updateModeDrawable(MODE mode) {
		int res;
		switch (mode) {
		case LOCKONE:
			res = R.drawable.player_mode_single;
			break;
		case REPEAT:
			res = R.drawable.player_mode_loop;
			break;
		case SHUFFLE:
			res = R.drawable.player_mode_random;
			break;
		default:
			res = R.drawable.player_mode_order;
			break;
		}
		this.mode.setImageResource(res);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser)
			updateTipView(progress);
	}

	private void updateTipView(int time) {
		String timestr = TimeUtils.makeTimeString(time);
		if (progressTip.getVisibility() == View.VISIBLE) {
			if (params == null)
				params = (RelativeLayout.LayoutParams) progressTip
						.getLayoutParams();
			float percent = 1.0f * time / progressbar.getMax();
			float x = progressbar.getThumbOffset() * Math.abs(0.5f - percent)
					* 2;
			if (percent > 0.5f)
				x = -x;
			params.leftMargin = (int) (progressbar.getMeasuredWidth() * percent
					+ progressbar.getLeft() + x - progressTip
					.getMeasuredWidth() / 2);
			progressTip.requestLayout();
			progressTip.setText(timestr);
		}
		this.time.setText(timestr);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		progressTip.setVisibility(View.VISIBLE);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		ServiceProxy.seek(seekBar.getProgress() * 1000);
		progressTip.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == REFLESH)
			updatePlayerView();
		else if (msg.what == UPDATE)
			updatePlayerView();
		else if (msg.what == INIT)
			collectViews();
		return true;
	}

	private void updateProgress() {
		int progress = (int) (ServiceProxy.getPosition() / 1000);
		progressbar.setProgress(progress);
		time.setText(TimeUtils.makeTimeString(progress));
	}

	public void onResume() {
		IntentFilter f = new IntentFilter();
		f.addAction(MediaPlaybackService.META_CHANGED);
		f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
		getContext().registerReceiver(mTrackListListener, f);
		// updatePlayerView();
		mHandler.sendEmptyMessageDelayed(UPDATE, 200);
	}

	private void updatePlayerView() {
		init();
		paused = !ServiceProxy.isPlaying();
		updateProgress();
		queueNextRefresh(1000);
	}

	public void onPause() {
		paused = true;
		getContext().unregisterReceiver(mTrackListListener);
		mHandler.removeMessages(REFLESH);
	}

	private BroadcastReceiver mTrackListListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MediaPlaybackService.META_CHANGED)) {
				init();
				queueNextRefresh(1);
			} else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
				paused = !intent.getBooleanExtra("playing", false);
				setPauseButtonImage();
				queueNextRefresh(1);
			}
		}
	};

	public void init() {
		int duration = (int) (ServiceProxy.getDuration() / 1000);
		progressbar.setMax(duration);
		progressbar.setProgress(0);
		time.setText("00:00");
		this.duration.setText(TimeUtils.makeTimeString(duration));
		setPauseButtonImage();
		updateModeDrawable(TrackIdProvider.getInstance().getMode());
	}

	private void queueNextRefresh(long delay) {
		if (paused) {
			this.mHandler.removeMessages(REFLESH);
		} else {
			Message msg = this.mHandler.obtainMessage(REFLESH);
			this.mHandler.sendMessageDelayed(msg, delay);
		}
	}

}
