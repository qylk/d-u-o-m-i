package com.qylk.app.musicplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimeUtils {
	private static final Object[] sTimeArgs = new Object[10];
	private static StringBuilder sFormatBuilder = new StringBuilder();

	@Deprecated
	private static Formatter sFormatter = new Formatter(sFormatBuilder,
			Locale.US);
	@Deprecated
	private static final String TIME_FORMAT_SHORT = "%2$d:%5$02d/%7$d:%10$02d";
	@Deprecated
	private static final String TIME_FORMAT_LONG = "%1$d:%3$02d:%5$02d/%6$d:%8$02d:%10$02d";

	/**
	 * use {@link #makeTimeString} instead
	 */
	@Deprecated
	public static String formatTimeString(long sec, long duration) {
		String durationformat = (sec < 3600 ? TIME_FORMAT_SHORT
				: TIME_FORMAT_LONG);
		sFormatBuilder.setLength(0);
		sTimeArgs[0] = Long.valueOf(sec / 3600L);// h
		sTimeArgs[1] = Long.valueOf(sec / 60L);// m0
		sTimeArgs[2] = Long.valueOf(sec / 60L % 60L);// m1
		sTimeArgs[3] = Long.valueOf(sec);
		sTimeArgs[4] = Long.valueOf(sec % 60L);
		sTimeArgs[5] = Long.valueOf(duration / 3600L);
		sTimeArgs[6] = Long.valueOf(duration / 60L);
		sTimeArgs[7] = Long.valueOf(duration / 60L % 60L);
		sTimeArgs[8] = Long.valueOf(duration);
		sTimeArgs[9] = Long.valueOf(duration % 60L);
		return sFormatter.format(Locale.US, durationformat, sTimeArgs)
				.toString();
	}

	public static String makeTimeString(long sec, long duration) {
		if (duration >= 3600)
			return makeTimeStringLonger(sec, duration);
		sFormatBuilder.delete(0, sFormatBuilder.length());
		long val = sec / 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(sec / 60);
		sFormatBuilder.append(':');
		val = sec % 60L;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(sec % 60L);
		sFormatBuilder.append('/');
		val = duration / 60L;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(duration / 60L);
		sFormatBuilder.append(':');
		val = duration % 60L;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(duration % 60L);
		return sFormatBuilder.toString();
	}

	public static String makeTimeString(long sec) {
		sFormatBuilder.delete(0, sFormatBuilder.length());
		long val = sec / 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(val);
		sFormatBuilder.append(':');
		val = sec % 60L;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(val);
		return sFormatBuilder.toString();
	}

	private static String makeTimeStringLonger(long sec, long duration) {
		sFormatBuilder.delete(0, sFormatBuilder.length());
		long val = sec / 3600;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(sec / 3600);
		sFormatBuilder.append(':');
		val = sec / 60 % 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(sec / 60 % 60);
		sFormatBuilder.append(':');
		val = sec % 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(sec % 60);
		sFormatBuilder.append('/');
		val = duration / 3600;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(duration / 3600);
		sFormatBuilder.append(':');
		val = duration / 60 % 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(duration / 60 % 60);
		sFormatBuilder.append(':');
		val = duration % 60;
		if (val < 10)
			sFormatBuilder.append((char) '0');
		sFormatBuilder.append(duration % 60);
		return sFormatBuilder.toString();
	}

	public static String getTimeString(long time) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd", Locale.getDefault());
		return localSimpleDateFormat.format(new Date(time));
	}

}
