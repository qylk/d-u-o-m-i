package com.qylk.app.musicplayer.utils;

import java.util.Random;

public class Shuffler {
	private int[] list;
	private int pos;
	private Random random = new Random();

	public Shuffler(int max) {
		if (max < 0)
			throw new IllegalArgumentException("max must be >=0");
		list = new int[max + 1];
		for (int i = 0; i < max + 1; i++) {
			list[i] = i;
		}
		shuffle();
	}

	public boolean isover() {
		return pos == list.length;
	}

	public int next() {
		if (pos > list.length - 1) {
			shuffle();
			pos = 0;
		}
		return list[pos++];
	}

	private void shuffle() {
		if (list.length == 1)
			return;
		for (int i = list.length - 1; i > 0; i--) {
			int idx = random.nextInt(i);
			int t = list[i];
			list[i] = list[idx];
			list[idx] = t;
		}
	}
}
