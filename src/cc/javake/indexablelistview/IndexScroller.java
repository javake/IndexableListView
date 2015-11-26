/*
 * Copyright  
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.javake.indexablelistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.SectionIndexer;

public class IndexScroller {

	private float mIndexbarWidth;
	private float mIndexbarMargin;
	private float mPreviewPadding;
	private float mDensity;
	private float mScaledDensity;
	private float mAlphaRate;
	private int mState = STATE_HIDDEN;
	private int mListViewWidth;
	private int mListViewHeight;
	private int mCurrentSection = -1;
	private boolean mIsIndexing = false;
	private IndexableListView mListView = null;
	private SectionIndexer mIndexer = null;
	private String[] mSections = null;
	private RectF mIndexbarRect;

	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWING = 1;
	private static final int STATE_SHOWN = 2;
	private static final int STATE_HIDING = 3;

	public IndexScroller(Context context, IndexableListView lv) {
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		mListView = lv;
		// set index bar DEF visiable status
		mState = lv.isIndexBarAutoHide() ? STATE_HIDDEN :STATE_SHOWING;
		mAlphaRate = lv.isIndexBarAutoHide() ? 0 :1;
		
		setAdapter(mListView.getAdapter());

		mIndexbarWidth = 20 * mDensity;
		mIndexbarMargin = 10 * mDensity;
		mPreviewPadding = 5 * mDensity;
	}

	public void draw(Canvas canvas) {
		if (mState == STATE_HIDDEN)
			return;

		// mAlphaRate determines the rate of opacity
		Paint indexbarPaint = new Paint();
		indexbarPaint.setColor(Color.BLACK);
		indexbarPaint.setAlpha((int) ((mIsIndexing ? 128 : 84) * mAlphaRate));
		indexbarPaint.setAntiAlias(true);
		canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity,
				indexbarPaint);

		if (mSections != null && mSections.length > 0) {
			// Preview is shown when mCurrentSection is set
			if (mCurrentSection >= 0) {
				Paint previewPaint = new Paint();
				previewPaint.setColor(Color.BLACK);
				previewPaint.setAlpha(128);
				previewPaint.setAntiAlias(true);
				previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

				Paint previewTextPaint = new Paint();
				previewTextPaint.setColor(Color.WHITE);
				previewTextPaint.setAntiAlias(true);
				previewTextPaint.setTextSize(50 * mScaledDensity);

				float previewTextWidth = previewTextPaint
						.measureText(mSections[mCurrentSection]);
				float previewSize = 2 * mPreviewPadding
						+ previewTextPaint.descent()
						- previewTextPaint.ascent();
				RectF previewRect = new RectF(
						(mListViewWidth - previewSize) / 2,
						(mListViewHeight - previewSize) / 2,
						(mListViewWidth - previewSize) / 2 + previewSize,
						(mListViewHeight - previewSize) / 2 + previewSize);

				canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity,
						previewPaint);
				int sectionIndex = mListView.isIndexBarDrawTopSec() ? mCurrentSection :mCurrentSection +1;
				if (IndexableAdapter.TOP_SEC.equals(mSections[sectionIndex])) {
					Drawable drBig = mListView.getContext().getResources().getDrawable(R.drawable.indexablelistview_search_l);
					Bitmap bmBig = ((BitmapDrawable)drBig).getBitmap();
					float preImgPadLeft = previewSize > bmBig.getWidth() ? (previewSize -bmBig.getWidth())/2 : 0;
					float preImgPadTop = previewSize > bmBig.getHeight() ? (previewSize -bmBig.getHeight())/2 : 0;
					canvas.drawBitmap(bmBig, previewRect.left + preImgPadLeft - 1,
							previewRect.top + preImgPadTop + 1,
							previewTextPaint);
				} else {
					canvas.drawText(
							mSections[sectionIndex],
							previewRect.left + (previewSize - previewTextWidth) / 2
							- 1,
							previewRect.top + mPreviewPadding
							- previewTextPaint.ascent() + 1,
							previewTextPaint);
				}
			}

			Paint indexPaint = new Paint();
			indexPaint.setColor(Color.WHITE);
			indexPaint.setAlpha((int) (255 * mAlphaRate));
			indexPaint.setAntiAlias(true);
			indexPaint.setTextSize(12 * mScaledDensity);

			float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin)
					/ getSectionsLength();
			float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint
					.ascent())) / 2;
			
			
			for (int i = 0; i < mSections.length; i++) {
				if (i == 0 && IndexableAdapter.TOP_SEC.equals(mSections[i])) { // Head for Search
					if (mListView.isIndexBarDrawTopSec()) {
						drawSearchImg(canvas, indexPaint);
					}
					continue;
				}
				float paddingLeft = (mIndexbarWidth - indexPaint
						.measureText(mSections[i])) / 2;
				float ptNum = mListView.isIndexBarDrawTopSec() ? i : (i-1);
				canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft,
						mIndexbarRect.top + mIndexbarMargin + sectionHeight * ptNum
								+ paddingTop - indexPaint.ascent(), indexPaint);
			}
		}
	}

	private void drawSearchImg(Canvas canvas, Paint indexPaint) {
		Drawable dr = mListView.getContext().getResources().getDrawable(R.drawable.indexablelistview_search_s);
		Bitmap bm = ((BitmapDrawable)dr).getBitmap();
		float imgPadLeft = mIndexbarWidth > bm.getWidth() ? (mIndexbarWidth -bm.getWidth())/2 : 0;
		canvas.drawBitmap(bm, mIndexbarRect.left +imgPadLeft, mIndexbarRect.top + mIndexbarMargin, indexPaint);
	}
	
	/**
	 * 考虑 是否显示 置顶 section 后的 section 数量
	 * @return
	 */
	private int getSectionsLength() {
		return (!mListView.isIndexBarDrawTopSec() 
				&& IndexableAdapter.TOP_SEC.equals(mSections[0])) 
				? mSections.length-1: mSections.length;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// If down event occurs inside index bar region, start indexing
			if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY())) {
				setState(STATE_SHOWN);

				// It demonstrates that the motion event started from index bar
				mIsIndexing = true;
				// Determine which section the point is in, and move the list to
				// that section
				mCurrentSection = getSectionByPoint(ev.getY());
				mListView.setSelection(mIndexer
						.getPositionForSection(mCurrentSection));
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsIndexing) {
				// If this event moves inside index bar
				if (contains(ev.getX(), ev.getY())) {
					// Determine which section the point is in, and move the
					// list to that section
					mCurrentSection = getSectionByPoint(ev.getY());
					mListView.setSelection(mIndexer
							.getPositionForSection(mCurrentSection));
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsIndexing) {
				mIsIndexing = false;
				mCurrentSection = -1;
			}
			if (mState == STATE_SHOWN)
				setState(STATE_HIDING);
			break;
		}
		return false;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (this.mSections == null && this.mSections.length > 0) {
			return;
		}
		mListViewWidth = w;
		mListViewHeight = h;
		float f = (h - 2.0F * mIndexbarMargin)
				/ IndexableAdapter.ALL_Sections.length()
				* ((1.0F * IndexableAdapter.ALL_Sections.length() - getSectionsLength()) / 2);
		if (mListView.isIndexBarWarpHeight()) {
			mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth, 
					f + mIndexbarMargin, 
					w - mIndexbarMargin, 
					h - mIndexbarMargin - f);
		} else {
			mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth,
					mIndexbarMargin, w - mIndexbarMargin, h - mIndexbarMargin);
		}
	}

	public void show() {
		if (mState == STATE_HIDDEN)
			setState(STATE_SHOWING);
		else if (mState == STATE_HIDING)
			setState(STATE_HIDING);
	}

	public void hide() {
		if (mState == STATE_SHOWN)
			setState(STATE_HIDING);
	}

	public void setAdapter(Adapter adapter) {
		if (adapter == null) {
			return;
		}
		Adapter realAdapter = adapter;
		if (adapter instanceof HeaderViewListAdapter) {
			realAdapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
		}
		if (realAdapter instanceof SectionIndexer) {
			mIndexer = (SectionIndexer) realAdapter;
			mSections = (String[]) mIndexer.getSections();
		}
	}

	private void setState(int state) {
		if (state < STATE_HIDDEN || state > STATE_HIDING)
			return;

		mState = state;
		switch (mState) {
		case STATE_HIDDEN:
			// Cancel any fade effect
			mHandler.removeMessages(0);
			break;
		case STATE_SHOWING:
			// Start to fade in
			mAlphaRate = 0;
			fade(0);
			break;
		case STATE_SHOWN:
			// Cancel any fade effect
			mHandler.removeMessages(0);
			break;
		case STATE_HIDING:
			if (mListView.isIndexBarAutoHide()) {
				// Start to fade out after three seconds
				mAlphaRate = 1;
				fade(3000);
			}
			break;
		}
	}

	public boolean contains(float x, float y) {
		// Determine if the point is in index bar region, which includes the
		// right margin of the bar
		return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top
				+ mIndexbarRect.height());
	}

	private int getSectionByPoint(float y) {
		if (mSections == null || getSectionsLength() == 0)
			return 0;
		if (y < mIndexbarRect.top + mIndexbarMargin)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
			return getSectionsLength() - 1;
		return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect
				.height() - 2 * mIndexbarMargin) / getSectionsLength()));
	}

	private void fade(long delay) {
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (mState) {
			case STATE_SHOWING:
				// Fade in effect
				mAlphaRate += (1 - mAlphaRate) * 0.2;
				if (mAlphaRate > 0.9) {
					mAlphaRate = 1;
					setState(STATE_SHOWN);
				}

				mListView.invalidate();
				fade(10);
				break;
			case STATE_SHOWN:
				// If no action, hide automatically
				setState(STATE_HIDING);
				break;
			case STATE_HIDING:
				// Fade out effect
				mAlphaRate -= mAlphaRate * 0.2;
				if (mAlphaRate < 0.1) {
					mAlphaRate = 0;
					setState(STATE_HIDDEN);
				}

				mListView.invalidate();
				fade(10);
				break;
			}
		}

	};
}
