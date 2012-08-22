package com.patienttouch.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.xmappr.Element;

import com.patienttouch.client.ErrorCodes;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.Campaign;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Patient;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.sms.SmsFormater;
import com.patienttouch.sms.threeci.CancelSubscriptionNotification;
import com.patienttouch.sms.threeci.SubscriptionNotification;
import com.patienttouch.util.Utility;

public class PostNotificationTask implements Runnable {
	private static final String regExpr = "(\\+1)(.*)";
	private static final Pattern pattern = Pattern.compile(regExpr);

	// SubscriptionNotification is received with the appointee response with 1 -
	// Accepted
	private final SubscriptionNotification sn;
	private final CancelSubscriptionNotification csn;
	
	public PostNotificationTask(SubscriptionNotification sn, CancelSubscriptionNotification csn) {
		this.sn = sn;
		this.csn = csn;
	}

	@Override
	public void run() {
		String phone, query, smsMessageText, preFormattedMsg = null;
		Session session = null;
		Campaign campaign;
		SmsMessage smsMessage, confirmMessage;
		SmsMessageType type = null;
		AppointmentInfo appInfo;
		List<AppointmentInfo> appointmentInfos;
		Map<String, String> notificationParam = new HashMap<String, String>();
		
		if (this.sn != null) {
			notificationParam.put("notification", "subscription");
			notificationParam.put("transactionId", this.sn.transactionId);
			notificationParam.put("trigger", this.sn.trigger);
			notificationParam.put("triggerId", this.sn.triggerId);
			notificationParam.put("groupId", this.sn.groupId);
			notificationParam.put("phoneNumber", this.sn.phoneNumber);
			notificationParam.put("carrierId", this.sn.carrierId);
		}
		else if (this.csn != null) {
			notificationParam.put("notification", "cancelsubscription");
			notificationParam.put("transactionId", this.csn.transactionId);
			notificationParam.put("trigger", this.csn.trigger);
			notificationParam.put("triggerId", this.csn.triggerId);
			notificationParam.put("groupId", this.csn.groupId);
			notificationParam.put("phoneNumber", this.csn.phoneNumber);
			notificationParam.put("carrierId", this.csn.carrierId);
		}
		else {
			System.out.println("Notification parameters are null");
			return;
		}
		
		System.out.println("Phone [" + notificationParam.get("phoneNumber") + "] TriggerId ["
				+ notificationParam.get("triggerId") + "] Trigger [" + notificationParam.get("trigger") + "]");

		try {
			// Remove country code +1 from the the phone number
			Matcher m = PostNotificationTask.pattern.matcher(notificationParam.get("phoneNumber"));
			if (m.matches()) {
				System.out.println("Phone [" + m.group(0) + "] [" + m.group(1)
						+ "] [" + m.group(2) + "]");
				phone = m.group(2);
			} else {
				phone = notificationParam.get("phoneNumber");
			}

			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			
			// Get patient whose phone number matches the phone number
			query = "from Patient where phone = '" + phone + "'";
			List<Patient> patients = PatientImpl.getPatientDetails(session, query);
			if (patients == null) {
				if (session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				
				System.out
						.println("Unable to process SubscriptionNotification. No patient found for phone ["
								+ phone + "]");
				return;
			}

			for (Patient appointee : patients) {
				System.out.println("Found Patient [" + appointee.getPatientid()
						+ "] FN [" + appointee.getFirstName() + "]");

				query = "from AppointmentInfo where patientid = "
						+ appointee.getPatientid()
						+ " and status in ('MESSAGE_SENT','DELIVERED')"
						+ " order by lastUpdateTime desc limit 1";

				appointmentInfos = WaitlistCampaignImpl.getAppointmentInfo(
						session, query);
				if (appointmentInfos == null) {
					if (session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					System.out
							.println("Unable to process SubscriptionNotification. No Appointment info found");
					return;
				}

				// limit 1 in the query above ensures that only one result is
				// received
				appInfo = appointmentInfos.get(0);

				smsMessage = new SmsMessage();
				smsMessage.setAppointmentInfo(appInfo);
				smsMessage.setTriggerId(notificationParam.get("triggerId"));
				smsMessage.setMessageText(notificationParam.get("trigger"));
				smsMessage
						.setSmsTimestamp(new Date(System.currentTimeMillis()));
				smsMessage.setPhoneNumber(phone);
				smsMessage.setVendorTransactionid(notificationParam.get("transactionId"));
				smsMessage.setStatus(SmsStatus.SMS_DELIVERED);
				smsMessage.setType(SmsMessageType.RESPONSE);
				
				session.save(smsMessage);
				
				campaign = appInfo.getCampaign();
				
				if (notificationParam.get("notification").equalsIgnoreCase("subscription")) {
					appInfo.setStatus(AppointmentStatus.ACCEPTED);
					preFormattedMsg = campaign.getConfirmMessage();
					type = SmsMessageType.CONFIRM;
				}
				else if (notificationParam.get("notification").equalsIgnoreCase("cancelsubscription")) {
					appInfo.setStatus(AppointmentStatus.RESCHEDULE);
					preFormattedMsg = campaign.getRescheduleMessage();
					type = SmsMessageType.RESCHEDULE;
				}
				appInfo.setLastUpdateTime(new Date(System.currentTimeMillis()));

				session.update(appInfo);

				if (campaign.isFollowUpCampaign()) {
					// Format SMS
					smsMessageText = SmsFormater.formatSms(preFormattedMsg, campaign.getPractice()
							.getName(), Utility.getDateTimeInyyyyMMdd(appInfo
							.getAppointmentDate()), appInfo
							.getAppointmentTime(), appointee, appInfo
							.getOffice(), appInfo.getDoctor());

					confirmMessage = new SmsMessage();
					confirmMessage.setMessageText(smsMessageText);
					confirmMessage.setAppointmentInfo(appInfo);
					confirmMessage.setPhoneNumber(appointee.getPhone());
					confirmMessage.setStatus(SmsStatus.SMS_SUBMISSION_PENDING);
					confirmMessage.setType(type);
					
					session.save(confirmMessage);
				}
				
				campaign.setLastUpdateTime(new Date(System.currentTimeMillis()));
				
				System.out.println("Campaign last time updated");
				session.update(campaign);
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
