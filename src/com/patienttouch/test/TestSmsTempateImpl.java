package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.SmsTemplateImpl;
import com.patienttouch.client.SmsTemplateInfo;
import com.patienttouch.hibernate.SmsTemplateType;
import com.patienttouch.hibernate.UserRole;

public class TestSmsTempateImpl {
	public static void main(String args[]) {
		String msg = "[First] has an appt with Dr [Doctor] on [Datetime] at our [office] office. Reply with 1 to confirm and 2 to reschedule.";
		System.out.println(msg.indexOf("[") + "-" + msg.indexOf("]"));
		System.out.println(msg.replaceAll("\\[First\\]", "Chandra"));
		//new TestSmsTempateImpl().addSmsTemplate();
		//new TestSmsTempateImpl().addSmsTemplate1();
		new TestSmsTempateImpl().addSmsTemplateAdmin();
		//new TestSmsTempateImpl().getSmsTemplate();
		//new TestSmsTempateImpl().getSmsTemplateForType();
		//new TestSmsTempateImpl().getSmsTemplateForTypeName();
	}

	public void addSmsTemplate() {
		SmsTemplateInfo request = new SmsTemplateInfo();

		//request.setPracticeName("Fortis");
		request.setSmsTemplateInfo(null,"default", SmsTemplateType.WAITLIST, 
				"[First] please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.SUPERUSER);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = SmsTemplateImpl.addTemplate(jsonReq);

		System.out.println(response);
	}
	
	public void addSmsTemplate1() {
		SmsTemplateInfo request = new SmsTemplateInfo();

		//request.setPracticeName("Fortis");
		request.setSmsTemplateInfo(null,"default_reminder", SmsTemplateType.REMINDER, 
				"<first> please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.SUPERUSER);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = SmsTemplateImpl.addTemplate(jsonReq);

		System.out.println(response);
	}
	
	public void addSmsTemplateAdmin() {
		SmsTemplateInfo request = new SmsTemplateInfo();

		request.setPracticeName("Fortis");
		request.setSmsTemplateInfo(null,"fortis_waitlist", SmsTemplateType.WAITLIST, 
				"<First> please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.ADMIN);
		request.setSmsTemplateInfo(null,"fortis_reminder", SmsTemplateType.REMINDER, 
				"<first> please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.ADMIN);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = SmsTemplateImpl.addTemplate(jsonReq);

		System.out.println(response);
	}
	
	public void addSmsTemplateAdminError() {
		SmsTemplateInfo request = new SmsTemplateInfo();

		request.setPracticeName("Fortis");
		request.setSmsTemplateInfo(null,"fortis_waitlist", SmsTemplateType.WAITLIST, 
				"<First> please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.SUPERUSER);
		request.setSmsTemplateInfo(null,"fortis_reminder", SmsTemplateType.REMINDER, 
				"<first> please confirm your availability for appt at <Time> in <Office>", 
				"Thank you for confirming appointment", 
				"Please get in touch with office for rescheduling appointment", 
				UserRole.SUPERUSER);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = SmsTemplateImpl.addTemplate(jsonReq);

		System.out.println(response);
	}
		
	public void getSmsTemplate() {
		String response = SmsTemplateImpl.getTemplate("Fortis", "", "");

		System.out.println(response);
	}
	
	public void getSmsTemplateForType() {
		String response = SmsTemplateImpl.getTemplate("Fortis", "REMINDER", "");

		System.out.println(response);
	}
	
	public void getSmsTemplateForTypeName() {
		String response = SmsTemplateImpl.getTemplate("Fortis", "WAITLIST", "default_new1");

		System.out.println(response);
	}
}
