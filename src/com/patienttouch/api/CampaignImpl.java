package com.patienttouch.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.CampaignInfo;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.sms.SmsFormater;
import com.patienttouch.util.Utility;

public class CampaignImpl {
	/**
	 * This method is used to fetch details for an appointment
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	public static List<AppointmentInfo> getAppointmentInfo(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		Practice p;
		List<AppointmentInfo> results = null;
		
		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.getTransaction().begin();
			}
			Query q = session.createQuery(query);

			results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
	
	/**
	 * This method is used to fetch details for an appointment
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	public static List<SmsMessage> getSmsMessageInfo(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		Practice p;
		List<SmsMessage> results = null;
		
		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.getTransaction().begin();
			}
			Query q = session.createQuery(query);

			results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
	
	/**
	 * This method is used to fetch details for campaigns
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	public static List<Campaign> getCampaignInfo(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		Practice p;
		List<Campaign> results = null;
		
		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.getTransaction().begin();
			}
			Query q = session.createQuery(query);

			results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}
			
			for (Campaign c : results) {
				c.getAppointmentInfo();
				break;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
	
	
	/**
	 * This method is used to send message to the next Appointee in wait state
	 * 
	 * @param session Db session
	 * @param campaign
	 * @param smsToBeSent
	 * @return
	 */
	public static void sendSmsToAppointeeInWaitState(Session session, Campaign campaign, List<SmsMessage> smsToBeSent) {
		List<AppointmentInfo> appInfos = campaign.getAppointmentInfo();
		
		for (AppointmentInfo appInfo : appInfos) {
			if (appInfo.getStatus() != AppointmentStatus.WAIT) {
				continue;
			}
			
			String smsMessageText = SmsFormater.formatSms(campaign.getMessage(),
					campaign.getPractice().getName(), Utility
							.getDateTimeInyyyyMMdd(appInfo
									.getAppointmentDate()),
									appInfo.getAppointmentTime(), appInfo.getAppointee(),
									appInfo.getOffice(), appInfo
							.getDoctor());

			SmsMessage message = new SmsMessage();
			message.setMessageText(smsMessageText);
			message.setAppointmentInfo(appInfo);
			message.setPhoneNumber(appInfo.getAppointee().getPhone());
			message
					.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
			message.setType(SmsMessageType.INITIAL);

			appInfo.setStatus(AppointmentStatus.TRYING);
			appInfo.setLastUpdateTime(new Date(System.currentTimeMillis()));
			
			smsToBeSent.add(message);
			
			session.update(appInfo);
			session.save(message);
			
			break;
		}
		
		return;
	}

	/**
	 * This method is used to fetch details for a campaign based on campaignid
	 * @param practiceName
	 * @return
	 */
	public static String getCampaignAppointmentInfoStatus(String campaignid) 
	{
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		List<AppointmentInfo> appInfoList;
		Campaign campaign;
		CampaignInfo wlInfo = new CampaignInfo();
			
		//all records office doctor 
		query = "from AppointmentInfo where campaignid =" + campaignid;
			
		appInfoList = CampaignImpl.getAppointmentInfo(null, query);
		if (appInfoList == null) {
			System.out.println("No Appointmentinfo found for campaignid =[" + campaignid + "]");
			response.getResults().setStatus(ErrorCodes.INVALID_CAMPAIGN_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_CAMPAIGN_ID]);
			response.getResults().setNumResults(0);
			
			return gson.toJson(response);
		}
		
		for (AppointmentInfo appInfo : appInfoList) {
			campaign = appInfo.getCampaign();
			wlInfo.setCampaignAppointmentInfo(Integer.toString(campaign.getCampaignid()),campaign.getName(), 
						Integer.toString(appInfo.getOffice().getOfficeid()), appInfo.getOffice().getName(), 
						Integer.toString(appInfo.getDoctor().getDoctorid()), appInfo.getDoctor().getNickName(), 
						Utility.getDateInyyyyMMdd(appInfo.getAppointmentDate()), appInfo.getAppointmentTime(),
						Integer.toString(appInfo.getAppointmentinfoid()), appInfo.getAppointee().getFirstName(),
						appInfo.getAppointee().getLastName(), appInfo.getStatus(), false);
			resultCount++;
		}
			
		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(wlInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to fetch details for a appointment based on appointmentinfoid
	 * @param appointmentinfoid
	 * @return
	 */
	public static String getCampaignAppointmentDetailStatus(String appointmentinfoid) 
	{
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		List<SmsMessage> smsInfoList;
		Campaign campaign;
		AppointmentInfo appInfo;
		
		CampaignInfo wlInfo = new CampaignInfo();
			
		//all records office doctor 
		query = "from SmsMessage where appointmentinfoid =" + appointmentinfoid;
			
		smsInfoList = CampaignImpl.getSmsMessageInfo(null, query);
		if (smsInfoList == null) {
			System.out.println("No Appointmentinfo found for appointmentinfoid =[" + appointmentinfoid + "]");
			response.getResults().setStatus(ErrorCodes.INVALID_APPOINTMENT_INFO_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_APPOINTMENT_INFO_ID]);
			response.getResults().setNumResults(0);
			
			return gson.toJson(response);
		}
		
		for (SmsMessage sms : smsInfoList) {
			appInfo = sms.getAppointmentInfo();
			campaign = appInfo.getCampaign();
			
			wlInfo.setAppointmentSmsInfo(Integer.toString(campaign.getCampaignid()), campaign.getName(), appointmentinfoid, 
						Integer.toString(sms.getSmsid()), sms.getMessageText(), 
						Utility.getDateTimeInyyyyMMdd(sms.getSmsTimestamp()), sms.getPhoneNumber(), sms.getStatus());
			resultCount++;
		}
			
		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(wlInfo.getRequest());

		return gson.toJson(response);

	}
}
