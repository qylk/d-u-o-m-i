package com.qylk.app.musicplayer.service;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

public class MultiPlayer {
	MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				mIsInitialized = false;
				mMediaPlayer.release();
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setWakeMode(mcontext,
						PowerManager.PARTIAL_WAKE_LOCK);
				mHandler.sendMessageDelayed(mHandler
						.obtainMessage(MediaPlaybackService.SERVER_DIED), 2000);
				return true;
			default:
				Log.d("MultiPlayer", "Error: " + what + "," + extra);
				break;
			}
			return false;
		}
	};
	MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mp) {
			mHandler.sendEmptyMessage(MediaPlaybackService.TRACK_ENDED);
		}
	};
	private Context mcontext;
	private Handler mHandler;

	private boolean mIsInitialized = false;

	private MediaPlayer mMediaPlayer = new MediaPlayer();

	public MultiPlayer(Context context) {
		this.mcontext = context;
		mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
	}

	public long duration() {
		return mMediaPlayer.getDuration();
	}

	public int getAudioSessionId() {
		return mMediaPlayer.getAudioSessionId();
	}

	public boolean isInitialized() {
		return mIsInitialized;
	}

	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public void pause() {
		mMediaPlayer.pause();
	}

	public long position() {
		return mMediaPlayer.getCurrentPosition();
	}

	/**
	 * You CANNOT use this player anymore after calling release()
	 */
	public void release() {
		stop();
		mMediaPlayer.release();
	}

	public long seek(long whereto) {
		mMediaPlayer.seekTo((int) whereto);
		return whereto;
	}

	public void setAudioSessionId(int sessionId) {
		mMediaPlayer.setAudioSessionId(sessionId);
	}

	public void setDataSource(String path) {
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setOnPreparedListener(null);
			if (path.startsWith("content://")) {
				mMediaPlayer.setDataSource(mcontext, Uri.parse(path));
			} else {
				mMediaPlayer.setDataSource(path);
			}
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepare();
		} catch (IOException ex) {
			mIsInitialized = false;
			return;
		} catch (IllegalArgumentException ex) {
			mIsInitialized = false;
			return;
		}
		mMediaPlayer.setOnCompletionListener(listener);
		mMediaPlayer.setOnErrorListener(errorListener);
		Intent i = new Intent(
				AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
		i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
		i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, mcontext.getPackageName());
		mcontext.sendBroadcast(i);
		mIsInitialized = true;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	public void setVolume(float vol) {
		mMediaPlayer.setVolume(vol, vol);
	}

	public void start() {
		mMediaPlayer.start();
	}

	public void stop() {
		mMediaPlayer.reset();
		mIsInitialized = false;
	}
}
