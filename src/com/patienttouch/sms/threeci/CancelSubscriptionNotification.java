package com.patienttouch.sms.threeci;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement(name="cancelSubscriptionNotification")
public class CancelSubscriptionNotification {
	@Element
	public String transactionId;
	@Element
	public String trigger;
	@Element
	public String triggerId;
	@Element
	public String groupId;
	@Element
	public String phoneNumber;
	@Element
	public String carrierId;
}
