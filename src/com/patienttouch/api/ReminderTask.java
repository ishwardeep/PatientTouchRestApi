package com.patienttouch.api;

import java.io.StringReader;
import java.util.Date;
import java.util.concurrent.Callable;

import org.hibernate.Session;
import org.xmappr.Xmappr;

import com.patienttouch.client.ErrorCodes;
import com.patienttouch.hibernate.AppointmentInfo;
import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.SmsMessage;
import com.patienttouch.hibernate.SmsStatus;
import com.patienttouch.sms.SmsProvider;
import com.patienttouch.sms.SmsProviderFactory;
import com.patienttouch.sms.threeci.ErrorNotification;
import com.patienttouch.sms.threeci.SuccessNotification;

//public class ReminderTask<Integer> implements Callable<Integer> {
public class ReminderTask implements Runnable {
	SmsMessage smsMsg;

	public ReminderTask(SmsMessage msg) {
		this.smsMsg = msg;
	}

	@Override
	// public Integer call() throws Exception {
	public void run() {
		int ret = ErrorCodes.SUCCESS;
		Xmappr xm;
		String userdata;
		StringReader reader;
		SmsProvider smsprovider;
		Session session= null;
		ErrorNotification en;
		SuccessNotification sn;
		AppointmentInfo appointmentInfo = this.smsMsg.getAppointmentInfo();

		try {
			System.out.println("Id [" + appointmentInfo.getAppointmentinfoid()
					+ "]");
			smsprovider = SmsProviderFactory
					.createSmsProvider(SmsProviderFactory.THREE_CI_PROVIDER);
			
			// Set sms sent time
			this.smsMsg.setSmsTimestamp(new Date(System.currentTimeMillis()));

			userdata = "appointmentinfoid=" + appointmentInfo.getAppointmentinfoid() + ":smsid=" + 
			this.smsMsg.getSmsid();
			
			String response = smsprovider.sendMessage(
					this.smsMsg.getPhoneNumber(), this.smsMsg.getMessageText(), userdata);
			if (response == null) {
				// Error condition
				System.out.println("Unable to send SMS");
				appointmentInfo
						.setStatus(AppointmentStatus.UNABLE_TO_SEND_MESSAGE);
			} else {
				reader = new StringReader(response);

				// If message submission was successful
				if (response.indexOf("successNotification") != -1) {
					xm = new Xmappr(SuccessNotification.class);
					sn = (SuccessNotification) xm.fromXML(reader);

					this.smsMsg.setVendorStatusMessage(sn.message.get(0));
					this.smsMsg.setTriggerId(sn.triggerId);
					this.smsMsg.setStatus(SmsStatus.SMS_SUBMITED_SUCCESSFULLY);
					
					appointmentInfo.setStatus(AppointmentStatus.MESSAGE_SENT);
				} else if (response.indexOf("errorNotification") != -1) {
					xm = new Xmappr(ErrorNotification.class);
					en = (ErrorNotification) xm.fromXML(reader);

					this.smsMsg.setVendorStatusMessage(en.message.get(0));
					this.smsMsg.setStatus(SmsStatus.SMS_SUBMISSION_ERROR);
					
					appointmentInfo
							.setStatus(AppointmentStatus.UNABLE_TO_SEND_MESSAGE);
				}
			}
			// Update message in the database
			session = DbOperations.getDbSession();

			session.getTransaction().begin();
			session.update(smsMsg);
			session.update(appointmentInfo);
			session.getTransaction().commit();
			
		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			t.printStackTrace();
		}

		return;
	}

}
