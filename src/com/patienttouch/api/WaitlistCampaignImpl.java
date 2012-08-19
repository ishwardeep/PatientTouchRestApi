package com.patienttouch.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;

import com.patienttouch.client.CampaignInfo;
import com.patienttouch.client.ReminderCampaignInfo;
import com.patienttouch.client.Response;
import com.patienttouch.client.WaitlistCampaignInfo;
import com.patienttouch.client.WaitlistInfo;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;

import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.CampaignStatus;
import com.patienttouch.hibernate.CampaignType;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Patient;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;
import com.patienttouch.hibernate.Waitlist;

import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.util.Utility;

public class WaitlistCampaignImpl {
	
	/**
	 * This method is used to setup a waitlist campaign. This method is
	 * invoked by the RESTful Api.
	 * 
	 * @param jsonRequest
	 *            - contains all the parameters required to start a waitlist campaign
	 * @return - json response that contains the status of the request. The
	 *         status parameter in the response specifies if addition was
	 *         successful or the request failed
	 * 
	 */
	public static String setupWaitlistCampaign(String jsonRequest) {
		int ret, interMsgSpacingIn;
		Gson gson = new Gson();
		String practiceName;
		String appointeeid, officeid, doctorid, waitlistid;
		String smstemplateid, smstemplatename;
		String confirmMsg = null, rescheduleMsg = null;
		String query, interMsgSpacing;
		String smsMessageText;
		boolean followUp = false;
		Response response = new Response();
		Office office;
		Doctor doctor;
		Patient appointee;
		Campaign waitlistCampaign;
		Practice practice;
		SmsTemplates template = null;
		SmsMessage message;
		AppointmentInfo appointmentInfo;
		List<SmsMessage> messages = null, msgList = null;
		List<String> appointmentTime;
		List<Map<String, Object>> waitlist, availableAppointment;
		Waitlist wlist;
		WaitlistCampaignInfo request;
		List<Patient> appointees;
		
		try {
			// Parse jsonString
			request = gson.fromJson(jsonRequest, WaitlistCampaignInfo.class);

			// Validate Mandatory Parameters
			ret = WaitlistCampaignImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			//Validate practice name
			practiceName = request.getPractice();
			practice = PracticeImpl.getPractice(null, practiceName, false, false, false, false);
			if (practice == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			smstemplateid = request.getTemplateId();
			if (smstemplateid != null && !smstemplateid.isEmpty()) {
				if (!smstemplateid.matches("\\d+")) {
					response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				query = "from SmsTemplates where smstemplateid = " + smstemplateid + 
					    " and type = 'WAITLIST'";
				template = SmsTemplateImpl.getTemplateInfo(null, query, false);
				if (template == null) {
					response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				//waitlistCampaign.setSmstemplate(template);
				if (template.getConfirmMessage() != null || template.getRescheduleMessage() != null) {
					followUp = true;
					//waitlistCampaign.setFollowUpCampaign(true);
				}
			}
			else {
				smstemplatename = request.getTemplateName();
				if (!smstemplatename.equalsIgnoreCase("custom")) {
					response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_NAME);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_NAME]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				//waitlistCampaign.setMessage(request.getInitialMessage());
				confirmMsg = request.getConfirmMessage();
				rescheduleMsg = request.getRescheduleMessage();
				//waitlistCampaign.setConfirmMessage(confirmMsg);
				//waitlistCampaign.setRescheduleMessage(rescheduleMsg);
				
				if ((confirmMsg != null && !confirmMsg.isEmpty()) || (rescheduleMsg != null && !rescheduleMsg.isEmpty())) {
					followUp = true;
					//waitlistCampaign.setFollowUpCampaign(true);
				}	
			}

			availableAppointment = request.getAppointments();
	
			//In waitlist campaign we have only 1 appointment info like office, doctor, appointment date
			Map<String, Object> appointment = availableAppointment.get(0);
			//Fetch Office details
			officeid = request.getOfficeId(appointment);
			query = "from Office where officeid = " + officeid + " and practiceid =" + practice.getPracticeid();
			office = OfficeImpl.getOfficeInfo(null, query, false, false);
			if (office == null) {
				// failed to add a new patient
				response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
				response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
				
			//Fetch Doctor details
			doctorid = request.getDoctorId(appointment);
			query = "from Doctor where doctorid = " + doctorid + " and practiceid =" +  practice.getPracticeid();
			doctor = DoctorImpl.getDoctorDetails(null, query);
			if (doctor == null) {
				// failed to add a new patient
				response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
				response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			appointmentTime = request.getAppointmentTime(appointment);

			waitlist = request.getWaitlist();
			appointees = new ArrayList<Patient>();	
			// Add any new entries to patient database and waitlist 
			for (Map<String, Object> waitlistInfo : waitlist) {
				appointee = new Patient();
				appointee.setFirstName(request.getAppointeeFirstName(waitlistInfo));
				appointee.setLastName(request.getAppointeeLastName(waitlistInfo));
				appointee.setPhone(request.getAppointeePhone(waitlistInfo));
				
				appointeeid = request.getAppointeeId(waitlistInfo);
				if (appointeeid == null || appointeeid.isEmpty()) {
					//New appointee add to the database	
					ret = DbOperations.addToDb(null, appointee);
					if (ret != ErrorCodes.SUCCESS) {
						// failed to add a new patient
						response.getResults().setStatus(ErrorCodes.UNABLE_TO_ADD_NEW_APPOINTEE);
						response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.UNABLE_TO_ADD_NEW_APPOINTEE]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
				}
				else {
					appointee.setPatientid(Integer.parseInt(appointeeid));
				}
				
				wlist = new Waitlist();
				wlist.setPractice(practice);
				wlist.setDoctor(doctor);
				wlist.setOffice(office);
				wlist.setPatient(appointee);
				
				waitlistid = request.getWaitlistId(waitlistInfo);
				if (waitlistid == null || waitlistid.isEmpty()) {
					//New waitlist add to the database	
					ret = DbOperations.addToDb(null, wlist);
					if (ret != ErrorCodes.SUCCESS) {
						// failed to add a new patient
						response.getResults().setStatus(ErrorCodes.UNABLE_TO_ADD_TO_WAITLIST);
						response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.UNABLE_TO_ADD_TO_WAITLIST]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}	
				}
				else {
					wlist.setWaitlistid(Integer.parseInt(waitlistid));
				}
				appointees.add(appointee);
			}
			
			request.getInterMsgSpacing(appointment);
			//Create a new Campaign for each available time slot
			for (String time : appointmentTime) {		
				waitlistCampaign = new Campaign();
				waitlistCampaign.setPractice(practice);
				waitlistCampaign.setName("Waitlist-" + request.getAppointmentDate(appointment) + " " + time);
				waitlistCampaign.setType(CampaignType.WAITLIST);					
				waitlistCampaign.setFollowUpCampaign(followUp);
					
				if (smstemplateid != null && !smstemplateid.isEmpty()) {
					waitlistCampaign.setSmstemplate(template);
				}
				else {						
					waitlistCampaign.setMessage(request.getInitialMessage());
					waitlistCampaign.setConfirmMessage(confirmMsg);
					waitlistCampaign.setRescheduleMessage(rescheduleMsg);
				}
					
				List<AppointmentInfo> appointmentInfos = new ArrayList<AppointmentInfo>();
					
				for (Patient appointe : appointees) {
					
					//waitlistCampaign.se
					appointmentInfo = new AppointmentInfo();
					appointmentInfo.setOffice(office);
					appointmentInfo.setDoctor(doctor);
					appointmentInfo.setAppointee(appointe);
					appointmentInfo.setCampaign(waitlistCampaign);
					appointmentInfo.setAppointmentDate(new Date(request.getAppointmentDate(appointment)));
					appointmentInfo.setAppointmentTime(time);
					appointmentInfo.setStatus(AppointmentStatus.TRYING);
					appointmentInfo.setUpdateTime(new Date(System.currentTimeMillis()));
						
					//Add patient info
					appointmentInfos.add(appointmentInfo);
						
					//Format SMS
					smsMessageText = SmsFormater.formatSms(request.getInitialMessage(), practice.getName() , 
								request.getAppointmentDate(appointment), time, 
								appointe, office, doctor);
						
					message = new SmsMessage();
					message.setMessageText(smsMessageText);
					message.setAppointmentInfo(appointmentInfo);
					message.setPhoneNumber(appointe.getPhone());
					message.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
						
					msgList = new ArrayList<SmsMessage>();
					msgList.add(message);
					appointmentInfo.setSmsMessages(msgList);
						
					if (messages == null) {
						messages = new ArrayList<SmsMessage>();
					}
						
					messages.add(message);
				}
					
				waitlistCampaign.setAppointmentInfo(appointmentInfos);
				waitlistCampaign.setStatus(CampaignStatus.RUNNING);
				waitlistCampaign.setScheduleTime(new Date(System.currentTimeMillis()));
					
				ret = DbOperations.addToDb(null, waitlistCampaign);
				if (ret != ErrorCodes.SUCCESS) {
					response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
					response.getResults().setEdesc(
							ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
					response.getResults().setNumResults(0);
				}		
			}
			
			//Send Reminder SMS to appointees		
			//Send SMS
			for (SmsMessage msg : messages) {
				SmsMsgRouter.getInstance().sendMessage(msg);
			}
		} catch (Throwable t) {
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		return gson.toJson(response);
	}
	
	/**
	 * This method is used to fetch status of waitlist campaigns for a practice.
	 * @param practiceName
	 * @return
	 */
	public static String getWaitlistStatus(String practiceName) 
	{
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		Session session = null;
		List<Campaign> campaignlist;
		
		// parameter validation
		if (practiceName == null || practiceName.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Lookup db to fetch practice details
		Practice p = PracticeImpl.getPractice(session, practiceName, false,
						false, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);
			
			return gson.toJson(response);
		}
				
		query = "from Campaign where practiceid =" + p.getPracticeid() + " and type = 'WAITLIST' order by scheduleTime desc";
		
		campaignlist = WaitlistCampaignImpl.getWaitCampaignlist(null, query);
		if (campaignlist == null) {
			//Empty waitlist
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		
		List<AppointmentInfo> appInfoList;
		
		CampaignInfo wlInfo = new CampaignInfo();
		
		for(Campaign campaign : campaignlist) {
			wlInfo.setPracticeName(p.getName());
			
			//all records office doctor 
			query = "from AppointmentInfo where campaignid =" + campaign.getCampaignid();
			
			appInfoList = WaitlistCampaignImpl.getAppointmentInfo(null, query);
			if (appInfoList == null) {
				System.out.println("No Appointmentinfo found for campaignid =[" + campaign.getCampaignid() + "]");
				continue;
			}
			for (AppointmentInfo appInfo : appInfoList) {
				wlInfo.setCampaignInfo(Integer.toString(campaign.getCampaignid()), campaign.getName(), 
						Integer.toString(appInfo.getOffice().getOfficeid()), appInfo.getOffice().getName(), 
						Integer.toString(appInfo.getDoctor().getDoctorid()), appInfo.getDoctor().getNickName(), 
						Utility.getDateInyyyyMMdd(appInfo.getAppointmentDate()), appInfo.getAppointmentTime(),
						campaign.getStatus());
				break;
			}
			
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(wlInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to fetch details for a campaign based on campaignid
	 * @param practiceName
	 * @return
	 */
	public static String getWaitlistAppointmentInfoStatus(String campaignid) 
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
			
		appInfoList = WaitlistCampaignImpl.getAppointmentInfo(null, query);
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
						appInfo.getAppointee().getLastName(), appInfo.getStatus());
			resultCount++;
		}
			
		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(wlInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to fetch details for a appointment based on appointmentinfoid
	 * @param practiceName
	 * @return
	 */
	public static String getWaitlistAppointmentDetailStatus(String appointmentinfoid) 
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
			
		smsInfoList = WaitlistCampaignImpl.getSmsInfo(null, query);
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
			
			wlInfo.setAppointmentSmsInfo(Integer.toString(campaign.getCampaignid()), appointmentinfoid, 
						Integer.toString(sms.getSmsid()), sms.getMessageText(), 
						Utility.getDateTimeInyyyyMMdd(sms.getSmsTimestamp()), sms.getPhoneNumber(), sms.getStatus());
			resultCount++;
		}
			
		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(wlInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to validate if all mandatory parameters are received
	 * in the request
	 * 
	 * @param request
	 *            - java object created after parsing input json request
	 * @return SUCCESS if parameter validation was successful.
	 */
	private static int validateParameter(WaitlistCampaignInfo request) {
		String tempStr;
		List<String> appointmentTime;
		List<Map<String,Object>>  waitlist;
		
		String practiceName = request.getPractice();
		if (practiceName == null || practiceName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING;
		}
		
		String templateId = request.getTemplateId();
		String templateName = request.getTemplateName();
		if ((templateId == null || templateId.isEmpty()) && (templateName == null || templateName.isEmpty())) {
			//Either template id should be present for existing template or templateName should be present
			return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_ID_MISSING;
		}
		
		if (templateName != null && !templateName.isEmpty() && templateName.equalsIgnoreCase("custom")) {
			//Initial message is mandatory
			String initialMessage = request.getInitialMessage();
			if (initialMessage == null || initialMessage.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_MESSAGE_MISSING;
			}
		}
		
		List<Map<String,Object>>  appointments = request.getAppointments();
		for(Map<String,Object> appointment : appointments) {
			tempStr = request.getAppointmentDate(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTMENT_DATE_MISSING;
			}
		
			appointmentTime = request.getAppointmentTime(appointment);
			if ((appointmentTime == null) || (appointmentTime != null && appointmentTime.isEmpty())) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTMENT_TIME_MISSING;
			}
			
			tempStr = request.getDoctorId(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_DOCTOR_MISSING;
			}
			
			tempStr = request.getOfficeId(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_OFFICE_MISSING;
			}	
		}
		
		waitlist = (List<Map<String, Object>>) request.getWaitlist();	
		for (Map<String, Object> appointee: waitlist) {
			tempStr = request.getAppointeePhone(appointee);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING;
			}
			
			tempStr = request.getAppointeeFirstName(appointee);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_FIRST_NAME_MISSING;
			}
			
			tempStr = request.getAppointeeLastName(appointee);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_LAST_NAME_MISSING;
			}
			
			tempStr = request.getAppointeeId(appointee);
			if (tempStr != null && !tempStr.isEmpty() && !tempStr.matches("\\d+")) {
				return ErrorCodes.INVALID_APPOINTEE_ID;
			}
			
			tempStr = request.getWaitlistId(appointee);
			if (tempStr != null && !tempStr.isEmpty() && !tempStr.matches("\\d+")) {
				return ErrorCodes.INVALID_WAITLIST_ID;
			}
		}
		
		return ErrorCodes.SUCCESS;
	}

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
	 * This method is used to fetch details for an appointment. When/What Sms was sent
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	public static List<SmsMessage> getSmsInfo(Session dbsession, String query) {
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
	 * 
	 * @param dbsession
	 * @param query
	 * @return
	 */
	public static List<Campaign> getWaitCampaignlist(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
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
	
}
