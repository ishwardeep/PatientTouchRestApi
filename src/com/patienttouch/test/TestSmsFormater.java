package com.patienttouch.test;

import java.util.HashMap;
import java.util.Map;

import com.patienttouch.sms.SmsFormater;

public class TestSmsFormater {
	private static String sms = "[First] has an appt with Dr [Doctor] on [Date] [Time] at our [Office] office. Reply with 1 to confirm and 2 to reschedule.";
	public static void main(String args[]) {
		SmsFormater format = new SmsFormater();
		Map<String,String> val = new HashMap<String, String>();
		val.put("firstName", "Chandra");
		val.put("doctor", "Tarun");
		val.put("date", "06/08/2012");
		val.put("time", "9:00 am");
		val.put("officeName", "Miami");
		
		System.out.println(format.buildMessage(TestSmsFormater.sms, val));
		
	}
}
