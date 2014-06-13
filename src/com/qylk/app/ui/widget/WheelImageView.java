package com.qylk.app.ui.widget;

import com.qylk.app.ui.animation.SeamlessAnimation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

public class WheelImageView extends ImageView implements AnimationListener {
	public interface AnimationEndListener {
		public void onAnimationEnd();
	}

	private float LastDegree;
	private AnimationEndListener lis;
	private boolean inAnimation;
	SeamlessAnimation mAnimation;

	public WheelImageView(Context context) {
		super(context);
		mAnimation = null;
	}

	public WheelImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAnimation = null;
	}

	public WheelImageView(Context context, AttributeSet attrs, int defStyle) {
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

	public boolean inAnimation() {
		return inAnimation;
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility != View.VISIBLE && inAnimation) {
			stopAnimation();
		}
	}

	public void resetAnimation() {
		if (inAnimation)
			stopAnimation();
		doInitAnimation((int) (500 * LastDegree / 360), 0, LastDegree, 0);
		startAnimation(mAnimation);
	}

}
