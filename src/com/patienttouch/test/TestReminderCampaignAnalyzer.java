package com.patienttouch.test;

import com.patienttouch.api.ReminderCampaignAnalyzer;

public class TestReminderCampaignAnalyzer {
	public static void main(String args[]) {
		new TestReminderCampaignAnalyzer().analyzeCampaign();
	}
	
	public void analyzeCampaign() {
		ReminderCampaignAnalyzer analyzer = new ReminderCampaignAnalyzer();
		analyzer.run();
	}
}
