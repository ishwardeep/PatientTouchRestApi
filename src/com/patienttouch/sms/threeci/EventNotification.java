package com.patienttouch.sms.threeci;

import org.xmappr.Element;

public class EventNotification {
	@Element
	public String dateTime;
	@Element
	public String messageId;
	@Element
	public String phoneNumber;
	@Element
	public String shortCode;
	@Element
	public String carrierName;
	@Element
	public String price;
	@Element
	public String messageText;
	@Element
	public String clientId;
	@Element
	public String clientTag;
	@Element
	public String statusId;
	@Element
	public String statusDescription;
	@Element
	public String detailedStatusId;
	@Element
	public String detailedStatusDescription;
}