package com.patienttouch.test;

import java.io.StringReader;

import org.xmappr.Xmappr;

import com.patienttouch.restapi.DlrNotificationApi;
import com.patienttouch.restapi.PostNotificationApi;
import com.patienttouch.sms.threeci.EventNotifications;

public class TestPostNotification {
	public static void main(String args[]) {
		new TestPostNotification().sendPostNotification();
	}
	
	public void sendPostNotification() {
		Xmappr xm;
		String response = "<?xml version='1.0' encoding='UTF-8'?> " +
			"<eventNotifications>" +
				"<subscriptionNotification>" +
					"<transactionId>24319962154320280</transactionId>" +
					"<trigger>1</trigger>" +
					"<triggerId>118717</triggerId>" +
					"<groupId>1234</groupId>" +
					"<phoneNumber>+18582541888</phoneNumber>" +
					"<carrierId>35</carrierId>" +
				"</subscriptionNotification>" +
				"<cancelSubscriptionNotification>" +
					"<transactionId>142893</transactionId>" +
					"<trigger>2</trigger>" +
					"<triggerId>118717</triggerId>" +
					"<groupId>15</groupId>" +
					"<phoneNumber>+18582541556</phoneNumber>" +
					"<carrierId>28</carrierId>" +
				"</cancelSubscriptionNotification>" +
			"</eventNotifications>" ;
		
		PostNotificationApi api = new PostNotificationApi();
		api.processPostNotification(response);
		
		//xm = new Xmappr(EventNotifications.class);
		//StringReader reader = new StringReader(response);
		//EventNotifications ens = (EventNotifications) xm.fromXML(reader);
		
		//System.out.println("Parsed error response");
	}
}
