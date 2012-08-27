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
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;
import com.patienttouch.hibernate.UserApi;
import com.patienttouch.hibernate.UserApiInvocationLog;

import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.util.Constants;
import com.patienttouch.util.Utility;

public class ReminderCampaignImpl {

	/**
	 * This method is used to setup a reminder campaign. This method is invoked
	 * by the RESTful Api.
	 * 
	 * @param jsonRequest
	 *            - contains all the parameters required to start a reminder
	 *            campaign
	 * @return - json response that contains the status of the request. The
	 *         status parameter in the response specifies if addition was
	 *         successful or the request failed
	 * 
	 */
	public static String setupReminderCampaign(String jsonRequest) {
		int ret;
		Gson gson = new Gson();
		long appointeeResponseTimeInMs = Constants.defaultReminderResponseTimeInMs;
		String practiceName;
		String appointeeid, officeid, doctorid;
		String smstemplateid, smstemplatename;
		String initialMsg, confirmMsg, rescheduleMsg;
		String query;
		String smsMessageText;
		String tmpStr;
		Response response = new Response();
		Office office;
		Doctor doctor;
		Patient appointee;
		Campaign reminder;
		Practice practice;
		SmsTemplates template;
		SmsMessage message;
		Session session = null;
		AppointmentInfo appointmentInfo;
		List<SmsMessage> messages = null, msgList = null;

		ReminderCampaignInfo request;
		Map<String, Office> offices = new HashMap<String, Office>();
		Map<String, Doctor> doctors = new HashMap<String, Doctor>();

		try {
			
			System.out.println("Setup Reminder request [" + jsonRequest + "]");
			
			// Parse jsonString
			request = gson.fromJson(jsonRequest, ReminderCampaignInfo.class);

			// Validate Mandatory Parameters
			ret = ReminderCampaignImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			// Validate practice name
			practiceName = request.getPractice();
			practice = PracticeImpl.getPractice(null, practiceName, false,
					false, false, false);
			if (practice == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			session = DbOperations.getDbSession();
			session.getTransaction().begin();

			reminder = new Campaign();
			reminder.setPractice(practice);
			reminder.setName("Reminder-" + Utility.getDateTimeInyyyyMMdd());
			reminder.setType(CampaignType.REMINDER);

			tmpStr = Utility
					.getProperty(session, Utility.REMINDER_RESPONSE_TIMEOUT_IN_MS);
			if (tmpStr != null && tmpStr.matches("\\d+")) {
				appointeeResponseTimeInMs = Long.parseLong(tmpStr);
			}
			reminder.setAppointeeResponseTimeInMs(appointeeResponseTimeInMs);

			smstemplateid = request.getTemplateId();
			if (smstemplateid != null && !smstemplateid.isEmpty() ) {
				if (!smstemplateid.matches("\\d+")) {
					response.getResults().setStatus(
							ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults()
							.setEdesc(
									ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				query = "from SmsTemplates where smstemplateid = "
						+ smstemplateid +
						// " and practiceid =" + practice.getPracticeid() +
						" and type = 'REMINDER'";
				template = SmsTemplateImpl.getTemplateInfo(session, query,
						false);
				if (template == null) {
					response.getResults().setStatus(
							ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults()
							.setEdesc(
									ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				
				initialMsg = template.getMessage();
				reminder.setSmstemplate(template);
				if (template.getConfirmMessage() != null
						|| template.getRescheduleMessage() != null) {
					reminder.setFollowUpCampaign(true);
				}
			} else {
				smstemplatename = request.getTemplateName();
				System.out.println("Find template [" + smstemplatename + "]");
				
				/*if (!smstemplatename.equalsIgnoreCase("custom")) {
					response.getResults().setStatus(
							ErrorCodes.INVALID_SMSTEMPLATE_NAME);
					response.getResults()
							.setEdesc(
									ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_NAME]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}*/
				if (smstemplatename.equalsIgnoreCase("custom")) {
					initialMsg = request.getInitialMessage();
					confirmMsg = request.getConfirmMessage();
					rescheduleMsg = request.getRescheduleMessage();
					
					reminder.setMessage(initialMsg);
					reminder.setConfirmMessage(confirmMsg);
					reminder.setRescheduleMessage(rescheduleMsg);

					if ((confirmMsg != null && !confirmMsg.isEmpty())
							|| (rescheduleMsg != null && !rescheduleMsg.isEmpty())) {
						reminder.setFollowUpCampaign(true);
					}
				}
				else {
					query = "from SmsTemplates where name = '"
							+ smstemplatename +
							"' and type = 'REMINDER'";
					
					template = SmsTemplateImpl.getTemplateInfo(session, query,
							false);
					if (template == null) {
						response.getResults().setStatus(
								ErrorCodes.INVALID_SMSTEMPLATE_NAME);
						response.getResults()
								.setEdesc(
										ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_NAME]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
					
					initialMsg = template.getMessage();
					reminder.setSmstemplate(template);
					if (template.getConfirmMessage() != null
							|| template.getRescheduleMessage() != null) {
						reminder.setFollowUpCampaign(true);
					}
				}
			}

			List<AppointmentInfo> appointmentInfos = new ArrayList<AppointmentInfo>();

			List<Map<String, Object>> appointments = request.getAppointments();
			for (Map<String, Object> appointment : appointments) {

				appointee = new Patient();
				appointee.setFirstName(request
						.getAppointeeFirstName(appointment));
				appointee
						.setLastName(request.getAppointeeLastName(appointment));
				appointee.setPhone(request.getAppointeePhone(appointment));

				appointeeid = request.getAppointeeId(appointment);
				if (appointeeid == null || appointeeid.isEmpty()) {
					// New appointee add to the database
					ret = DbOperations.addToDbWithConditionalTransaction(
							session, appointee);
					if (ret != ErrorCodes.SUCCESS) {
						// failed to add a new patient
						response.getResults().setStatus(
								ErrorCodes.UNABLE_TO_ADD_NEW_APPOINTEE);
						response.getResults()
								.setEdesc(
										ErrorCodes.edesc[ErrorCodes.UNABLE_TO_ADD_NEW_APPOINTEE]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
				} else {
					appointee.setPatientid(Integer.parseInt(appointeeid));
				}
				// Fetch Office details. 1st lookup HashMap then query database
				// if not found
				officeid = request.getOfficeId(appointment);
				office = offices.get(officeid);
				if (office == null) {
					query = "from Office where officeid = " + officeid
							+ " and practiceid =" + practice.getPracticeid();
					office = OfficeImpl.getOfficeInfo(session, query, false,
							false);
					if (office == null) {
						// failed to add a new patient
						response.getResults().setStatus(
								ErrorCodes.INVALID_OFFICE_ID);
						response.getResults().setEdesc(
								ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
					offices.put(officeid, office);
				}

				// Fetch Doctor details. Ist lookup HashMap then query database
				// if not found
				doctorid = request.getDoctorId(appointment);
				doctor = doctors.get(doctorid);
				if (doctor == null) {
					query = "from Doctor where doctorid = " + doctorid
							+ " and practiceid =" + practice.getPracticeid();
					doctor = DoctorImpl.getDoctorDetails(session, query);
					if (doctor == null) {
						// failed to add a new patient
						response.getResults().setStatus(
								ErrorCodes.INVALID_DOCTOR_ID);
						response.getResults().setEdesc(
								ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
					doctors.put(doctorid, doctor);
				}

				appointmentInfo = new AppointmentInfo();
				appointmentInfo.setOffice(office);
				appointmentInfo.setDoctor(doctor);
				appointmentInfo.setAppointee(appointee);
				appointmentInfo.setCampaign(reminder);
				appointmentInfo.setAppointmentDate(Utility.parseDate(request
						.getAppointmentDate(appointment)));
				appointmentInfo.setAppointmentTime(request
						.getAppointmentTime(appointment));
				appointmentInfo.setStatus(AppointmentStatus.TRYING);
				appointmentInfo.setLastUpdateTime(new Date(System
						.currentTimeMillis()));
				// Add patient info
				appointmentInfos.add(appointmentInfo);

				// Format SMS
				smsMessageText = SmsFormater.formatSms(
						initialMsg, practice.getName(),
						request.getAppointmentDate(appointment),
						request.getAppointmentTime(appointment), appointee,
						office, doctor);

				message = new SmsMessage();
				message.setMessageText(smsMessageText);
				message.setAppointmentInfo(appointmentInfo);
				message.setPhoneNumber(appointee.getPhone());
				message.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
				message.setType(SmsMessageType.INITIAL);

				msgList = new ArrayList<SmsMessage>();
				msgList.add(message);
				appointmentInfo.setSmsMessages(msgList);

				if (messages == null) {
					messages = new ArrayList<SmsMessage>();
				}

				messages.add(message);
			}

			reminder.setAppointmentInfo(appointmentInfos);
			reminder.setStatus(CampaignStatus.RUNNING);
			reminder.setScheduleTime(new Date(System.currentTimeMillis()));
			reminder.setLastUpdateTime(new Date(System.currentTimeMillis()));

			ret = DbOperations.addToDbWithConditionalTransaction(session,
					reminder);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
				response.getResults().setNumResults(0);
			}

			session.getTransaction().commit();

			// Send Reminder SMS to appointees
			// Send SMS
			for (SmsMessage msg : messages) {
				SmsMsgRouter.getInstance().sendMessage(msg);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
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
	private static int validateParameter(ReminderCampaignInfo request) {
		String tempStr;
		String practiceName = request.getPractice();
		if (practiceName == null || practiceName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING;
		}
		String templateId = request.getTemplateId();
		String templateName = request.getTemplateName();
		if ((templateId == null || templateId.isEmpty())
				&& (templateName == null || templateName.isEmpty())) {
			// Either template id should be present for existing template or
			// templateName should be present
			return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_ID_MISSING;
		}

		if (templateName != null && !templateName.isEmpty()
				&& templateName.equalsIgnoreCase("custom")) {
			// Initial message is mandatory
			String initialMessage = request.getInitialMessage();
			if (initialMessage == null || initialMessage.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_MESSAGE_MISSING;
			}
		}

		List<Map<String, Object>> appointments = request.getAppointments();
		for (Map<String, Object> appointment : appointments) {
			tempStr = request.getAppointeePhone(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING;
			}

			tempStr = request.getAppointeeFirstName(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_FIRST_NAME_MISSING;
			}

			tempStr = request.getAppointeeLastName(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_LAST_NAME_MISSING;
			}

			tempStr = request.getDoctorId(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_DOCTOR_MISSING;
			}

			tempStr = request.getOfficeId(appointment);
			if (tempStr == null || tempStr.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_OFFICE_MISSING;
			}

			tempStr = request.getAppointeeId(appointment);
			if (tempStr != null && !tempStr.isEmpty()
					&& !tempStr.matches("\\d+")) {
				return ErrorCodes.INVALID_APPOINTEE_ID;
			}
		}
		return ErrorCodes.SUCCESS;
	}

	/**
	 * This method is used to fetch status of reminder campaigns for a practice.
	 * 
	 * @param practiceName
	 * @param appointmentStartDate
	 * @param appointmentEndDate
	 * 
	 * @return
	 */
	public static String getReminderStatus(String practiceName, String userId,
			String appointmentStartDate, String appointmentEndDate) {
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		Session session = null;
		Campaign campaign;
		UserApiInvocationLog apiLog;
		User apiUser;
		List<User> users;
		boolean alreadyViewed = false;

		try {
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
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			
			query = "from User where userid = " + userId;

			apiUser = UserImpl.getUserDetails(session, query);
			if (apiUser == null) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			apiLog = UserApiLogImpl.getUserApiLog(session, userId,
					UserApi.REMINDER_STATUS);
			if (apiLog == null) {
				apiLog = new UserApiInvocationLog();
				apiLog.setUser(apiUser);
				apiLog.setApi(UserApi.REMINDER_STATUS);
				//set timestamp to 1 day old
				apiLog.setLastAccessTime(new Date(System.currentTimeMillis() - (Constants.msInOneDay)));
				
				session.save(apiLog);
			}
			
			/*query = "select appinfo from AppointmentInfo as appinfo inner join appinfo.campaign as camp inner join camp.practice as prac"
			+ " where prac.practiceid =" +
			+ p.getPracticeid() +
			" and camp.type = 'WAITLIST'";*/

			query = "select appinfo from AppointmentInfo as appinfo inner join appinfo.campaign as camp"
					+ " where camp.practice.practiceid =" +
					+ p.getPracticeid() +
					" and camp.type = 'REMINDER'";
			
			if (!appointmentStartDate.isEmpty()) {
				query = query + " and appinfo.appointmentDate >= '"
						+ appointmentStartDate 
						+ "'";
			}
			
			if (!appointmentEndDate.isEmpty()) {
				query = query + 
						"' and appinfo.appointmentDate <= '" + appointmentEndDate
						+ "'";
			}

			query = query + " order by appinfo.lastUpdateTime desc";

			System.out.println("Reminder Query [" + query + "]");
			
			List<AppointmentInfo> appInfoList;

			CampaignInfo campaignInfo = new CampaignInfo();

			campaignInfo.setPracticeName(p.getName());

			appInfoList = CampaignImpl.getAppointmentInfo(session, query);
			if (appInfoList == null) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				System.out.println("No Appointmentinfo found for practiceid =["
						+ p.getPracticeid() + "]");
				response.getResults().setStatus(ErrorCodes.SUCCESS);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			System.out.println("Number of appointments [" + appInfoList.size() + "]");
			
			for (AppointmentInfo appInfo : appInfoList) {
				campaign = appInfo.getCampaign();
				// campaignInfo.setCampaignAppointmentInfo(campaignid,
				// campaignName, officeid, office, doctorid, doctor,
				// appDate, appTime, appointmentInfoid, appointeeFirstName,
				// appointeeLastName, status);
				if (Utility.diffInMs(appInfo.getLastUpdateTime(), apiLog.getLastAccessTime()) > 0) {
					alreadyViewed = false;
				}
				else {
					alreadyViewed = true;
				}
				campaignInfo
						.setCampaignAppointmentInfo(
								Integer.toString(campaign.getCampaignid()), 
								campaign.getName(), 
								Integer.toString(appInfo.getOffice().getOfficeid()),
								appInfo.getOffice().getName(), 
								Integer.toString(appInfo.getDoctor().getDoctorid()), 
								appInfo.getDoctor().getNickName(), 
								Utility.getDateInyyyyMMdd(appInfo.getAppointmentDate()), 
								appInfo.getAppointmentTime(), 
								Integer.toString(appInfo.getAppointmentinfoid()),
								appInfo.getAppointee().getFirstName(), 
								appInfo.getAppointee().getLastName(), 
								appInfo.getStatus(), 
								alreadyViewed);
				resultCount++;
			}

			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(resultCount);
			response.getResults().setResults(campaignInfo.getRequest());
			
			apiLog.setLastAccessTime(new Date(System.currentTimeMillis()));
			session.update(apiLog);
			
			session.getTransaction().commit();
			
		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}
}
