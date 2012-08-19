package com.patienttouch.client;

import java.util.HashMap;
import java.util.Map;

public class PracticeInfo extends Request {
	
	public String getPracticeId() {
		return (String)this.request.get("practiceid");
	}
	
	public void setPracticeId(String practiceid) {
		if (practiceid == null || practiceid.isEmpty()) {
			return;
		}
		this.request.put("practiceid", practiceid);
	}
	
	public String getPracticeName() {
		return (String)this.request.get("name");
	}
	
	public void setPracticeName(String name) {
		if (name == null || name.isEmpty()) {
			return;
		}
		this.request.put("name", name);
	}
	
	public Map<String, Object> getLoginInfo() {
		return (Map<String, Object>) this.request.get("login");
	}
	
	public String getLoginUserName(Map<String, Object> loginInfo) {	
		return (String) loginInfo.get("login");
	}
	
	public String getLoginPassword(Map<String, Object> loginInfo) {	
		return (String) loginInfo.get("password");
	}
	
	public String getLoginConfirmPassword(Map<String, Object> loginInfo) {	
		return (String) loginInfo.get("confirmPassword");
	}
	
	public void setLoginInfo(String username, String password, String confirmPassword) {
		Map<String, Object> m = new HashMap<String, Object>();
		this.request.put("login", m);
		m.put("login", username);
		m.put("password", password);
		m.put("confirmPassword", confirmPassword);
	}
	
	public Map<String, Object> getContact() {
		return (Map<String, Object>) this.request.get("contact");
	}
	
	public String getContactFirstName(Map<String, Object> contact) {
		return (String) contact.get("firstName");
	}
	
	public String getContactLastName(Map<String, Object> contact) {
		return (String) contact.get("lastName");
	}
	
	public String getContactEmail(Map<String, Object> contact) {
		return (String) contact.get("email");
	}
	
	public String getContactPhone(Map<String, Object> contact) {
		return (String) contact.get("phone");
	}
	
	public void setContact(String firstName, String lastName, String email, String phone) {
		Map<String, Object> m = new HashMap<String, Object>();
		this.request.put("contact", m);
		m.put("firstName", firstName);
		m.put("lastName", lastName);
		m.put("email", email);
		m.put("phone", phone);
	}
	
	public Map<String, Object> getHeadOffice() {
		return (Map<String, Object>) this.request.get("headOffice");
	}
	
	public String getHeadOfficeName(Map<String, Object> ho) {
		return (String) ho.get("name");
	}
	
	public String getHeadOfficeStreetAddress1(Map<String, Object> ho) {
		return (String) ho.get("streetAddress1");
	}
	
	public String getHeadOfficeStreetAddress2(Map<String, Object> ho) {
		return (String) ho.get("streetAddress2");
	}
	
	public String getHeadOfficeCity(Map<String, Object> ho) {
		return (String) ho.get("city");
	}
	
	public String getHeadOfficeCityShort(Map<String, Object> ho) {
		return (String) ho.get("cityShort");
	}
	
	public String getHeadOfficeSate(Map<String, Object> ho) {
		return (String) ho.get("state");
	}
	
	public String getHeadOfficeZip(Map<String, Object> ho) {
		return (String) ho.get("zip");
	}
	
	public String getHeadOfficePhone(Map<String, Object> ho) {
		return (String) ho.get("phone");
	}
	
	public void setHeadOffice(String name, String streetAddress1, String streetAddress2, String city, String cityShort,
			                  String state, String zip, String phone) {
		Map<String, Object> m = new HashMap<String, Object>();
		this.request.put("headOffice", m);
		m.put("name", name);
		m.put("streetAddress1", streetAddress1);
		m.put("streetAddress2", streetAddress2);
		m.put("city", city);
		m.put("cityShort", cityShort);
		m.put("state", state);
		m.put("zip", zip);
		m.put("phone", phone);
	}
	
	public Map<String, Object> getBillingInfo() {
		return (Map<String, Object>) this.request.get("billing");
	}
	
	public String getBillingStreetAddress1(Map<String, Object> billing) {
		return (String) billing.get("streetAddress1");
	}
	
	public String getBillingStreetAddress2(Map<String, Object> billing) {
		return (String) billing.get("streetAddress2");
	}
	
	public String getBillingCity(Map<String, Object> billing) {
		return (String) billing.get("city");
	}
	
	public String getBillingSate(Map<String, Object> billing) {
		return (String) billing.get("state");
	}
	
	public String getBillingZip(Map<String, Object> billing) {
		return (String) billing.get("zip");
	}
	
	public void setBillingInfo(String streetAddress1, String streetAddress2, String city,
            String state, String zip) {
		Map<String, Object> m = new HashMap<String, Object>();
		this.request.put("billing", m);
		m.put("streetAddress1", streetAddress1);
		m.put("streetAddress2", streetAddress2);
		m.put("city", city);
		m.put("state", state);
		m.put("zip", zip);
	}
}
