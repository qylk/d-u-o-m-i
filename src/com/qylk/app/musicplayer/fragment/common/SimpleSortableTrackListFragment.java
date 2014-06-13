package com.qylk.app.musicplayer.fragment.common;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.qylk.app.musicplayer.R;
import com.qylk.app.musicplayer.fragment.NowPlayingCursor;

public class SimpleSortableTrackListFragment extends SimpleTrackListFragment {
	private DragSortListView listview;
	private DragSortController mController;

	protected DragSortController onBuildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.num);// ��ק�ѱ���Ĭ��ÿ��ItemViewΪDragHandle
		controller.setFlingHandleId(R.id.num);// ɾ���ѱ�
		controller.setSortEnabled(true);
		controller.setRemoveEnabled(true);
		controller.setBackgroundColor(getResources().getColor(// FloatView�ı���ɫ
				R.color.float_color));
		controller.setDragInitMode(DragSortController.ON_DRAG);
		controller.setRemoveMode(DragSortController.FLING_REMOVE);// ����ɾ��ģʽ
		return controller;
	}

	public final DragSortController getController() {
		return mController;
	}

	@Override
	protected Cursor onCreateCursor() {
		return new NowPlayingCursor(getActivity());
	}

	@Override
	protected void onCreateContentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		super.onCreateContentView(inflater, container, savedInstanceState);
		listview = (DragSortListView) getListView();
		mController = onBuildController(listview);
		listview.setFloatViewManager(mController);
		listview.setOnTouchListener(mController);
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		try {
//			// ʹ�ô����޸�ԭActionBar���ף�ֱ�����÷����滻Ϊ���ݵ�ActionBar��ͼ
//			Field field = ActionBarFragment.class.getDeclaredField(
//					"mFrameRes");
//			field.setAccessible(true);
//			field.set(this, Integer.valueOf(R.layout.contentplayingframe));
//		} catch (Exception e) {
//		}
//		super.onCreate(savedInstanceState);
//	}

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	// mPlayingPositionListener.onReceive(null, null);// ����
	// // ��mAdapter��ʵ����DragSortListener����Ͳ�д��
	// // listview.setDropListener(mDropListener);
	// // listview.setRemoveListener(mRemoveListener);
	// getListView().setOnItemClickListener(this);
	// }

	protected int getLayout() {
		return R.layout.listview_sortable;
	}

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// ServiceProxy.PlaySelected((int) getAdapter().getItemId(position));
	// }

}
