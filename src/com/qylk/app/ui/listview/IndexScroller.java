package com.qylk.app.ui.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * Indexer for ListView
 * 
 */
public class IndexScroller {

	private float mIndexbarWidth; // ���������
	private float mIndexbarMargin; // ��������߾�
	private float mPreviewPadding; //
	private float mDensity; // �ܶ�
	private float mScaledDensity; // �����ܶ�
	private int mState = STATE_HIDDEN; // ״̬
	private int mListViewWidth; // ListView���
	private int mListViewHeight; // ListView�߶�
	private int mCurrentSection = -1; // ��ǰ����
	private boolean mIsIndexing = false; // �Ƿ���������
	private ListView mListView = null;
	private Paint previewTextPaint, previewPaint;
	private SectionIndexer mIndexer = null;
	private RectF previewRect;
	private static final char[] alphabet = { '#', 'A', 'B', 'C', 'D', 'E', 'F',
			'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
			'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private RectF mIndexbarRect;
	private Paint indexBarPaint;
	private Paint indexPaint;
	private float previewSize;
	private int hightLightPos;

	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWN = 1;
	protected static final long HIDE_DELAY = 1500;

	public IndexScroller(Context context, ListView lv) {
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		mListView = lv;
		setAdapter(mListView.getAdapter());

		mIndexbarWidth = 35 * mDensity; // ���������
		mIndexbarMargin = 10 * mDensity;// ���������
		mPreviewPadding = 5 * mDensity; // �ڱ߾�

		previewTextPaint = new Paint(); // �����滭������ĸ�Ļ���
		previewTextPaint.setColor(Color.WHITE); // ���û���Ϊ��ɫ
		previewTextPaint.setAntiAlias(true); // ���ÿ����
		previewTextPaint.setTextSize(50 * mScaledDensity); // ���������С

		previewSize = 2 * mPreviewPadding + previewTextPaint.descent()
				- previewTextPaint.ascent();

		previewPaint = new Paint();
		previewPaint.setColor(Color.BLACK);
		previewPaint.setAlpha(96);
		previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

		indexBarPaint = new Paint();
		indexBarPaint.setColor(Color.WHITE);
		indexBarPaint.setAntiAlias(true);

		indexPaint = new Paint();
		indexPaint.setColor(Color.BLUE);
		indexPaint.setAntiAlias(true);
		indexPaint.setTextSize(12 * mScaledDensity);
	}

	public void draw(Canvas canvas) {
		if (mState == STATE_HIDDEN)
			return;

		// mAlphaRate determines the rate of opacity
		// ���Ҳ���ĸ������Բ����
		canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity,
				indexBarPaint);

		if (alphabet != null && alphabet.length > 0) {
			// Preview is shown when mCurrentSection is set
			if (mCurrentSection >= 0) {

				// �ı��Ŀ��
				float previewTextWidth = previewTextPaint.measureText(Character
						.toString(alphabet[mCurrentSection]));

				// �м��������Ǹ���
				canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity,
						previewPaint);

				// �滭������ĸ
				canvas.drawText(
						alphabet,
						mCurrentSection,
						1,
						previewRect.left + (previewSize - previewTextWidth) / 2
								- 1,
						previewRect.top + mPreviewPadding
								- previewTextPaint.ascent() + 1,
						previewTextPaint);
			}
			float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin)
					/ alphabet.length;
			float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint
					.ascent())) / 2;
			for (int i = 0; i < alphabet.length; i++) {
				float paddingLeft = (mIndexbarWidth - indexPaint
						.measureText(String.valueOf(alphabet[i]))) / 2;
				if (i == hightLightPos) {
					indexPaint.setColor(Color.RED);
					indexPaint.setFakeBoldText(true);
				} else {
					indexPaint.setColor(Color.BLACK);
					indexPaint.setFakeBoldText(false);
				}
				canvas.drawText(alphabet, i, 1, mIndexbarRect.left
						+ paddingLeft, mIndexbarRect.top + mIndexbarMargin
						+ sectionHeight * i + paddingTop - indexPaint.ascent(),
						indexPaint);
			}
		}
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if (mState == STATE_HIDDEN)
			return false;
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN: // ���£���ʼ����
			// If down event occurs inside index bar region, start indexing
			if (contains(ev.getX(), ev.getY())) {
				// It demonstrates that the motion event started from index bar
				mHandler.removeMessages(0);
				mIsIndexing = true;
				// Determine which section the point is in, and move the list to
				// that section
				mCurrentSection = getSectionByPoint(ev.getY());
				hightLightPos = mCurrentSection;
				mListView.setSelection(mIndexer
						.getPositionForSection(mCurrentSection));
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // �ƶ�
			if (mIsIndexing) {
				// If this event moves inside index bar
				if (contains(ev.getX(), ev.getY())) {
					// Determine which section the point is in, and move the
					// list to that section
					mCurrentSection = getSectionByPoint(ev.getY());
					hightLightPos = mCurrentSection;
					mListView.setSelection(mIndexer
							.getPositionForSection(mCurrentSection));
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP: // ̧��
			if (mIsIndexing) {
				mIsIndexing = false;
				mCurrentSection = -1;
				mHandler.sendEmptyMessageDelayed(0, HIDE_DELAY);
			}
			break;
		}
		return false;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		mListViewWidth = w;
		mListViewHeight = h;
		mIndexbarRect = new RectF(w - mIndexbarWidth, 10, w + 10, h - 10);
		previewRect = new RectF((mListViewWidth - previewSize) / 2,
				(mListViewHeight - previewSize) / 2,
				(mListViewWidth - previewSize) / 2 + previewSize,
				(mListViewHeight - previewSize) / 2 + previewSize);
	}

	// ��ʾ
	public void show() {
		if (mState == STATE_HIDDEN) {
			mState = STATE_SHOWN;
			mListView.invalidate();
			mHandler.sendEmptyMessageDelayed(0, HIDE_DELAY);
		}
	}

	// ����
	public void hide() {
		if (mState == STATE_SHOWN) {
			mState = STATE_HIDDEN;
			mListView.invalidate();
		}
	}

	public void setAdapter(Adapter adapter) {
		if (adapter instanceof SectionIndexer) {
			mIndexer = (SectionIndexer) adapter;
		}
	}

	public void setHighLightPosition(int pos) {
		hightLightPos = mIndexer.getSectionForPosition(pos);
	}

	public boolean isVisible() {
		return mState == STATE_SHOWN;
	}

	private boolean contains(float x, float y) {
		// Determine if the point is in index bar region, which includes the
		// right margin of the bar
		return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top
				+ mIndexbarRect.height());
	}

	private int getSectionByPoint(float y) {
		if (y < mIndexbarRect.top + mIndexbarMargin)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
			return alphabet.length - 1;
		return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect
				.height() - 2 * mIndexbarMargin) / alphabet.length));
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			hide();
		}
	};
}
