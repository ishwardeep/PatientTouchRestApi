package com.patienttouch.api;

import java.util.Date;
import java.util.List;

import com.patienttouch.hibernate.Campaign;

public class CampaignAnalyzer implements Runnable {
	@Override
	public void run() {
		String query;
		List<Campaign> campaigns;
		
		try {
			System.out.println("Last run at [" + new Date(System.currentTimeMillis()) + "]");
			query = "from Campaign where type = 'REMINDER' and status = 'RUNNING'";
			campaigns = CampaignImpl.getCampaignInfo(null, query);
			if (campaigns == null) {
				System.out.println("No reminder campaign running");
			}
			else {
				for(Campaign c : campaigns) {	
					//ReminderCampaignAnalyzerTask task = new ReminderCampaignAnalyzerTask(c);
					//task.run();
					CampaignManager.getInstance().analyzeReminderCampaign(c);
				}
			}
			
			query = "from Campaign where type = 'WAITLIST' and status = 'RUNNING'";
			campaigns = CampaignImpl.getCampaignInfo(null, query);
			if (campaigns == null) {
				System.out.println("No waitlist campaign running");		
			}
			else {
				for(Campaign c : campaigns) {
					//WaitlistCampaignAnalyzerTask task = new WaitlistCampaignAnalyzerTask(c);
					//task.run();
					CampaignManager.getInstance().analyzeWaitlistCampaign(c);
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
				//CampaignInWaitStateAnalyzerTask task = new CampaignInWaitStateAnalyzerTask(c);
				//task.run();
				CampaignManager.getInstance().analyzeWaitlistCampaignInWaitState(c);
			}
			
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
