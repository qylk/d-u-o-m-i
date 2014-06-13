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
		case AudioManager.AUDIOFOCUS_GAIN:// �ѻ������Ƶ����
			System.out.println(MediaPlaybackService.class.getSimpleName()+" gain AudioFocus");
			mFocusable.onGainedAudioFocus();
			break;
		case AudioManager.AUDIOFOCUS_LOSS:// �Ѿ���ʧ����Ƶ����Ƚϳ���ʱ���ˣ������ֹͣ���е���Ƶ���ţ���ΪԤ�ϵ�����ܺܳ�ʱ��Ҳ�����ٻ���Ƶ���㣬�������������������Դ�ĺõط������磬������ͷ�
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:// ��ʱ�ԵĶ�������Ƶ���㣬�ܿ�ͻ����»��,����ֹͣ���е���Ƶ���ţ����ǿ��Ա��������Դ
			System.out.println(MediaPlaybackService.class.getSimpleName()+" lost AudioFocus");
			mFocusable.onLostAudioFocus(false);
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:// ��ʱ�ԵĶ�������Ƶ���㣬������������Ե��������ţ���������ȫֹͣ��
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
