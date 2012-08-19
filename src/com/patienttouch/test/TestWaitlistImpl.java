package com.patienttouch.test;

import com.patienttouch.api.WaitlistImpl;

public class TestWaitlistImpl {
	public static void main(String args[]) {
		//new TestWaitlistImpl().getWaitlist();
		//new TestWaitlistImpl().getWaitlistForOffice();
		new TestWaitlistImpl().getWaitlistForOfficeDoctor();
		//new TestWaitlistImpl().deleteWaitlist();
	}
	
	public void getWaitlist()
	{
		String response = WaitlistImpl.getWaitlist("Fortis", "","");
		System.out.println(response);
	}
	
	public void getWaitlistForOffice()
	{
		String response = WaitlistImpl.getWaitlist("Fortis", "4","");
		System.out.println(response);
	}
	
	public void getWaitlistForOfficeDoctor()
	{
		String response = WaitlistImpl.getWaitlist("Fortis", "4","1");
		System.out.println(response);
	}
	
	public void deleteWaitlist() {
		String response = WaitlistImpl.deleteFromWaitlist("5");
		System.out.println(response);
	}
}
