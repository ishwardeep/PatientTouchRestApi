package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patienttouch.hibernate.SmsTemplateType;
import com.patienttouch.hibernate.UserRole;

public class SmsTemplateInfo extends Request {
	
	public String getPracticeName() {
		return (String) this.request.get("practice");
	}
	
	public void setPracticeName(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}
	
	public List<Map<String, Object>> getSmsTemplates() {
		return (List<Map<String, Object>>) this.request.get("templates");
	}
	
	public String getTemplateId(Map<String, Object> template) {
		return (String) template.get("templateid");
	}
	
	public String getTemplateName(Map<String, Object> template) {
		return (String) template.get("name");
	}
	
	public SmsTemplateType getTemplateType(Map<String, Object> template) {
		String type = (String) template.get("type");
		if (type == null) {
			return null;
		}
		SmsTemplateType templateType = null;
		try {
			templateType = SmsTemplateType.valueOf(type);
		}
		catch (Throwable t) {
			System.out.println("Invalid template type [" + type + "]");
			t.printStackTrace();
		}
		return templateType;
	}
	
	public String getTemplateMessage(Map<String, Object> template) {
		return (String) template.get("message");
	}
	
	public String getTemplateConfirmMessage(Map<String, Object> template) {
		return (String) template.get("confirmMessage");
	}
	
	public String getTemplateReschduleMessage(Map<String, Object> template) {
		return (String) template.get("rescheduleMessage");
	}
	
	public UserRole getTemplateRole(Map<String, Object> template) {
		String role = (String) template.get("role");
		if (role == null) {
			return null;
		}
		
		if (role.equalsIgnoreCase("SUPERUSER")) {
			return UserRole.SUPERUSER;
		}
		else if (role.equalsIgnoreCase("ADMIN")) {
			return UserRole.ADMIN;
		}
		else if (role.equalsIgnoreCase("WEBUSER")) {
			return UserRole.WEBUSER;
		}
		
		return null;
	}
	
	public void setSmsTemplateInfo(String templateid, String name, SmsTemplateType type, String message, String confirmMessage, 
			String rescheduleMessage, UserRole role) {
		List<Map<String,Object>> tempaltes;
		Map<String, Object> template = new HashMap<String, Object>();
		
		tempaltes = (ArrayList<Map<String, Object>>) this.request.get("templates");
		if (tempaltes == null) {
			tempaltes = new ArrayList<Map<String, Object>>();
			this.request.put("templates", tempaltes);
		}
		tempaltes.add(template);
		
		if (templateid != null && !templateid.isEmpty()) {
			template.put("templateid", templateid);
		}
		template.put("name", name);
		template.put("type", type);
		template.put("message", message);
		template.put("confirmMessage", confirmMessage);
		template.put("rescheduleMessage", rescheduleMessage);
		template.put("role", role);
	}
}
