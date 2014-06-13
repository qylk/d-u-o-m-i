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
	// 子视图显示文字
	private static String[][] attributes = new String[][] {
			{ "华语", "欧美", "日韩", "港台", "世界" },
			{ "流行", "摇滚", "乡村", "民族", "国风", "纯音乐", "天籁", "民谣" },
			{ "舒缓柔情", "抒情优美", "欢快自由", "纯净唯美", "伤感忧郁", "甜蜜可爱", "清新治愈", "恢弘大气",
					"冥想游历", "深沉悲凉", "高亢激昂" },
			{ "男声", "女声", "对唱" },
			{ "70s", "80s", "90s" },
			{ "影视", "动漫", "祖国", "历史", "军旅", "戏曲", "文化", "自然", "爱情", "亲情", "乡情",
					"友情", "儿童", "佛教", "生活", "武侠", "青春", "理想" },
			{ "钢琴", "古筝", "二胡", "吉他", "小提琴", "萨克斯", "笛子", "葫芦丝", "琵琶", "其他" } };
	// 设置组视图的图片
	// 设置组视图的显示文字
	private static String[] TagTypes = new String[] { "语种", "流派", "节奏", "唱法",
			"年龄", "主题", "乐器" };

	// 自己定义一个获得文字信息的方法
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
