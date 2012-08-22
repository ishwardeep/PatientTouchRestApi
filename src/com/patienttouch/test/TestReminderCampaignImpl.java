package com.patienttouch.test;

import com.google.gson.Gson;
import com.patienttouch.api.ReminderCampaignImpl;
import com.patienttouch.client.ReminderCampaignInfo;

public class TestReminderCampaignImpl {
	public static void main(String args[]) {
		new TestReminderCampaignImpl().startReminderCampaign();
	}
	
	public void startReminderCampaign() {
		ReminderCampaignInfo reminder = new ReminderCampaignInfo();
		
		reminder.setPractice("Fortis");
		reminder.setCustomTemplate("Hi", "confirmed", "reschduled");
		reminder.setAppointmentInfo("14", "8582541888", "Chandra", "Kholia", "11/08/2012", "9:30 am","1", "4");
		reminder.setAppointmentInfo("15", "8582541556", "Ekpal", "Singh", "11/08/2012", "10:00 am", "2", "1");
		
		Gson gson = new Gson();
		String jsonrequest = gson.toJson(reminder);
		
		System.out.println(jsonrequest);
		String response = ReminderCampaignImpl.setupReminderCampaign(jsonrequest);
		
		System.out.println(response);
	
	}
}
