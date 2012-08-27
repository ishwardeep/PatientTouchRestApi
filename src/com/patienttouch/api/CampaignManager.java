package com.patienttouch.api;


import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.patienttouch.hibernate.Campaign;
import com.patienttouch.util.Utility;


public class CampaignManager {
	private static volatile CampaignManager _self;
	private static ScheduledExecutorService analyzer;
	private static final long scheduleDelayInMs = 60000;
	private static final int scheduleThreadPoolSize = 10;
	
	/**
	 * Private constructor to make this class singleton
	 */
	private CampaignManager() {		
	}
	
	public static CampaignManager getInstance() {
		if (CampaignManager._self == null) {
			synchronized(CampaignManager.class) {
				CampaignManager._self = new CampaignManager();
				_self.init();
			}
		}
		
		return CampaignManager._self;
	}
	
	/**
	 * This method is used to initialize the executor
	 * 
	 */
	private void init() {
		int threadPoolSize;
		long scheduledDelay;
		
		System.out.println("Initialize Campaign Manager");
		
		String tmpStr = Utility.getProperty(null, Utility.SCHEDULE_THREAD_POOL_SIZE);
		if (tmpStr != null && tmpStr.matches("\\d+")) {
			threadPoolSize = Integer.parseInt(tmpStr);
		}
		else {
			threadPoolSize = scheduleThreadPoolSize;
		}
		
		tmpStr = Utility.getProperty(null, Utility.CAMPAIGN_ANALYZER_SCHEDULE_TIME_IN_MS);
		if (tmpStr != null && tmpStr.matches("\\d+")) {
			scheduledDelay = Long.parseLong(tmpStr);
		}
		else {
			scheduledDelay = scheduleDelayInMs;
		}
		
		System.out.println("Campaign Manager : threadPoolSize [" + threadPoolSize + "] ScheduleTime [" + scheduledDelay + "] in ms");
		
		analyzer = Executors.newScheduledThreadPool(threadPoolSize);
		analyzer.scheduleWithFixedDelay(new CampaignAnalyzer(), 1000, scheduledDelay, TimeUnit.MILLISECONDS);
	}
	
	public void analyzeReminderCampaign(Campaign reminder) {
		ReminderCampaignAnalyzerTask task = new ReminderCampaignAnalyzerTask(reminder);
		analyzer.execute(task);
		System.out.println("Analyze Reminder Campa");
	}
	
	public void analyzeWaitlistCampaign(Campaign waitlist) {
		WaitlistCampaignAnalyzerTask task = new WaitlistCampaignAnalyzerTask(waitlist);
		analyzer.execute(task);
	}
	
	public void analyzeWaitlistCampaignInWaitState(Campaign waitlistInWaitState) {
		CampaignInWaitStateAnalyzerTask task = new CampaignInWaitStateAnalyzerTask(waitlistInWaitState);
		analyzer.execute(task);
	}
}
