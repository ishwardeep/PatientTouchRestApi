package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.CampaignImpl;
import com.patienttouch.api.ReminderCampaignImpl;
import com.patienttouch.client.ReminderCampaignInfo;

public class TestReminderCampaignImpl {
	public static void main(String args[]) {
		//new TestReminderCampaignImpl().startReminderCampaign();
		//new TestReminderCampaignImpl().startReminderCampaign1();
		new TestReminderCampaignImpl().getReminderStatus();
		//new TestReminderCampaignImpl().getReminderStatus2();
		//new TestReminderCampaignImpl().getAppointmentDetail();
	}
	
	public void startReminderCampaign() {
		ReminderCampaignInfo reminder = new ReminderCampaignInfo();
		
		reminder.setPractice("Fortis");
		reminder.setCustomTemplate("[First] has an appt with Dr [Doctor] on [Date] [Time] at our [Office] office. Reply with 1 to confirm and 2 to reschedule.", 
				"Thank you for confirming your appointment. Please call our office for any changes [Office TN]", 
				"We're sorry you are unable to make your appointment. Please call our office to reschedule [Office TN]");
		reminder.setAppointmentInfo("14", "8582541888", "Chandra", "Kholia", "2012-08-20", "9:30 am","1", "4");
		reminder.setAppointmentInfo("15", "8582541556", "Ekpal", "Singh", "2012-08-20", "10:00 am", "2", "1");
		
		Gson gson = new Gson();
		String jsonrequest = gson.toJson(reminder);
		
		System.out.println(jsonrequest);
		String response = ReminderCampaignImpl.setupReminderCampaign(jsonrequest);
		
		System.out.println(response);
	
	}
	
	public void startReminderCampaign1() {
		ReminderCampaignInfo reminder = new ReminderCampaignInfo();
		
		reminder.setPractice("Fortis");
		reminder.setTemplate(0, "fortis_reminder");
		reminder.setAppointmentInfo("14", "8582541888", "Chandra", "Kholia", "2012-08-20", "9:30 am","1", "4");
		reminder.setAppointmentInfo("15", "8582541556", "Ekpal", "Singh", "2012-08-20", "10:00 am", "2", "1");
		
		Gson gson = new Gson();
		String jsonrequest = gson.toJson(reminder);
		
		System.out.println(jsonrequest);
		String response = ReminderCampaignImpl.setupReminderCampaign(jsonrequest);
		
		System.out.println(response);
	
	}
	
	public void getReminderStatus() {
		
		String response = ReminderCampaignImpl.getReminderStatus("Fortis", "8", "2012-08-20","");
		System.out.println(response);
	}
	
	public void getReminderStatus2() {
		
		String response = ReminderCampaignImpl.getReminderStatus("Fortis", "8", "Today","");
		System.out.println(response);
	}
	
	public void getAppointmentDetail() {
		String response = CampaignImpl.getCampaignAppointmentDetailStatus("3");
		System.out.println(response);
	}
}
