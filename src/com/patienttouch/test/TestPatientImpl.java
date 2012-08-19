package com.patienttouch.test;

import com.patienttouch.api.PatientImpl;


public class TestPatientImpl {
	public static void main(String args[]) {
		new TestPatientImpl().addPatient();
		//new TestPatientImpl().getPatientByPhone();
	}
	
	public void addPatient() {
		String response = PatientImpl.addPatient("8582544277", "Chandra", "Kholia");
		response = PatientImpl.addPatient("8585319107", "Sameer", "Sondhi");

		System.out.println(response);
	}
	public void getPatientByPhone() {
		String response = PatientImpl.getByPhoneNumber("8585313499");

		System.out.println(response);
	}
	
	
}
