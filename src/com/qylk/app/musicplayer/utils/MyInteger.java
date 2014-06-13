package com.qylk.app.musicplayer.utils;

public class MyInteger extends Number {
	private static final long serialVersionUID = 4664145622009190765L;
	private int value;

	public MyInteger(int value) {
		this.value = value;
	}

	public void setIntVal(int value) {
		this.value = value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

}
