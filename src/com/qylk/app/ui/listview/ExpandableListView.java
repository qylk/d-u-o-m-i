package com.qylk.app.ui.listview;

import java.util.BitSet;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.qylk.app.ui.animation.ExpandCollapseAnimation;

/**
 * Depend on ListCell.java as its ItemView or a LinearLayout which contains a
 * listItemView and an extra view which will be expanded to show when the toggle
 * button is clicked;the toggle button should be placed into listItemView so the
 * user can click it,and its id must be set as {@link android.R.id.toggle}, the
 * extra view must be set its id as {@link android.R.id.extractArea} if no
 * ListCellLayout is select as the rootLayout
 * 
 * @see ListCellLayout
 * @see track_list_item.xml
 * 
 */
public class ExpandableListView extends ListView {
	private View lastOpen = null;
	private int lastOpenPosition = -1;
	private int animationDuration = 250;
	private BitSet openItems = new BitSet();
	private AdapterWrapper mAdapterWrapper;
	private final SparseIntArray viewHeights = new SparseIntArray(10);
	private OnItemExpandCollapseListener expandCollapseListener;

	public interface OnItemExpandCollapseListener {
		public void onExpand(View itemView, int position);

		public void onCollapse(View itemView, int position);
	}

	public interface ViewCollapseRequest {
		public void requestCollapse(View toobarview, int position);
	}

	public void setItemExpandCollapseListener(
			OnItemExpandCollapseListener listener) {
		expandCollapseListener = listener;
	}

	public void removeItemExpandCollapseListener() {
		expandCollapseListener = null;
	}

	public ExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void enableExpandOnItemClick() {
		this.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				view.findViewById(android.R.id.toggle).performClick();
			}
		});
	}

	public void enableFor(View parent, int position) {
		View more, itemToolbar;
		if (parent instanceof ListCellLayout) {
			if (((ListCellLayout) parent).hasExtraView()) {
				more = ((ListCellLayout) parent).getToggleBtn();
				more.setVisibility(View.VISIBLE);
				itemToolbar = ((ListCellLayout) parent).getExtraView();
			} else {
				throw new NullPointerException(
						"a popdown view should be put into the View hierarchy");
			}
		} else {
			more = parent.findViewById(android.R.id.toggle);
			more.setVisibility(View.VISIBLE);
			itemToolbar = parent.findViewById(android.R.id.extractArea);
		}
		if (itemToolbar == null || more == null)
			throw new NullPointerException(
					"the expandableView cant find an popdownview which id is android.R.id.extractArea");
		itemToolbar.measure(parent.getWidth(), parent.getHeight());
		itemToolbar.setId(position);
		enableFor(more, itemToolbar, position);
		//itemToolbar.requestLayout();
	}

	private void enableFor(final View button, final View target,
			final int position) {
		if (target == lastOpen && position != lastOpenPosition) {
			lastOpen = null;
		}
		if (position == lastOpenPosition) {
			lastOpen = target;
		}
		int height = viewHeights.get(position, -1);
		if (height == -1) {
			viewHeights.put(position, target.getMeasuredHeight());
			updateExpandable(target, position);
		} else {
			updateExpandable(target, position);
		}
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View view) {
				Animation a = target.getAnimation();
				if (a != null && a.hasStarted() && !a.hasEnded()) {
					a.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							toggle(target, position);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				} else {
					toggle(target, position);
				}
			}
		});
	}

	private void notifiyExpandCollapseListener(int type, View view, int position) {
		if (expandCollapseListener != null) {
			if (type == ExpandCollapseAnimation.EXPAND) {
				expandCollapseListener.onExpand(view, position);
			} else if (type == ExpandCollapseAnimation.COLLAPSE) {
				view.setEnabled(false);
				expandCollapseListener.onCollapse(view, position);
			}
		}
	}

	/**
	 * dismiss or show the extra view
	 * 
	 * @param itemview
	 * @param position
	 *            the position that the view at the listview
	 */
	public void toggle(View target, int position) {
		target.setAnimation(null);
		int type = target.getVisibility() == View.VISIBLE ? ExpandCollapseAnimation.COLLAPSE
				: ExpandCollapseAnimation.EXPAND;
		if (type == ExpandCollapseAnimation.EXPAND) {
			openItems.set(position, true);
		} else {
			openItems.set(position, false);
		}
		if (type == ExpandCollapseAnimation.EXPAND) {
			if (lastOpenPosition != -1 && lastOpenPosition != position) {
				if (lastOpen != null) {
					animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
					notifiyExpandCollapseListener(
							ExpandCollapseAnimation.COLLAPSE, lastOpen,
							lastOpenPosition);
				}
				openItems.set(lastOpenPosition, false);
			}
			lastOpen = target;
			lastOpenPosition = position;
		} else if (lastOpenPosition == position) {
			lastOpenPosition = -1;
		}
		animateView(target, type);
		notifiyExpandCollapseListener(type, target, position);
	}

	private void updateExpandable(View target, int position) {

		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) target
				.getLayoutParams();
		if (openItems.get(position)) {
			target.setVisibility(View.VISIBLE);
			params.bottomMargin = 0;
		} else {
			target.setVisibility(View.GONE);
			params.bottomMargin = 0 - viewHeights.get(position);
		}
	}

	private void animateView(final View target, final int type) {
		Animation anim = new ExpandCollapseAnimation(target, type);
		anim.setDuration(animationDuration);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (type == ExpandCollapseAnimation.EXPAND) {
					ListView listView = (ListView) ExpandableListView.this;
					int movement = target.getBottom();

					Rect r = new Rect();
					boolean visible = target.getGlobalVisibleRect(r);
					Rect r2 = new Rect();
					listView.getGlobalVisibleRect(r2);

					if (!visible) {
						listView.smoothScrollBy(movement, animationDuration);
					} else {
						if (r2.bottom == r.bottom) {
							listView.smoothScrollBy(movement, animationDuration);
						}
					}
				}
			}
		});
		target.startAnimation(anim);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		if (adapter != null) {
			mAdapterWrapper = new AdapterWrapper(adapter);
		} else {
			mAdapterWrapper = null;
		}
		super.setAdapter(mAdapterWrapper);
	}

	@Override
	public ListAdapter getAdapter() {
		if (mAdapterWrapper != null) {
			return mAdapterWrapper.mAdapter;
		} else
			return null;
	}

	class AdapterWrapper extends BaseAdapter {
		private ListAdapter mAdapter;

		public AdapterWrapper(ListAdapter adapter) {
			mAdapter = adapter;
			mAdapter.registerDataSetObserver(new DataSetObserver() {
				public void onChanged() {
					notifyDataSetChanged();
				}

				public void onInvalidated() {
					notifyDataSetInvalidated();
				}
			});
		}

		public ListAdapter getAdapter() {
			return mAdapter;
		}

		@Override
		public long getItemId(int position) {
			return mAdapter.getItemId(position);
		}

		@Override
		public Object getItem(int position) {
			return mAdapter.getItem(position);
		}

		@Override
		public int getCount() {
			return mAdapter.getCount();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mAdapter.areAllItemsEnabled();
		}

		@Override
		public boolean isEnabled(int position) {
			return mAdapter.isEnabled(position);
		}

		@Override
		public int getItemViewType(int position) {
			return mAdapter.getItemViewType(position);
		}

		@Override
		public int getViewTypeCount() {
			return mAdapter.getViewTypeCount();
		}

		@Override
		public boolean hasStableIds() {
			return mAdapter.hasStableIds();
		}

		@Override
		public boolean isEmpty() {
			return mAdapter.isEmpty();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = mAdapter.getView(position, convertView,
					ExpandableListView.this);
			if (v != convertView)
				openItems.set(position, false);
			enableFor(v, position);
			return v;
		}
	}
}
