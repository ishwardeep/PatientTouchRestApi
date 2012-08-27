package com.patienttouch.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.patienttouch.client.ErrorCodes;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.CampaignStatus;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.hibernate.Waitlist;
import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.util.Utility;


public class CampaignInWaitStateAnalyzerTask implements Runnable {
	private final Campaign campaign;

	public CampaignInWaitStateAnalyzerTask(Campaign campaign) {
		this.campaign = campaign;
	}

	@Override
	public void run() {
		int ret;
		String query;
		String dependentCampaign;
		String dependentCampaignIds[];
		Session session = null;
		List<SmsMessage> smsToBeSent = new ArrayList<SmsMessage>();
		List<Campaign> campaigns, dependentCampaigns;
		List<AppointmentInfo> appInfo;
		Campaign dCampaign;
		
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
				dependentCampaign = c.getDependentCampaignId();
				//Just fetch appointmentinfo to avoid lazy initialization error
				//appInfo = c.getAppointmentInfo();
				
				dependentCampaignIds = dependentCampaign.split(",");
				for (String cid : dependentCampaignIds) {
					query = "from Campaign where id = " + cid;
					
					dependentCampaigns = CampaignImpl.getCampaignInfo(session, query);
					if (dependentCampaigns == null) {
						System.out.println("No dependent campaign running for campaign id [" + cid + "]");
						continue;
					}

					//Each campaign has a unique campaign 
					dCampaign = dependentCampaigns.get(0);
					if (dCampaign.getStatus() == CampaignStatus.RUNNING) {
						System.out.println("Parent campaign id [" + cid + 
								"] is still running. Can not start WAITING campaignid [" +
								cid + "]");
						continue;
					}
					
					analyzeDependentCampaign(session, dCampaign, c);
					
					ret = scheduleCampaign(session, c, smsToBeSent);
					if (ret != ErrorCodes.SUCCESS) {
						if (session != null && session.getTransaction().isActive()) {
							session.getTransaction().rollback();
						}
					}
				}
			}
			
			session.getTransaction().commit();
			
			for (SmsMessage sms : smsToBeSent) {
				SmsMsgRouter.getInstance().sendMessage(sms);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		}
	}
	
	/**
	 * This method is used to schedule a campaign
	 * 
	 * @param session
	 * @param waitlistCampaign
	 */
	private int scheduleCampaign(Session session, Campaign waitlistCampaign, List<SmsMessage> smsToBeSent) {
		int ret;
		String smsMessageText;
		SmsMessage message;
		List<AppointmentInfo> appointmentInfos;
		List<SmsMessage> msgList;
		waitlistCampaign.setStatus(CampaignStatus.RUNNING);	
		
		appointmentInfos = waitlistCampaign.getAppointmentInfo();
		
		Collections.sort(appointmentInfos);
		
		for (AppointmentInfo appInfo : appointmentInfos) {
			if (appInfo.getStatus() != AppointmentStatus.WAIT) {
				continue;
			}
			
			appInfo.setStatus(AppointmentStatus.TRYING);
				
			//Format SMS
			smsMessageText = SmsFormater.formatSms(waitlistCampaign.getMessage(), waitlistCampaign.getPractice().getName() , 
						Utility.getDateInyyyyMMdd(appInfo.getAppointmentDate()) ,appInfo.getAppointmentTime(), 
						appInfo.getAppointee(), appInfo.getOffice(), appInfo.getDoctor());
				
			message = new SmsMessage();
			message.setMessageText(smsMessageText);
			message.setAppointmentInfo(appInfo);
			message.setPhoneNumber(appInfo.getAppointee().getPhone());
			message.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
			message.setType(SmsMessageType.INITIAL);
				
			msgList = new ArrayList<SmsMessage>();
			msgList.add(message);
			
			appInfo.setSmsMessages(msgList);
					
			smsToBeSent.add(message);
				
			if (waitlistCampaign.getInterMessageSpacingInMs() > 0) {
				break;
			}
		}
			
		ret = DbOperations.updateDbWithConditionalTransaction(session, waitlistCampaign);
		
		return ret;
	}
	
	/**
	 * This method is used to analyze the wait list campaign that has successfully completed. Appointees
	 * who have already accepted an Appointment should be removed from the waitlist. The priority for those who have
	 * not responded should have lower priority when the next campaign is scheduled.
	 *  
	 * @param session
	 * @param dependentCampaign
	 * @param waitingCampaign
	 */
	
	private void analyzeDependentCampaign(Session session, Campaign dependentCampaign, Campaign waitingCampaign) {
		String query, key;
		AppointmentInfo dAppInfo;
		List<AppointmentInfo> appInfos;
		Map<String, AppointmentInfo> appInfoMap = new HashMap<String, AppointmentInfo>();
		
		appInfos = dependentCampaign.getAppointmentInfo();
		for (AppointmentInfo appInfo : appInfos) {
			key = appInfo.getOffice().getOfficeid() + "-" + appInfo.getDoctor().getDoctorid() + "-" +
					appInfo.getAppointee().getPatientid();
			appInfoMap.put(key, appInfo);
			
			if (appInfo.getStatus() ==  AppointmentStatus.ACCEPTED) {
				//delete from Waitlist
				query = "from Waitlist where practiceid = " + dependentCampaign.getPractice().getPracticeid() +
						" and officeid = " + appInfo.getOffice().getOfficeid() + 
						" and doctor= " + appInfo.getDoctor().getDoctorid() +
						" and patientid = " + appInfo.getAppointee().getPatientid();
				
				//Mark as deleted in waiting campaign
				List<Waitlist> waitlist = WaitlistImpl.getWaitlist(session, query);
				if (waitlist == null) {
					System.out.println("No entry found in waitlist table");
				}
				else {
					for(Waitlist entry : waitlist) {
						session.delete(entry);
						break;
					}
				}
			}
		}
		
		appInfos = waitingCampaign.getAppointmentInfo();
		for (AppointmentInfo appInfo : appInfos) {
			key = appInfo.getOffice().getOfficeid() + "-" + appInfo.getDoctor().getDoctorid() + "-" +
					appInfo.getAppointee().getPatientid();
			dAppInfo = appInfoMap.get(key);
			if (dAppInfo == null) {
				System.out.println("No entry found for key [" + key + "]");
				continue;
			}
			//WAIT,TRYING,UNABLE_TO_SEND_MESSAGE, MESSAGE_SENT, NOT_DELIVERED,DELIVERED,ACCEPTED,CANCEL,RESCHEDULE,
			//PROBLEM,NO_RESPONSE,DELETED
			if (dAppInfo.getStatus() == AppointmentStatus.ACCEPTED) {
				appInfo.setStatus(AppointmentStatus.DELETED);
				appInfo.setPriority(-1);
			}
			else if (dAppInfo.getStatus() == AppointmentStatus.UNABLE_TO_SEND_MESSAGE ||
					dAppInfo.getStatus() == AppointmentStatus.NOT_DELIVERED) {
				appInfo.setPriority(4);
			}
			else if (dAppInfo.getStatus() == AppointmentStatus.NO_RESPONSE) {
				appInfo.setPriority(3);
			}
			else if (dAppInfo.getStatus() == AppointmentStatus.RESCHEDULE ||
					dAppInfo.getStatus() == AppointmentStatus.CANCEL){
				appInfo.setPriority(2);
			}
			else {
				appInfo.setPriority(1);
			}
			
			session.update(appInfo);
		}
		
		return;
	}

}
