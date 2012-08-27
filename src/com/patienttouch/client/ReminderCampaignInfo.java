package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderCampaignInfo extends Request {
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
		if (templateid != 0) {
			this.request.put("templateId", templateid);
		}
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
	
	public void setAppointmentInfo(String patientid, String phone, String firstName, String lastName, 
			String appointmentDate, String appointmentTime, String doctorid, String officeid) {
		List<Map<String,Object>>  appointments;
		Map<String, Object> appointment = new HashMap<String, Object>();
		
		 appointments = (ArrayList<Map<String, Object>>) this.request.get("appointments");
		if ( appointments == null) {
			 appointments = new ArrayList<Map<String, Object>>();
			this.request.put("appointments",  appointments);
		}
		 appointments.add(appointment);
		 
		
		if (patientid != null && !patientid.isEmpty()) {
			appointment.put("appointeeid", patientid);
		}
		appointment.put("phone", phone);
		appointment.put("firstName", firstName);
		appointment.put("lastName", lastName);
		appointment.put("appointmentDate", appointmentDate);
		appointment.put("appointmentTime", appointmentTime);
		appointment.put("doctorid", doctorid);
		appointment.put("officeid", officeid);
	}
	
	public List<Map<String,Object>> getAppointments() {
		return (List<Map<String,Object>>)this.request.get("appointments");
	}
	public String getAppointeeId(Map<String, Object> appointment) {
		return (String)appointment.get("appointeeid");
	}
	
	public String getAppointeeFirstName(Map<String, Object> appointment) {
		return (String)appointment.get("firstName");
	}
	
	public String getAppointeeLastName(Map<String, Object> appointment) {
		return (String)appointment.get("lastName");
	}
	
	public String getAppointeePhone(Map<String, Object> appointment) {
		return (String)appointment.get("phone");
	}
	
	public String getAppointmentDate(Map<String, Object> appointment) {
		return (String)appointment.get("appointmentDate");
	}
	
	public String getAppointmentTime(Map<String, Object> appointment) {
		return (String)appointment.get("appointmentTime");
	}
	
	public String getDoctorId(Map<String, Object> appointment) {
		return (String)appointment.get("doctorid");
	}
	
	public String getOfficeId(Map<String, Object> appointment) {
		return (String)appointment.get("officeid");
	}
}
