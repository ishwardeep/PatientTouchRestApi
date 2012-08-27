package com.patienttouch.hibernate;

/**
 * Appointment Status enum is used to keep track of various states an appointment can be in
 * WAIT - No SMS has been sent to the appointee as SMS has been sent to some other appointee in same campaign. This state will
 *        exist mostly for Waitlist campaign where the messages are sent sequentially 1 after the other
 * TRYING - Task to submit an SMS has been initiated
 * UNABLE_TO_SEND_MESSAGE - Task to send SMS returned an error - because of invalid phone number, landline number etc
 * MESSAGE_SENT - Sms has been sent to SMSC for delivery
 * NOT_DELIVERED - SMSC was unable to deliver the message to the destination phone number
 * DELIVERED - Sms successfully sent delivered to destination phone number
 * ACCEPTED - Appointee responded to confirm the appointment
 * CANCEL - Appointee declined the appointment
 * RESCHEDULE - Appointee wants to reschedule the the appointment
 * PROBLEM - 
 * NO_RESPONSE - Appointee did not respond to the SMS
 * DELETED - Appointee deleted from waitlist as they have already accepted an appointment slot
 * 
 * @author Ishwardeepsingh
 *
 */
public enum AppointmentStatus {
	WAIT,TRYING,UNABLE_TO_SEND_MESSAGE, MESSAGE_SENT, NOT_DELIVERED,DELIVERED,ACCEPTED,CANCEL,RESCHEDULE,
	PROBLEM,NO_RESPONSE,DELETED
}
