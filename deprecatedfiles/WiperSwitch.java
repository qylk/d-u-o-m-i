package com.qylk.app.musicplayer.deprecated;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.qylk.app.musicplayer.R;

public class WiperSwitch extends View implements OnTouchListener {
	/**
	 * 回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface OnChangedListener {
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
	}
	private Bitmap bg_on, bg_off, slipper;
	private OnChangedListener listener;
	private boolean nowStatus = false;
	private int w_bg;

	private int w_slipper;

	public WiperSwitch(Context context) {
		this(context, null);
	}

	public WiperSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WiperSwitch(Context context, AttributeSet attrs, int defstyle) {
		super(context, attrs, defstyle);
		TypedArray a = context
				.obtainStyledAttributes(attrs, R.styleable.Switch);
		bg_on = BitmapFactory
				.decodeResource(getResources(), R.drawable.checkon);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.checkoff);
		int resid = a
				.getResourceId(R.styleable.Switch_slipper, R.drawable.loop);
		slipper = BitmapFactory.decodeResource(getResources(), resid);
		a.recycle();
		w_bg = bg_on.getWidth();
		w_slipper = slipper.getWidth();
		setOnTouchListener(this);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (nowStatus) {
			canvas.drawBitmap(bg_off, 0, 0, null);
		} else {
			canvas.drawBitmap(bg_on, 0, 0, null);
		}
		canvas.drawBitmap(slipper, nowStatus ? (w_bg - w_slipper) : 0, 0, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(bg_on.getWidth() + 2, bg_on.getHeight());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // {
			if (event.getX() > w_bg || event.getY() > bg_off.getHeight())
				return false;
			break;
		case MotionEvent.ACTION_MOVE: {
			return true;
		}
		case MotionEvent.ACTION_UP: {
			boolean changed;
			if (event.getX() > (w_bg / 2)) {
				changed = (nowStatus == false);
				nowStatus = true;
			} else {
				changed = (nowStatus == true);
				nowStatus = false;
			}
			if (listener != null && changed) {
				listener.OnChanged(this, nowStatus);
			}
			break;
		}
		}
		// 刷新界面
		invalidate();
		return true;
	}

	/**
	 * 设置滑动开关的初始状态，供外部调用
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		nowStatus = checked;
	}

	/**
	 * 为WiperSwitch设置一个监听，供外部调用的方法
	 * 
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener) {
		this.listener = listener;
	}

}
