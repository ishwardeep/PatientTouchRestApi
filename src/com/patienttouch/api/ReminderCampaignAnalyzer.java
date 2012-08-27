package com.patienttouch.api;

import java.util.List;
import com.patienttouch.hibernate.Campaign;

public class ReminderCampaignAnalyzer implements Runnable {
	
	@Override
	public void run() {
		String query = "from Campaign where type = 'REMINDER' and status = 'RUNNING'";
		List<Campaign> campaigns;
		
		try {	
			campaigns = CampaignImpl.getCampaignInfo(null, query);
			if (campaigns == null) {
				System.out.println("No reminder campaign running");
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
