package com.patienttouch.test;

import com.patienttouch.api.LogIn;

public class TestLogin {
	public static void main(String args[]) {
		LogIn l = new LogIn();
		
		String response = l.login("", "");
		
		System.out.println(response);
		
		response = l.login("web","");
		
		System.out.println(response);
		
		response = l.login("web", "web");
		System.out.println(response);
		
		response = l.login("su", "admin");
		System.out.println(response);
	}
}
