package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorInfo extends Request {
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
	
	public String getOfficeName(Map<String, Object> office) {
		return (String) office.get("name");
	}
	
	public void setOfficeName(String officeName) {
		List<Map<String,Object>> offices;
		Map<String, Object> office = new HashMap<String, Object>();
		
		offices = (ArrayList<Map<String, Object>>) this.request.get("offices");
		if (offices == null) {
			offices = new ArrayList<Map<String, Object>>();
			this.request.put("offices", offices);
		}
		offices.add(office);
		
		office.put("name", officeName);
	}
	
	public List<Map<String, Object>> getDoctors() {
		return (List<Map<String, Object>>) this.request.get("doctors");
	}
	
	public String getDoctorid(Map<String, Object> doctor) {
		return (String) doctor.get("doctorid");
	}
	
	public String getFirstName(Map<String, Object> doctor) {
		return (String) doctor.get("firstName");
	}
	
	public String getLastName(Map<String, Object> doctor) {
		return (String) doctor.get("lastName");
	}
	
	public String getNickName(Map<String, Object> doctor) {
		return (String) doctor.get("nickName");
	}
	public void setDoctorInfo(String doctorid, String firstName, String lastName, String nickName) {
		List<Map<String,Object>> doctors;
		Map<String, Object> doctor = new HashMap<String, Object>();
		
		doctors = (ArrayList<Map<String, Object>>) this.request.get("doctors");
		if (doctors == null) {
			doctors = new ArrayList<Map<String, Object>>();
			this.request.put("doctors", doctors);
		}
		doctors.add(doctor);
		
		if (doctorid != null && !doctorid.isEmpty()) {
			doctor.put("doctorid", doctorid);
		}
		doctor.put("firstName", firstName);
		doctor.put("lastName", lastName);
		doctor.put("nickName", nickName);
	}
}
