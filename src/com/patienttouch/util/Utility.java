package com.patienttouch.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
	private static final SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	private static final SimpleDateFormat dateTime_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
	
	public static String getDateTimeInyyyyMMdd() {
		return dateTime.format(new Date());
	}
	
	public static String getDateTimeInyyyyMMdd(Date d) {	
		return dateTime_yyyyMMdd.format(d);
	}
	
	public static String getDateInyyyyMMdd(Date d) {
		return date.format(d);
	}
}
