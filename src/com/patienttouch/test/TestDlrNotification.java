package com.patienttouch.test;

import java.io.StringReader;

import org.xmappr.Xmappr;

import com.patienttouch.restapi.DlrNotificationApi;
import com.patienttouch.sms.threeci.ErrorNotification;
import com.patienttouch.sms.threeci.EventNotifications;
import com.patienttouch.sms.threeci.SuccessNotification;

public class TestDlrNotification {
	public static void main(String args[]) {
		new TestDlrNotification().sendDlrNotification();
	}
	
	public void sendDlrNotification() {
		Xmappr xm;
		String response = "<?xml version='1.0' encoding='UTF-8'?> " +
"<eventNotifications>" +
	"<eventNotification type=\"DLR\">" +
		"<dateTime><![CDATA[2010-12-09 09:52:11]]></dateTime>" +
		"<messageId><![CDATA[13593809244154858787]]></messageId>" +
		"<phoneNumber><![CDATA[+18582544277]]></phoneNumber>" +
		"<shortCode><![CDATA[12345]]></shortCode>" +
		"<carrierId><![CDATA[8]]></carrierId>" +
		"<carrierName><![CDATA[AT&T Mobility]]></carrierName>" +
		"<price><![CDATA[0]]></price>" +
		"<messageText><![CDATA[Hi, this is the MT sent to the end user]]></messageText>" +
		"<clientId><![CDATA[7435]]></clientId>" +
		"<clientTag><![CDATA[appointmentinfoid=6:smsid=4]]></clientTag>" +
		"<statusId><![CDATA[2]]></statusId>" +
		"<statusDescription><![CDATA[Message submitted to carrier/gateway]]></statusDescription>" +
		"<detailedStatusId><![CDATA[1002]]></detailedStatusId>" +
		"<detailedStatusDescription><![CDATA[Invalid destination]]></detailedStatusDescription>" +
	"</eventNotification>" +  
"</eventNotifications>";
		
		DlrNotificationApi api = new DlrNotificationApi();
		api.processDlrNotification(response);
		
		//xm = new Xmappr(EventNotifications.class);
		//StringReader reader = new StringReader(response);
		//EventNotifications ens = (EventNotifications) xm.fromXML(reader);
		
		System.out.println("Parsed error response");
	}
}
