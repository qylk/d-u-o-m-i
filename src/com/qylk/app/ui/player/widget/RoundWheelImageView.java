/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qylk.app.ui.player.widget;

import com.qylk.app.ui.animation.SeamlessAnimation;
import com.qylk.app.ui.widget.RoundImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;

public class RoundWheelImageView extends RoundImageView implements
		AnimationListener {
	public interface AnimationEndListener {
		public void onAnimationEnd();
	}

	private float LastDegree;
	private AnimationEndListener lis;
	private boolean inAnimation;
	SeamlessAnimation mAnimation;

	public RoundWheelImageView(Context context) {
		super(context);
		mAnimation = null;
	}

	public RoundWheelImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAnimation = null;
	}

	public RoundWheelImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mAnimation = null;
	}

	private void initAnimation(long duration, boolean isForward, int repeatCount) {
		float from;
		float to;
		if (isForward) {
			from = LastDegree;
			to = from - 360.0f;
		} else {
			from = LastDegree;
			to = from + 360.0f;
		}
		doInitAnimation(duration, repeatCount, from, to);
	}

	private void doInitAnimation(long duration, int repeatCount, float from,
			float to) {
		LinearInterpolator lir = new LinearInterpolator();
		mAnimation = new SeamlessAnimation(from, to,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mAnimation.setDuration(duration);
		mAnimation.setRepeatMode(Animation.RESTART);
		mAnimation.setRepeatCount(repeatCount);
		mAnimation.setInterpolator(lir);
		mAnimation.setAnimationListener(this);
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (lis != null) {
			lis.onAnimationEnd();
			lis = null;
		}
		inAnimation = false;
		LastDegree = mAnimation.getDegree() % 360.0f;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
	}

	public void startAnimation(long duration, boolean isForward) {
		if (inAnimation)
			return;
		startAnimation(duration, isForward, Animation.INFINITE);
	}

	public void startAnimation(long duration, boolean isForward, int repeatCount) {
		initAnimation(duration, isForward, repeatCount);
		startAnimation(mAnimation);
		inAnimation = true;
	}

	public void startAnimation(long duration, boolean isForward,
			int repeatCount, AnimationEndListener listener) {
		this.lis = listener;
		startAnimation(duration, isForward, repeatCount);
	}

	public void stopAnimation() {
		if (inAnimation) {
			LastDegree = mAnimation.getDegree() % 360.0f;
			mAnimation.cancel();
			mAnimation.reset();
			inAnimation = false;
		}
	}

	public void resetAnimation() {
		if (inAnimation)
			stopAnimation();
		doInitAnimation((int) (500 * LastDegree / 360), 0, LastDegree, 0);
		startAnimation(mAnimation);
	}

	public boolean inAnimation() {
		return inAnimation;
	}

}
