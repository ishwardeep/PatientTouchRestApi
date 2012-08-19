package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.PracticeImpl;
import com.patienttouch.client.PracticeInfo;

public class TestPracticeImpl {
	public static void main(String args[]) {
		new TestPracticeImpl().addPractice();
		new TestPracticeImpl().addPracticeEscorts();
		//new TestPracticeImpl().getAllPractice();
		//new TestPracticeImpl().getPracticeById();
		//new TestPracticeImpl().getPractice();
		//new TestPracticeImpl().updatePractice();
		//new TestPracticeImpl().deletePractice();
		
		
	}
	
	public void addPractice() {
		PracticeInfo request = new PracticeInfo();
		
		request.setPracticeName("Fortis");
		request.setContact("John", "Rambo", "john.rambo@yahoo.com", "8588241888");
 		request.setLoginInfo("john", "1234", "1234");
		request.setHeadOffice("Miami Office", "701", "2nd Street", "Miami", "Miami", "FL", "33101", "8588241888");
		request.setBillingInfo("701", "2nd Street", "Miami", "FL", "33101");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = PracticeImpl.addPractice(gson.toJson(request));
		
		System.out.println(response);
	}
	
	public void addPracticeEscorts() {
		PracticeInfo request = new PracticeInfo();
		
		request.setPracticeName("Escorts");
		request.setContact("Tarun", "Batra", "tjs@yahoo.com", "8588241444");
 		request.setLoginInfo("tarun", "1234", "1234");
		request.setHeadOffice("NY Office", "702", "3rd Street", "New York", "NY", "NY", "33102", "8588241444");
		request.setBillingInfo("702", "3rd Street", "New York", "NY", "33102");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = PracticeImpl.addPractice(gson.toJson(request));
		
		System.out.println(response);
	}
	
	public void updatePractice() {
		PracticeInfo request = new PracticeInfo();
		
		request.setPracticeName("Fortis");
		request.setContact("John", "Rambo", "john.rambo@yahoo.com", "8588241899");
 		request.setLoginInfo("john", "1234", "1234");
		request.setHeadOffice("Miami Office", "701", "2nd Street", "Miami", "Miami", "FL", "33101", "8588241888");
		request.setBillingInfo("701", "2nd Street", "Miami", "FL", "33101");
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = PracticeImpl.updatePractice(gson.toJson(request));
		
		System.out.println(response);
	}
	
	public void deletePractice() {
		String response = PracticeImpl.deletePractice("1");
		
		System.out.println(response);
	}
	public void getPractice() {
		String response = PracticeImpl.getPracticeByName("Fortis");
		
		System.out.println(response);	
	}
	
	public void getPracticeById() {
		String response = PracticeImpl.getPracticeById("13");
		
		System.out.println(response);	
	}
	
	public void getAllPractice() {
		String response = PracticeImpl.getAllPractice();
		
		System.out.println(response);
	}
}
