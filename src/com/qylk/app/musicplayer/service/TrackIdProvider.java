package com.qylk.app.musicplayer.service;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.qylk.app.musicplayer.utils.MusicUtils;
import com.qylk.app.musicplayer.utils.MyInteger;
import com.qylk.app.musicplayer.utils.ServiceProxy;
import com.qylk.app.musicplayer.utils.TextUtils;

/**
 * @author qylk2014 <br>
 *         V1.3 <br>
 *         2014-06-04 <br>
 *         {@link http://www.qylk.blog.163.com}
 */
public class TrackIdProvider {
	private static final int LIST_DEFAULT_SIZE = 10;
	public static final String QUEUE_CHANGED = "com.qylk.music.queuechanged";

	private int idIndex = -1;
	private int listLen = 0;
	private MODE mMode = MODE.NORMAL;
	private int[] mPlayList = new int[LIST_DEFAULT_SIZE];
	private SharedPreferences mPreferences;
	private Context mContext;
	private static TrackIdProvider instance;

	public enum MODE {
		NORMAL(0), REPEAT(1), LOCKONE(2), SHUFFLE(3);
		private int id;

		MODE(int id) {
			this.id = id;
		}

		static MODE getMode(int id) {
			for (MODE m : MODE.values()) {
				if (m.getId() == id) {
					return m;
				}
			}
			return NORMAL;
		}

		int getId() {
			return id;
		}
	};

	public TrackIdProvider(Context context) {
		mPreferences = context.getSharedPreferences("Music",
				Context.MODE_PRIVATE);
		mContext = context;
		reload();
	}

	public static TrackIdProvider getInstance(Context context) {
		if (instance == null)
			instance = new TrackIdProvider(context);
		return instance;
	}

	public void changeList(Cursor cursor, boolean shuffle) {
		int[] ids = com.qylk.app.musicplayer.utils.TextUtils
				.extractCursor(cursor);
		if (ids != null) {
			mPlayList = ids;
			listLen = ids.length;
			if (shuffle)
				MusicUtils.shuffleArray(mPlayList, 0, listLen);
			idIndex = 0;
		}
	}

	public void setToPosition(Cursor cursor, int position) {
		changeList(cursor, false);
		setPosition(position);
		// idIndex = -1;// 当前播放位置置为-1，为不妨碍moveItems
		// moveItems(position, 0);// 当position位置的歌曲id移到队列首
		// idIndex = 0;
	}

	/**
	 * 将id值存入列表末尾
	 * 
	 * @param id
	 */
	public void addToEnd(int id) {
		if (searchId(id) != -1)
			return;
		ensureListCapacity(++listLen);
		mPlayList[listLen - 1] = id;
	}

	/**
	 * 将id值存入播放指针idIndex的下一个位置，并更新idIndex为该id值存入的位置，如果此id值已经存在，
	 * 则直接将播放指针idIndex指向该id的存在位置
	 * 
	 * @param id
	 */
	public void addToNext(int id) {
		int p = searchId(id);
		if (p == -1) {
			ensureListCapacity(++listLen);
			p = listLen - 1;
			mPlayList[p] = id;
		}
		moveItems(p, (p <= idIndex) ? idIndex : idIndex + 1);
	}

	public void close() {
		SharedPreferences.Editor ed = mPreferences.edit();
		ed.putString("queue", TextUtils.Array2HexString(mPlayList, listLen));
		ed.putInt("curpos", idIndex);
		ed.putInt("mode", mMode.getId());
		ed.commit();
	}

	private void ensureListCapacity(int size) {
		if (mPlayList == null) {
			mPlayList = new int[LIST_DEFAULT_SIZE];
		} else if (size > mPlayList.length) {
			int[] newlist = new int[size * 2];
			int len = mPlayList.length;
			for (int i = 0; i < len; i++) {
				newlist[i] = mPlayList[i];
			}
			mPlayList = newlist;
		}
	}

	/**
	 * 移动位置
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean moveItems(int from, int to) {
		if (from >= listLen) {
			from = listLen - 1;
		}
		if (to >= listLen) {
			to = listLen - 1;
		}
		if ((from < 0) || (to < 0))
			throw new IllegalArgumentException("illegal index values" + from
					+ " or " + to);
		int tmp = mPlayList[from];
		if (from < to) {
			for (int i = from; i < to; i++) {
				mPlayList[i] = mPlayList[i + 1];
			}
			if (idIndex == from) {
				idIndex = to;
			} else if (idIndex > from && idIndex <= to) {
				idIndex--;
			}
		} else {
			for (int i = from; i > to; i--) {
				mPlayList[i] = mPlayList[i - 1];
			}
			if (idIndex == from) {
				idIndex = to;
			} else if (idIndex >= to && idIndex <= from) {
				idIndex++;
			}
		}
		mPlayList[to] = tmp;
		return true;
	}

	public int getCurPosition() {
		return idIndex;
	}

	public int getId() {
		if (idIndex >= 0)
			return mPlayList[idIndex];
		else
			return -1;
	}

	/**
	 * copy at most the number of TrackId as second param {@link limit} refers
	 * from plaiyingList, the copy start position is 10 lower than NowPlaying
	 * position if NowPlaying position is bigger than 10,otherwise, copy will
	 * from the beginning of Playing List;the end position(not included) is the
	 * length of Playing List if the request number is more than the reminde
	 * number after the position of NowPlaying,otherwise,the end position is
	 * (start position +limit)
	 * 
	 * @param offset
	 *            the start position saved in this value
	 * @param limit
	 * @return
	 */
	public int[] getCopyList(MyInteger offset, int limit) {
		int start = idIndex < 10 ? 0 : (idIndex - 10);
		int end = (start + limit <= listLen) ? start + limit : listLen;
		offset.setIntVal(start);
		int[] t = new int[end - start];
		// native implementation,more efficient?
		System.arraycopy(mPlayList, start, t, 0, end - start);
		// for (int i = start, j = 0; i < end; i++, j++)
		// t[j] = mPlayList[i];
		return t;
	}

	public MODE getMode() {
		return mMode;
	}

	public int getSize() {
		return listLen;
	}

	public boolean hasNext() {
		if (listLen <= 0)
			return false;
		if (mMode == MODE.REPEAT || mMode == MODE.LOCKONE)
			return true;
		else
			return idIndex < listLen - 1;
	}

	public boolean hasPrevious() {
		return idIndex > 0;
	}

	public boolean isEmpty() {
		return listLen <= 0;
	}

	public int next() {
		if (mMode != MODE.LOCKONE)
			idIndex = (++idIndex) % listLen;
		return getId();
	}

	private void parseQueueString(String str) {
		mPlayList = TextUtils.HexString2Array(str);
		listLen = mPlayList.length;
	}

	public int previous() {
		if (idIndex <= 0)
			idIndex = listLen - 1;
		else
			idIndex -= 1;
		return getId();
	}

	public void reload() {
		idIndex = mPreferences.getInt("curpos", -1);
		parseQueueString(mPreferences.getString("queue", ""));
		mMode = MODE.getMode(mPreferences.getInt("mode", MODE.NORMAL.getId()));
	}

	public boolean remove(int first, int last) {
		if (last < first)
			return false;
		if (first < 0)// exception
			first = 0;
		if (last >= listLen)
			last = listLen - 1;
		boolean forceStop = (idIndex >= first && idIndex <= last);
		if (forceStop) {
			idIndex = (last < listLen - 1) ? first : first - 1;
		} else if (idIndex > last) {
			idIndex -= (last - first + 1);
		}
		int num = listLen - last - 1;
		for (int i = 0; i < num; i++) {
			mPlayList[first + i] = mPlayList[last + 1 + i];
		}
		listLen -= last - first + 1;
		if (forceStop)// notification service play new one
			ServiceProxy.play();
		return true;
	}

	/**
	 * search for the position of the id in the arrayList
	 * 
	 * @param id
	 * @return the index of the id,or -1 if found nothing
	 */
	private int searchId(int id) {
		for (int i = 0; i < listLen; i++) {
			if (mPlayList[i] == id)
				return i;
		}
		return -1;
	}

	public void setMode(MODE mode) {
		if (mode == MODE.SHUFFLE) {
			if (listLen > 1) {
				int id = getId();
				MusicUtils.shuffleArray(mPlayList, 0, listLen);
				idIndex = searchId(id);
				moveItems(idIndex, 0);
				notifyChanged();
			}
		} else if (mMode == MODE.SHUFFLE) {
			int id = getId();
			Arrays.sort(mPlayList, 0, listLen);
			idIndex = searchId(id);
			notifyChanged();
		}
		mMode = mode;
	}

	private void notifyChanged() {
		mContext.sendBroadcast(new Intent(QUEUE_CHANGED));
	}

	public void setNextMode() {
		setMode(MODE.getMode(mMode.getId() + 1));
	}

	public void setPosition(int idx) {
		if (idx > listLen - 1)
			throw new IllegalArgumentException("idx must be <" + listLen);
		idIndex = idx;
	}

	public void clear() {
		idIndex = -1;
		listLen = 0;
	}
}