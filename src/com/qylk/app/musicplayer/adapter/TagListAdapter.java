package com.qylk.app.musicplayer.adapter;

import com.qylk.app.musicplayer.utils.MediaDatabase;
import com.qylk.app.musicplayer.utils.TrackTAG;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TagListAdapter extends BaseExpandableListAdapter {
	// ����ͼ��ʾ����
	private static String[][] attributes = new String[][] {
			{ "����", "ŷ��", "�պ�", "��̨", "����" },
			{ "����", "ҡ��", "���", "����", "����", "������", "����", "��ҥ" },
			{ "�滺����", "��������", "��������", "����Ψ��", "�˸�����", "���ۿɰ�", "��������", "�ֺ����",
					"ڤ������", "�������", "�߿�����" },
			{ "����", "Ů��", "�Գ�" },
			{ "70s", "80s", "90s" },
			{ "Ӱ��", "����", "���", "��ʷ", "����", "Ϸ��", "�Ļ�", "��Ȼ", "����", "����", "����",
					"����", "��ͯ", "���", "����", "����", "�ഺ", "����" },
			{ "����", "����", "����", "����", "С����", "����˹", "����", "��«˿", "����", "����" } };
	// ��������ͼ��ͼƬ
	// ��������ͼ����ʾ����
	private static String[] TagTypes = new String[] { "����", "����", "����", "����",
			"����", "����", "����" };

	// �Լ�����һ�����������Ϣ�ķ���
	private static TextView getTextView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				AbsListView.LayoutParams.WRAP_CONTENT);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(60, 15, 0, 15);
		textView.setTextSize(25);
		textView.setTextColor(Color.WHITE);
		return textView;
	}

	private Context mContext;
	private int mId;
	private int lib_Id;
	private int[] tagvalue = new int[TagTypes.length];
	private final StringBuilder mBuilder = new StringBuilder();

	public String getTagPreviewText() {
		mBuilder.delete(0, mBuilder.length());
		for (int i = 0; i < TagTypes.length; i++)
			for (int j = 0; j < attributes[i].length; j++)
				if ((tagvalue[i] & (1 << j)) != 0)
					mBuilder.append(attributes[i][j]).append('_');
		if (mBuilder.length() != 0)
			mBuilder.deleteCharAt(mBuilder.length() - 1);
		return mBuilder.toString();
	}

	public TagListAdapter(Context context, int id) {
		mContext = context;
		mId = id;
		if (id > 0)
			getTag(id);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return attributes[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return attributes[groupPosition].length;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		CheckBox cb = new CheckBox(mContext);
		cb.setClickable(false);
		cb.setFocusable(false);
		cb.setChecked((tagvalue[groupPosition] & (1 << childPosition)) != 0);
		ll.addView(cb);

		TextView attributeName = getTextView(mContext);
		attributeName.setText(attributes[groupPosition][childPosition]);
		ll.addView(attributeName);
		return ll;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return TagTypes[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return TagTypes.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView tagName = getTextView(mContext);
		tagName.setBackgroundColor(Color.parseColor("#FF403D3D"));
		tagName.setText(getGroup(groupPosition).toString());
		return tagName;
	}

	public int getId() {
		return mId;
	}

	private void getTag(int id) {
		TrackTAG tag = MediaDatabase.getTag(mContext, id);
		if (tag != null) {
			tagvalue = tag.getTagIntArrray();
			lib_Id = tag.getLib_Id();
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	public void invertBit(int groupPosition, int childPosition) {
		tagvalue[groupPosition] = tagvalue[groupPosition]
				^ (1 << childPosition);
		notifyDataSetChanged();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void setId(int id) {
		if (id <= 0)
			throw new IllegalArgumentException(
					"id should never be negtive or zero");
		if (mId != id)
			getTag(id);
		this.mId = id;
		notifyDataSetChanged();
	}

	public int[] getNewTagIntArray() {
		return tagvalue;
	}

	public int getLib_Id() {
		return lib_Id;
	}

};
