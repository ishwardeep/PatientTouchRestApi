package com.patienttouch.api;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.patienttouch.client.ErrorCodes;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsMessageType;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.sms.threeci.EventNotification;
import com.patienttouch.sms.threeci.ThreeCiSmsProvider;

public class DlrNotificationTask implements Runnable {
	private final EventNotification en;
	
	public DlrNotificationTask(EventNotification evt) {
		this.en = evt;
	}
	
	@Override
	public void run() {
		int statusId, ret;
		String clientTag, appointmentinfoid ="", smsid="";
		String paramValues[],paramValue[];
		Session session = null;
		try {
			//Parse client tag parameter to extract appointmentinfoid
			clientTag = this.en.clientTag;
			
			System.out.println("Dlr notification received for [" + clientTag + "] with status [" + this.en.statusId + "]");
			
			paramValues = clientTag.split(":");
			for (String value : paramValues) {
				paramValue = value.split("=");
				if (paramValue[0].equalsIgnoreCase("appointmentinfoid")) {
					appointmentinfoid = paramValue[1];
				}
				else if (paramValue[0].equalsIgnoreCase("smsid")) {
					smsid = paramValue[1];
				}
				else {
					System.out.println("Unable to parse DlrNotification. Appointmentinfoid/Smsid parameter missing");
					return;
				}
			}
			
			if (smsid == null || smsid.isEmpty()) {
				System.out.println("Unable to parse DlrNotification. Smsid parameter missing");
				return;
			}
			
			//Fetch Sms info
			String query = "from SmsMessage where smsid =" + smsid; 
			List<SmsMessage> smsmessages = ReminderCampaignImpl.getSmsMessageInfo(null, query);
			if (smsmessages == null) {
				System.out.println("DLR notification Invalid smsid [" + smsid + "]");
				return;
			}
			// Fetch Appointment 
			/*query = "from AppointmentInfo where appointmentinfoid =" + appointmentinfoid;
			List<AppointmentInfo> appointmentinfos = ReminderCampaignImpl.getAppointmentInfo(null, query);
			if (appointmentinfos == null) {
				System.out.println("DLR notification Invalid Appointmentinfo id [" + appointmentinfoid + "]");
				return;
			}*/
			
			//List will have only 1 results. Fetch from 0 index
			SmsMessage smsmessage = smsmessages.get(0);
			AppointmentInfo appointment = smsmessage.getAppointmentInfo();
			
			statusId = Integer.parseInt(this.en.statusId);
			switch (statusId) {
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_DELIVERED:
					appointment.setStatus(AppointmentStatus.DELIVERED);
					smsmessage.setStatus(SmsStatus.SMS_DELIVERED);
					break;
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_DELIVERY_FAILED:
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_DELIVERY_FAILED_REMOTE_SMSC_REJECT:
					appointment.setStatus(AppointmentStatus.NOT_DELIVERED);
					smsmessage.setStatus(SmsStatus.SMS_NOT_DELIVERED);
					break;
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_SUBMITTED_TO_REMOTE_SMSC:
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_QUEUED_AT_REMOTE_SMSC:
				case ThreeCiSmsProvider.SMS_STATUS_CODE_MESSAGE_LOCALLY_QUEUED:
					appointment.setStatus(AppointmentStatus.MESSAGE_SENT);
					break;
				default:
					break;
			}
			
			if (this.en.detailedStatusDescription != null && !this.en.detailedStatusDescription.isEmpty()) {
				smsmessage.setMessageDeliveryStatus(this.en.detailedStatusDescription);
			}
			appointment.setLastUpdateTime(new Date(System.currentTimeMillis()));
			
			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			session.update(smsmessage);
			if (smsmessage.getType() == SmsMessageType.INITIAL) {
				session.update(appointment);
			}
			session.getTransaction().commit();
			
		}
		catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			t.printStackTrace();
		}

		return;
	}

}
