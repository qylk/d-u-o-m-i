package com.qylk.app.musicplayer.service;

import android.content.Context;
import android.media.AudioManager;

public class AudioFocusHelper implements
		AudioManager.OnAudioFocusChangeListener {
	AudioManager mAM;
	MusicFocusable mFocusable;

	public AudioFocusHelper(Context ctx, MusicFocusable focusable) {
		mAM = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
		mFocusable = focusable;
	}

	public boolean abandonFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAM
				.abandonAudioFocus(this);
	}

	public void onAudioFocusChange(int focusChange) {
		if (mFocusable == null)
			return;
		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:// 已获得了音频焦点
			System.out.println(MediaPlaybackService.class.getSimpleName()+" gain AudioFocus");
			mFocusable.onGainedAudioFocus();
			break;
		case AudioManager.AUDIOFOCUS_LOSS:// 已经丢失了音频焦点比较长的时间了．你必须停止所有的音频播放．因为预料到你可能很长时间也不能再获音频焦点，所以这里是清理你的资源的好地方．比如，你必须释放
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:// 临时性的丢掉了音频焦点，很快就会重新获得,必须停止所有的音频播放，但是可以保留你的资源
			System.out.println(MediaPlaybackService.class.getSimpleName()+" lost AudioFocus");
			mFocusable.onLostAudioFocus(false);
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:// 临时性的丢掉了音频焦点，但是允许继续以低音量播放，而不是完全停止．
			System.out.println(MediaPlaybackService.class.getSimpleName()+" lost AudioFocus but can duck");
			mFocusable.onLostAudioFocus(true);
			break;
		default:break;
		}
	}

	public boolean requestFocus() {
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAM
				.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
						AudioManager.AUDIOFOCUS_GAIN);
	}
}
