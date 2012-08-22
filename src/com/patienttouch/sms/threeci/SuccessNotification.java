package com.patienttouch.sms.threeci;

import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement(name="successNotification")
public class SuccessNotification {
	@Element
	public String phoneNumber;
	@Element
	public List<String> message;
	@Element
	public String triggerId;
}
