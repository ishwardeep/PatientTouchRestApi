package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitlistInfo extends Request {
	public String getPractice() {
		return (String) this.request.get("practice");
	}
	
	public void setPractice(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}
	
	public String getOffice() {
		return (String) this.request.get("office");
	}
	
	public void setOffice(String officeName) {
		if (officeName == null || officeName.isEmpty()) {
			return;
		}
		this.request.put("office", officeName);
	}
	
	public String getDoctor() {
		return (String) this.request.get("doctor");
	}
	
	public void setDoctor(String doctorName) {
		if (doctorName == null || doctorName.isEmpty()) {
			return;
		}
		this.request.put("doctor", doctorName);
	}
	
	public void setWaitlistInfo(String waitlistid, String patientid, String phone, String firstName, String lastName) {
		List<Map<String,Object>>  waitlist;
		Map<String, Object> appointee = new HashMap<String, Object>();
		
		waitlist = (List<Map<String, Object>>) this.request.get("waitlist");
		if ( waitlist == null) {
			waitlist = new ArrayList<Map<String, Object>>();
			this.request.put("waitlist",  waitlist);
		}
		waitlist.add(appointee); 
		
		if (waitlistid != null && !waitlistid.isEmpty()) {
			appointee.put("waitlistid", waitlistid);
		}
		
		if (patientid != null && !patientid.isEmpty()) {
			appointee.put("appointeeid", patientid);
		}
		
		appointee.put("phone", phone);
		appointee.put("firstName", firstName);
		appointee.put("lastName", lastName);		
	}
	
	public List<Map<String,Object>> getWaitlist() {
		return (List<Map<String,Object>>)this.request.get("waitlist");
	}
	
	public String getWaitlistId(Map<String, Object> waitlist) {
		return (String)waitlist.get("waitlistid");
	}
	
	public String getAppointeeId(Map<String, Object> waitlist) {
		return (String)waitlist.get("appointeeid");
	}
	
	public String getAppointeeFirstName(Map<String, Object> waitlist) {
		return (String)waitlist.get("firstName");
	}
	
	public String getAppointeeLastName(Map<String, Object> waitlist) {
		return (String)waitlist.get("lastName");
	}
	
	public String getAppointeePhone(Map<String, Object> waitlist) {
		return (String)waitlist.get("phone");
	}
}
