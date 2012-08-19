package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficeInfo extends Request{
	public String getPracticeName() {
		return (String) this.request.get("practice");
	}
	
	public void setPracticeName(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}
	
	public List<Map<String, Object>> getOffices() {
		return (List<Map<String, Object>>) this.request.get("offices");
	}
	
	public String getOfficeId(Map<String, Object> o) {
		return (String) o.get("officeid");
	}
	
	public String getOfficeName(Map<String, Object> o) {
		return (String) o.get("name");
	}
	
	public String getOfficeStreetAddress1(Map<String, Object> o) {
		return (String) o.get("streetAddress1");
	}
	
	public String getOfficeStreetAddress2(Map<String, Object> o) {
		return (String) o.get("streetAddress2");
	}
	
	public String getOfficeCity(Map<String, Object> o) {
		return (String) o.get("city");
	}
	
	public String getOfficeCityShort(Map<String, Object> o) {
		return (String) o.get("cityShort");
	}
	
	public String getOfficeSate(Map<String, Object> o) {
		return (String) o.get("state");
	}
	
	public String getOfficeZip(Map<String, Object> o) {
		return (String) o.get("zip");
	}
	
	public String getOfficePhone(Map<String, Object> o) {
		return (String) o.get("phone");
	}
	
	public String isMainOffice(Map<String, Object> o) {
		return (String) o.get("headoffice");
	}
	
	public void setOfficeInfo(String officeid, String officeName, String streetAddress1, String streetAddress2, String city, String cityShort,
            String state, String zip, String phone, boolean headOffice) {
		List<Map<String,Object>> offices;
		Map<String, Object> office = new HashMap<String, Object>();
		offices = (ArrayList<Map<String, Object>>) this.request.get("offices");
		if (offices == null) {
			offices = new ArrayList<Map<String, Object>>();
			this.request.put("offices", offices);
		}
		offices.add(office);
		if (officeid != null && !officeid.isEmpty()) {
			office.put("officeid", officeid);
		}
		office.put("name", officeName);
		office.put("streetAddress1", streetAddress1);
		office.put("streetAddress2", streetAddress2);
		office.put("city", city);
		office.put("cityShort", cityShort);
		office.put("state", state);
		office.put("zip", zip);
		office.put("phone", phone);
		office.put("headOffice",headOffice);
	}
}
