package com.patienttouch.sms;

public interface SmsProvider {
	public String init();
	public String sendMessage(String phone, String message, Object userdata);
}
