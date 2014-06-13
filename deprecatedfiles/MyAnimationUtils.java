package com.qylk.app.musicplayer.deprecated;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

public class MyAnimationUtils {
	public static final LayoutAnimationController getListCascadeLayoutAnimation() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	public static final Animation getXTranscentAnimation(float fromXValue,
			float toXValue, int type) {
		TranslateAnimation trans = new TranslateAnimation(type, fromXValue,
				type, toXValue, type, 0, type, 0);
		trans.setInterpolator(new DecelerateInterpolator());
		trans.setDuration(250);
		return trans;
	}
}
