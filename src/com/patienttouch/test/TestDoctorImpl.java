package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.DoctorImpl;
import com.patienttouch.client.DoctorInfo;


public class TestDoctorImpl {
	public static void main(String args[]) {
		//new TestDoctorImpl().addDoctor();
		//new TestDoctorImpl().addDoctor1();
		//new TestDoctorImpl().addDoctor2();
		//new TestDoctorImpl().updateDoctor();
		//new TestDoctorImpl().getAllDoctorsForPractice();
		new TestDoctorImpl().getDoctorsForPracticeOffice();
		//new TestDoctorImpl().getDetailsForDoctorOffice();
		//new TestDoctorImpl().getOfficeForDoctor();
		//new TestDoctorImpl().getDoctorById();
		//new TestDoctorImpl().deleteDoctor();
	}

	public void updateDoctor() {
		DoctorInfo request = new DoctorInfo();

		request.setPracticeName("Fortis");
		request.setOfficeName("Miami Office");
		request.setOfficeName("San Diego Office");
		request.setDoctorInfo("3","Tarun", "Batra", "Tjs");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = DoctorImpl.updateDoctor(jsonReq);

		System.out.println(response);
	}
	
	public void addDoctor() {
		DoctorInfo request = new DoctorInfo();

		request.setPracticeName("Escorts");
		request.setOfficeName("NY Office");
		//request.setOfficeName("San Diego Office");
		request.setDoctorInfo(null,"Tarun", "Batra", "Tjs");
		request.setDoctorInfo(null,"Vimpy", "Batra", "Vimpy");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		//String response = DoctorImpl.addDoctor(jsonReq);

		//System.out.println(response);
	}

	public void addDoctor1() {
		DoctorInfo request = new DoctorInfo();

		request.setPracticeName("Fortis");
		request.setOfficeName("Miami Office");
		request.setDoctorInfo(null, "Vimpy", "Batra", "Vimpy");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = DoctorImpl.addDoctor(jsonReq);

		System.out.println(response);
	}
	
	public void addDoctor2() {
		DoctorInfo request = new DoctorInfo();

		request.setPracticeName("Fortis");
		request.setOfficeName("San Diego Office");
		request.setDoctorInfo(null, "Vimpy", "Batra", "Vimpy");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = DoctorImpl.addDoctor(jsonReq);

		System.out.println(response);
	}
	
	public void getAllDoctorsForPractice() {
		String response = DoctorImpl.getDoctor("Fortis","","","","");

		System.out.println(response);
	}
	
	public void getDoctorsForPracticeOffice() {
		String response = DoctorImpl.getDoctor("Fortis","Miami Office~San Diego Office","","","");

		System.out.println(response);
	}
	
	public void getDetailsForDoctorOffice() {
		String response = DoctorImpl.getDoctor("Fortis","San Diego Office","Tjs","","Batra");

		System.out.println(response);
	}
	
	public void getOfficeForDoctor() {
		String response = DoctorImpl.getDoctor("Fortis","","Tjs","","Batra");

		System.out.println(response);
	}
	
	public void getDoctorById() {
		String response = DoctorImpl.getDoctorById("3");

		System.out.println(response);
	}
	
	public void deleteDoctor() {
		String response = DoctorImpl.deleteDoctor("3");

		System.out.println(response);
	}
}
