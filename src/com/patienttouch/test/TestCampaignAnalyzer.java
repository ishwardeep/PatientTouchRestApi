package com.patienttouch.test;

import com.patienttouch.api.CampaignAnalyzer;
import com.patienttouch.api.CampaignManager;


public class TestCampaignAnalyzer {
	public static void main(String args[]) {
		CampaignManager mgr = CampaignManager.getInstance();
		//new TestCampaignAnalyzer().analyzeCampaign();
	}
	
	public void analyzeCampaign() {
		CampaignAnalyzer analyzer = new CampaignAnalyzer();
		analyzer.run();
	}
}
