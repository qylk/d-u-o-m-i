package com.qylk.app.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.qylk.app.musicplayer.R;

public class RoundImageView extends ImageView {
	private int mWidth = 0;
	private int mHeight = 0;
	private int radius;
	private Paint paint;
	private int padding;

	public RoundImageView(Context context) {
		this(context, null);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.roundImageView);
		padding = (int) a.getDimension(R.styleable.roundImageView_padding, 10);
		a.recycle();
		initPaint();
	}

	private void initPaint() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null)
			return;
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		if (bitmap == null)
			return;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int diameter = radius * 2;

		// draw bitmap using a new cavas
		Bitmap roundImg = Bitmap.createBitmap(diameter, diameter,
				Config.ARGB_8888);
		Canvas cs = new Canvas(roundImg);
		paint.setXfermode(null);
		// draw dest Layer first
		cs.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);

		// determine which area to scale
		int blank = Math.abs((bitmap.getHeight() - bitmap.getWidth()) / 2);
		Rect src = null;
		if (h > w)
			src = new Rect(0, blank, w, h - blank);
		else if (h < w)
			src = new Rect(blank, 0, w - blank, h);
		// scaled to a size that a little bigger than preferred,thus no edge is
		// visible to user
		Rect dst = new Rect(-8, -8, diameter + 8, diameter + 8);
		// draw src Layer
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		cs.drawBitmap(bitmap, src, dst, paint);
		// draw the round Image
		paint.setXfermode(null);
		canvas.drawBitmap(roundImg, mWidth / 2 - radius, mHeight / 2 - radius,
				paint);
		if (!isInEditMode())
			roundImg.recycle();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		radius = (w < h) ? w / 2 - padding : h / 2 - padding;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);// ³¤¿íÒ»ÖÂ
	}
}