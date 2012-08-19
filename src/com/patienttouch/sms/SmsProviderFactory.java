package com.patienttouch.sms;

import com.patienttouch.sms.threeci.ThreeCiSmsProvider;

public class SmsProviderFactory {
	public static final String THREE_CI_PROVIDER = "3CiProvider";
	
	public static final SmsProvider createSmsProvider(String type) {
		if (type.equalsIgnoreCase(SmsProviderFactory.THREE_CI_PROVIDER)) {
			return ThreeCiSmsProvider.getInstance();
		}
		return null;
	}
}
