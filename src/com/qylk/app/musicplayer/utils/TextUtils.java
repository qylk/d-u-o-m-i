package com.qylk.app.musicplayer.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.database.Cursor;

public class TextUtils {
	
	private static final char hexdigits[] = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	

	/**
	 * 获取字符串MD5(16位)
	 * 
	 * @param string
	 * @return
	 */
	public static String MD5_16BITS(String string) {
		return MD5_32BITS(string).substring(8, 24);
	}

	/**
	 * 获取字符串MD5(32位)
	 * 
	 * @param string
	 * @return
	 */
	public static String MD5_32BITS(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	
	public static int[] extractCursor(Cursor cursor) {
		if (cursor == null || cursor.getCount() == 0)
			return null;
		else {
			int[] ids = new int[cursor.getCount()];
			cursor.moveToFirst();
			int i = 0;
			while (!cursor.isAfterLast()) {
				ids[i++] = cursor.getInt(0);
				cursor.moveToNext();
			}
			return ids;
		}
	}

	public static String Array2HexString(int[] array, int len) {
		if (len > array.length)
			throw new IllegalArgumentException(
					"parameter len exceeds array's real length");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			int id = array[i];
			if (id < 0) {
				continue;
			} else if (id == 0) {
				sb.append("0;");
			} else {
				while (id != 0) {
					int digit = (int) (id & 0xf);
					id >>>= 4;
					sb.append(hexdigits[digit]);
				}
				sb.append(";");
			}
		}
		return sb.toString();
	}

	public static int[] ensureArrayCapacity(int[] array, int size) {
		if (size > array.length) {
			int[] newlist = new int[size * 2];
			int len = array.length;
			for (int i = 0; i < len; i++) {
				newlist[i] = array[i];
			}
			array = newlist;
		}
		return array;
	}

	public static int[] HexString2Array(String str) {
		if (str == null)
			throw new IllegalArgumentException("HexString is null");
		int qlen = str.length();
		if (qlen > 1) {
			int[] array = new int[1];
			int plen = 0;
			int n = 0;
			int shift = 0;
			for (int i = 0; i < qlen; i++) {
				char c = str.charAt(i);
				if (c == ';') {
					array = ensureArrayCapacity(array, plen + 1);
					array[plen] = n;
					plen++;
					n = 0;
					shift = 0;
				} else {
					if (c >= '0' && c <= '9') {
						n += ((c - '0') << shift);
					} else if (c >= 'a' && c <= 'f') {
						n += ((10 + c - 'a') << shift);
					} else {
						plen = 0;
						break;
					}
					shift += 4;
				}
			}
			return array;
		}
		return new int[0];
	}
}
