package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.UserImpl;
import com.patienttouch.client.UserInfo;
import com.patienttouch.hibernate.UserRole;

public class TestUserImpl {
	public static void main(String args[]) {
		//new TestUserImpl().addUser();
		new TestUserImpl().addUser1();
		//new TestUserImpl().updateUser();
	}
	
	public void addUser() {
		UserInfo request = new UserInfo();

		request.setPracticeName("Escorts");
		request.setUserInfo(null, "admin", "admin", "admin", "Ishwardeep", "Singh", UserRole.ADMIN);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = UserImpl.addUser(jsonReq);

		System.out.println(response);
	}
	
	public void addUser1() {
		UserInfo request = new UserInfo();

		request.setPracticeName("Escorts");
		request.setUserInfo(null, "admin", "admin", "admin", "Ishwardeep", "Singh", UserRole.ADMIN);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = UserImpl.addUser(jsonReq);

		System.out.println(response);
	}
	
	public void updateUser() {
		UserInfo request = new UserInfo();

		request.setPracticeName("Escorts");
		request.setUserInfo("5", "admin", "admin2", "admin2", "Ishwardeep", "Singh", UserRole.ADMIN);
		
		Gson gson = new Gson();
		String jsonReq = gson.toJson(request);
		System.out.println(jsonReq);
		String response = UserImpl.updateUser(jsonReq);

		System.out.println(response);
	}
}
