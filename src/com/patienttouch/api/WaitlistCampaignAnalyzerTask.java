package com.patienttouch.api;

import java.util.ArrayList;
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
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.util.Utility;

public class WaitlistCampaignAnalyzerTask implements Runnable {
	private final Campaign campaign;

	public WaitlistCampaignAnalyzerTask(Campaign campaign) {
		this.campaign = campaign;
	}

	@Override
	public void run() {
		boolean pendingAppointments = false, sendMsgToNextAppointee = false;
		long diffInMs, waitlistResposeTimeoutInMs;
		String query;
		Date smsTimestamp;
		Session session = null;
		List<AppointmentInfo> appInfos;
		List<SmsMessage> smsMessages, smsToBeSent = null;
		List<Campaign> campaigns;
		AppointmentStatus status;

		try {
			query = "from Campaign where id = " + this.campaign.getCampaignid();

			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			
			campaigns = CampaignImpl.getCampaignInfo(session, query);
			if (campaigns == null) {
				System.out.println("No reminder campaign running for campaign id [" + this.campaign.getCampaignid() + "]");
				return;
			}

			for (Campaign c : campaigns) {
				waitlistResposeTimeoutInMs = c.getAppointeeResponseTimeInMs();
				appInfos = c.getAppointmentInfo();
				for (AppointmentInfo appInfo : appInfos) {
					status = appInfo.getStatus();
					//WAIT,TRYING,UNABLE_TO_SEND_MESSAGE, MESSAGE_SENT, 
					//NOT_DELIVERED,INVALID_PHONE,DELIVERED,ACCEPTED,CANCEL,
					//RESCHEDULE,PROBLEM,NO_RESPONSE
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
								if (diffInMs > waitlistResposeTimeoutInMs) {
									appInfo.setStatus(AppointmentStatus.NO_RESPONSE);
									session.update(appInfo);
									sendMsgToNextAppointee = true;		
								} else {
									pendingAppointments = true;
								}
								break;
							}
						}
					}
					else if (status == AppointmentStatus.WAIT ||
							 status == AppointmentStatus.TRYING) 
					{
						pendingAppointments = true;
					}
				}
				
				if (sendMsgToNextAppointee) {
					if (smsToBeSent == null) {
						smsToBeSent = new ArrayList<SmsMessage>();
					}
					CampaignImpl.sendSmsToAppointeeInWaitState(session, c, smsToBeSent);
				}
				
				if (!pendingAppointments) {
					c.setStatus(CampaignStatus.COMPLETE);
					c.setLastUpdateTime(new Date(System.currentTimeMillis()));
					session.update(c);
				}
			}
			
			session.getTransaction().commit();
			if (sendMsgToNextAppointee) {
				for (SmsMessage sms : smsToBeSent) {
					SmsMsgRouter.getInstance().sendMessage(sms);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		}
	}

}