package com.patienttouch.sms.threeci;

import java.util.List;

import org.xmappr.Element;
import org.xmappr.RootElement;


@RootElement(name="eventNotifications")
public class EventNotifications {
	@Element
	public List<EventNotification> eventNotification;
}