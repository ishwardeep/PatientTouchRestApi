package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.OfficeImpl;
import com.patienttouch.client.OfficeInfo;

public class TestOfficeImpl {
	public static void main(String args[]) {
		new TestOfficeImpl().addOffice();
		//new TestOfficeImpl().getOfficeById();
		//new TestOfficeImpl().updateOffice();
		//new TestOfficeImpl().deleteOffice("2");
		
		//new TestOfficeImpl().getOffice();
		//new TestOfficeImpl().getOffice();

	}

	public void addOffice() {
		OfficeInfo request = new OfficeInfo();

		request.setPracticeName("Fortis");
		request.setOfficeInfo(null,"Miami Office", "704", "5th Street", "Miami",
				"Miami", "FL", "33101", "8588241888", false);
		request.setOfficeInfo(null,"San Diego Office", "705", "5th Street", "San Diego", "San", "CA", "92121", "8581234567", false);

		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = OfficeImpl.addOffice(jsonReq);

		System.out.println(response);
	}
	
	public void updateOffice() {
		OfficeInfo request = new OfficeInfo();

		request.setPracticeName("Escorts");
		request.setOfficeInfo("2","NY Office", "701", "2nd Street", "New York",
				"New York", "NY", "33101", "8588241888", false);

		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = OfficeImpl.updateOffice(jsonReq);

		System.out.println(response);
	}
	
	public void deleteOffice(String officeid) {
		String response = OfficeImpl.deleteOffice(officeid);

		System.out.println(response);
	}

	public void getAllOffices() {
		String response = OfficeImpl.getOffice("Fortis","");

		System.out.println(response);
	}
	
	public void getOffice() {
		String response = OfficeImpl.getOffice("Fortis","NY Office");

		System.out.println(response);
	}
	
	public void getOfficeById() {
		String response = OfficeImpl.getOfficeById("2");

		System.out.println(response);
	}
	
	public void getOfficeNoResult() {
		String response = OfficeImpl.getOffice("Fortis","NY Offices");

		System.out.println(response);
	}
}
