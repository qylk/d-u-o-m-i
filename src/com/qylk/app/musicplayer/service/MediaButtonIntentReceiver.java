/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qylk.app.musicplayer.service;

import com.qylk.app.musicplayer.service.MediaPlaybackService;
import com.qylk.app.musicplayer.service.MediaPlaybackService.CMD;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

public class MediaButtonIntentReceiver extends BroadcastReceiver {
	private void applyCMD(Context context, CMD command) {
		Intent intent = new Intent(MediaPlaybackService.SERVICECMD);
		intent.putExtra(MediaPlaybackService.CMDNAME, command);
		context.startService(intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		CMD command = CMD.CMDEMPTY;
		if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
			command = CMD.CMDPAUSE;
		} else if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {

			KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(
					Intent.EXTRA_KEY_EVENT);
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
				return;

			switch (keyEvent.getKeyCode()) {
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				command = CMD.CMDTOGGLEPAUSE;
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				command = CMD.CMDPREVIOUS;
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				command = CMD.CMDNEXT;
				break;
			default:
				break;
			}
		}
		applyCMD(context, command);
	}
}
