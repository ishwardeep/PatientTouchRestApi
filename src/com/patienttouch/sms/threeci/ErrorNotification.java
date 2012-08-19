package com.patienttouch.sms.threeci;

import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement(name="errorNotification")
public class ErrorNotification {
	@Element
	public List<String> message;
}
