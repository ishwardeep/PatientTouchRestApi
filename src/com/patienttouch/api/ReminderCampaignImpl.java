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
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;

import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.SmsMsgRouter;
import com.patienttouch.util.Utility;

public class ReminderCampaignImpl {
	
	/**
	 * This method is used to setup a reminder campaign. This method is
	 * invoked by the RESTful Api.
	 * 
	 * @param jsonRequest
	 *            - contains all the parameters required to start a reminder campaign
	 * @return - json response that contains the status of the request. The
	 *         status parameter in the response specifies if addition was
	 *         successful or the request failed
	 * 
	 */
	public static String setupReminderCampaign(String jsonRequest) {
		int ret;
		Gson gson = new Gson();
		String practiceName;
		String appointeeid, officeid, doctorid;
		String smstemplateid, smstemplatename;
		String confirmMsg, rescheduleMsg;
		String query;
		String smsMessageText;
		Response response = new Response();
		Office office;
		Doctor doctor;
		Patient appointee;
		Campaign reminder;
		Practice practice;
		SmsTemplates template;
		SmsMessage message;
		AppointmentInfo appointmentInfo;
		List<SmsMessage> messages = null, msgList = null;
		
		ReminderCampaignInfo request;
		Map<String, Office> offices = new HashMap<String, Office>();
		Map<String, Doctor> doctors = new HashMap<String, Doctor>();
		
		try {
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

			//Validate practice name
			practiceName = request.getPractice();
			practice = PracticeImpl.getPractice(null, practiceName, false, false, false, false);
			if (practice == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			reminder = new Campaign();
			reminder.setPractice(practice);
			reminder.setName("Reminder-" + Utility.getDateTimeInyyyyMMdd());
			reminder.setType(CampaignType.REMINDER);
			
			smstemplateid = request.getTemplateId();
			if (smstemplateid != null && !smstemplateid.isEmpty()) {
				if (!smstemplateid.matches("\\d+")) {
					response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				query = "from SmsTemplates where smstemplateid = " + smstemplateid + 
						//" and practiceid =" + practice.getPracticeid() + 
						" and type = 'REMINDER'";
				template = SmsTemplateImpl.getTemplateInfo(null, query, false);
				if (template == null) {
					response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				reminder.setSmstemplate(template);
				if (template.getConfirmMessage() != null || template.getRescheduleMessage() != null) {
					reminder.setFollowUpCampaign(true);
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
				reminder.setMessage(request.getInitialMessage());
				confirmMsg = request.getConfirmMessage();
				rescheduleMsg = request.getRescheduleMessage();
				reminder.setConfirmMessage(confirmMsg);
				reminder.setRescheduleMessage(rescheduleMsg);
				
				if ((confirmMsg != null && !confirmMsg.isEmpty()) || (rescheduleMsg != null && !rescheduleMsg.isEmpty())) {
					reminder.setFollowUpCampaign(true);
				}	
			}
			
			List<AppointmentInfo> appointmentInfos = new ArrayList<AppointmentInfo>();
				
			List<Map<String, Object>> appointments = request.getAppointments();
			for (Map<String, Object> appointment : appointments) {
				
				appointee = new Patient();
				appointee.setFirstName(request.getAppointeeFirstName(appointment));
				appointee.setLastName(request.getAppointeeLastName(appointment));
				appointee.setPhone(request.getAppointeePhone(appointment));
				
				appointeeid = request.getAppointeeId(appointment);
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
				//Fetch Office details. 1st lookup HashMap then query database if not found
				officeid = request.getOfficeId(appointment);
				office = offices.get(officeid);
				if (office == null) {
					query = "from Office where officeid = " + officeid + " and practiceid =" + practice.getPracticeid();
					office = OfficeImpl.getOfficeInfo(null, query, false, false);
					if (office == null) {
						// failed to add a new patient
						response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
						response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
						response.getResults().setNumResults(0);

						return gson.toJson(response);
					}
					offices.put(officeid, office);
				}
				
				//Fetch Doctor details. Ist lookup HashMap then query database if not found
				doctorid = request.getDoctorId(appointment);
				doctor = doctors.get(doctorid);
				if (doctor == null) {
					query = "from Doctor where doctorid = " + doctorid + " and practiceid =" +  practice.getPracticeid();
					doctor = DoctorImpl.getDoctorDetails(null, query);
					if (doctor == null) {
						// failed to add a new patient
						response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
						response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
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
				appointmentInfo.setAppointmentDate(new Date(request.getAppointmentDate(appointment)));
				appointmentInfo.setAppointmentTime(request.getAppointmentTime(appointment));
				appointmentInfo.setStatus(AppointmentStatus.TRYING);
				appointmentInfo.setUpdateTime(new Date(System.currentTimeMillis()));
				//Add patient info
				appointmentInfos.add(appointmentInfo);
				
				//Format SMS
				smsMessageText = SmsFormater.formatSms(request.getInitialMessage(), practice.getName() , 
						request.getAppointmentDate(appointment), request.getAppointmentTime(appointment), 
						appointee, office, doctor);
				
				message = new SmsMessage();
				message.setMessageText(smsMessageText);
				message.setAppointmentInfo(appointmentInfo);
				message.setPhoneNumber(appointee.getPhone());
				message.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
				
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
			
			ret = DbOperations.addToDb(null, reminder);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
				response.getResults().setNumResults(0);
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
			if (tempStr != null && !tempStr.isEmpty() && !tempStr.matches("\\d+")) {
				return ErrorCodes.INVALID_APPOINTEE_ID;
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
}
