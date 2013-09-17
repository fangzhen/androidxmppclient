package com.sys.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeRender {

	private static SimpleDateFormat formatBuilder;

	public static String getDate(String format) {
		formatBuilder = new SimpleDateFormat(format);
		return formatBuilder.format(new Date());
	}

	public static String getDate() {
		return getDate("MM-dd  hh:mm:ss");
	}
}
