package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientInfo extends Request {
	public List<Map<String,Object>> getPatients() {
		return (List<Map<String,Object>>) this.request.get("patients");
	}
	
	public String getPatientid(Map<String, Object> patient) {
		return (String) patient.get("patientid");
	}
	
	public String getPatientFirstName(Map<String, Object> patient) {
		return (String) patient.get("firstName");
	}
	
	public String getPatientLastName(Map<String, Object> patient) {
		return (String) patient.get("lastName");
	}
	
	public String getPatientPhone(Map<String, Object> patient) {
		return (String) patient.get("phoneNumber");
	}
	
	public void setPatientInfo(String patientid, String firstName, String lastName, String phoneNumber) {
		List<Map<String,Object>> patients;
		Map<String, Object> patient = new HashMap<String, Object>();
		
		patients = (ArrayList<Map<String, Object>>) this.request.get("patients");
		if (patients == null) {
			patients = new ArrayList<Map<String, Object>>();
			this.request.put("patients", patients);
		}
		patients.add(patient);
		
		if (patientid != null && !patientid.isEmpty()) {
			patient.put("patientid", patientid);
		}
		patient.put("firstName", firstName);
		patient.put("lastName", lastName);
		patient.put("phoneNumber", phoneNumber);
	}
}
