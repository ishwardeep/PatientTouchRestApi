package com.patienttouch.api;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.CampaignStatus;
import com.patienttouch.hibernate.DbOperations;

import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.util.Utility;

public class ReminderCampaignAnalyzerTask implements Runnable {
	//private static final long defaultReminderResponseTimeoutInMs = 300000;

	private final Campaign campaign;

	public ReminderCampaignAnalyzerTask(Campaign campaign) {
		this.campaign = campaign;
	}

	@Override
	public void run() {
		boolean pendingAppointments = false;
		long diffInMs, reminderResposeTimeoutInMs;// = ReminderCampaignAnalyzerTask.defaultReminderResponseTimeoutInMs;
		//String tmpStr;
		String query;
		Date smsTimestamp;
		Session session = null;
		List<AppointmentInfo> appInfos;
		List<SmsMessage> smsMessages;
		List<Campaign> campaigns;
		AppointmentStatus status;

		try {
			query = "from Campaign where id = " + this.campaign.getCampaignid();

			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			campaigns = CampaignImpl.getCampaignInfo(session, query);
			if (campaigns == null) {
				System.out.println("No reminder campaign running");
				return;
			}

			for (Campaign c : campaigns) {
				reminderResposeTimeoutInMs = c.getAppointeeResponseTimeInMs();
				appInfos = c.getAppointmentInfo();
				for (AppointmentInfo appInfo : appInfos) {
					status = appInfo.getStatus();
					// TRYING,UNABLE_TO_SEND_MESSAGE, MESSAGE_SENT,
					// NOT_DELIVERED,INVALID_PHONE,DELIVERED,
					// ACCEPTED,CANCEL,RESCHEDULE,PROBLEM
					if (status == AppointmentStatus.MESSAGE_SENT
							|| status == AppointmentStatus.DELIVERED) {
						// waiting for response from the appointee
						smsMessages = appInfo.getSmsMessages();
						for (SmsMessage sms : smsMessages) {
							if (sms.getType() == SmsMessageType.INITIAL) {
								smsTimestamp = sms.getSmsTimestamp();
								diffInMs = Utility.diffInMs(
										new Date(System.currentTimeMillis()),
										smsTimestamp);
								System.out.println("Time difference in ms ["
										+ diffInMs + "]");
								if (diffInMs > reminderResposeTimeoutInMs) {
									appInfo.setStatus(AppointmentStatus.NO_RESPONSE);
									appInfo.setLastUpdateTime(new Date(System.currentTimeMillis()));
									session.update(appInfo);
								} else {
									pendingAppointments = true;
								}
								break;
							}
						}
					}
				}
				if (!pendingAppointments) {
					c.setStatus(CampaignStatus.COMPLETE);
					c.setLastUpdateTime(new Date(System.currentTimeMillis()));
					session.update(c);
				}
			}
			

			session.getTransaction().commit();
		} catch (Throwable t) {
			t.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		}
	}

}
