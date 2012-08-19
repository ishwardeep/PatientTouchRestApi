package com.patienttouch.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.patienttouch.api.WaitlistCampaignImpl;
import com.patienttouch.api.WaitlistImpl;
import com.patienttouch.client.WaitlistCampaignInfo;

public class TestWaitlistCampaignImpl {
	public static void main(String args[]) {
		new TestWaitlistCampaignImpl().startWaitlistCampaign();
		//new TestWaitlistCampaignImpl().getCampaignStatus();
		//new TestWaitlistCampaignImpl().getCampaignDetail();
		//new TestWaitlistCampaignImpl().getAppointmentDetail();
	}
	
	public void startWaitlistCampaign() {
		String response;
		WaitlistCampaignInfo waitlist = new WaitlistCampaignInfo();
		List<String> appointmentTime = new ArrayList<String>();
		appointmentTime.add("9:30 am");
		appointmentTime.add("10:20 am");
		
		waitlist.setPractice("Fortis");
		waitlist.setCustomTemplate("[First] has an appt with Dr [Doctor] on [Date] [Time] at our [Office] office. Reply with 1 to confirm and 2 to reschedule.", 
				"Thank you for confirming your appointment. Please call our office for any changes [Office TN]", 
				"We're sorry you are unable to make your appointment. Please call our office to reschedule [Office TN]");
		waitlist.setWaitlistInfo(null, "1", "8582544277", "Chandra", "Kholia");
		waitlist.setWaitlistInfo(null, "8", "8582541556", "Ekpal", "Singh");
		
		waitlist.setWaitlistAppointmentInfo("15/08/2012", appointmentTime, "1", "4", "5");
		
		Gson gson = new Gson();
		String jsonRequest = gson.toJson(waitlist);
		System.out.println(jsonRequest);
		
		response = WaitlistCampaignImpl.setupWaitlistCampaign(jsonRequest);
		
		System.out.println(response);	
	}
	
	public void getCampaignStatus() {
		String response = WaitlistCampaignImpl.getWaitlistStatus("Fortis");
		System.out.println(response);
	}
	
	public void getCampaignDetail() {
		String response = WaitlistCampaignImpl.getWaitlistAppointmentInfoStatus("38");
		System.out.println(response);
	}
	
	public void getAppointmentDetail() {
		String response = WaitlistCampaignImpl.getWaitlistAppointmentDetailStatus("22");
		System.out.println(response);
	}
	
}
