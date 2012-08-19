package com.patienttouch.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitlistCampaignInfo extends Request {
	public String getPractice() {
		return (String) this.request.get("practice");
	}
	
	public void setPractice(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}
	
	public String getTemplateId() {
		return (String) this.request.get("templateId");
	}
	
	public String getTemplateName() {
		return (String) this.request.get("templateName");
	}
	
	public String getInitialMessage() {
		return (String) this.request.get("initialMsg");
	}
	
	public String getConfirmMessage() {
		return (String) this.request.get("confirmMsg");
	}
	
	public String getRescheduleMessage() {
		return (String) this.request.get("rescheduleMsg");
	}
	
	public void setTemplate(int templateid, String templateName) {
		this.request.put("templateId", templateid);
		this.request.put("templateName", templateName);
	}
	
	public void setCustomTemplate(String initialMessage, String confirmMessage, String rescheduleMessage) {
		this.request.put("templateName", "custom");
		if (initialMessage != null && !initialMessage.isEmpty()) {
			this.request.put("initialMsg", initialMessage);
		}
		if (confirmMessage != null && !confirmMessage.isEmpty()) {
			this.request.put("confirmMsg", confirmMessage);
		}
		if (rescheduleMessage != null && !rescheduleMessage.isEmpty()) {
			this.request.put("rescheduleMsg", rescheduleMessage);
		}
	}
	
	public void setWaitlistAppointmentInfo(String appointmentDate, List<String> appointmentTime, String doctorid, 
			String officeid, String interMsgSpacing) {
		List<Map<String, Object>> availableAppointments;
		Map<String, Object> appointment = new HashMap<String, Object>();
		
		availableAppointments = (List<Map<String, Object>>) this.request.get("availableAppointment");
		if (availableAppointments == null) {
			availableAppointments = new ArrayList<Map<String, Object>>();
			this.request.put("availableAppointment",  availableAppointments);
		}
		
		availableAppointments.add(appointment);
		
		appointment.put("appointmentDate", appointmentDate);
		appointment.put("appointmentTime", appointmentTime);
		appointment.put("doctorid", doctorid);
		appointment.put("officeid", officeid);
		appointment.put("interMsgSpacing", interMsgSpacing);
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
	
	public List<Map<String,Object>> getAppointments() {
		return (List<Map<String,Object>>)this.request.get("availableAppointment");
	}
	
	public String getAppointmentDate(Map<String, Object> appointment) {
		return (String)appointment.get("appointmentDate");
	}
	
	public List<String> getAppointmentTime(Map<String, Object> appointment) {
		return (List<String>)appointment.get("appointmentTime");
	}
	
	public String getDoctorId(Map<String, Object> appointment) {
		return (String)appointment.get("doctorid");
	}
	
	public String getOfficeId(Map<String, Object> appointment) {
		return (String)appointment.get("officeid");
	}
	
	public String getInterMsgSpacing(Map<String, Object> appointment) {
		return (String)appointment.get("interMsgSpacing");
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
