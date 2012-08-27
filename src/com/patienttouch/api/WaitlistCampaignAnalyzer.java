package com.patienttouch.api;

import java.util.List;
import com.patienttouch.hibernate.Campaign;

public class WaitlistCampaignAnalyzer implements Runnable {
	
	@Override
	public void run() {
		String query = "from Campaign where type = 'WAITLIST' and status = 'RUNNING'";
		List<Campaign> campaigns;
		
		try {	
			campaigns = CampaignImpl.getCampaignInfo(null, query);
			if (campaigns == null) {
				System.out.println("No waitlist campaign running");		
			}
			else {
				for(Campaign c : campaigns) {
					ReminderCampaignAnalyzerTask task = new ReminderCampaignAnalyzerTask(c);
					task.run();
				}
			}
			
			//Identify Campaigns in a wait state
			query = "from Campaign where type = 'WAITLIST' and status = 'WAIT'";
			campaigns = CampaignImpl.getCampaignInfo(null, query);
			if (campaigns == null) {
				System.out.println("No waitlist campaign in WAIT state");
				return;
			}
			
			for(Campaign c : campaigns) {
				ReminderCampaignAnalyzerTask task = new ReminderCampaignAnalyzerTask(c);
				task.run();
			}
			
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}