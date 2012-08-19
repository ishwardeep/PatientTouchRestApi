package com.patienttouch.hibernate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SmsMessage")
public class SmsMessage {
	private int smsid;
	private AppointmentInfo appointmentInfo;
	private String messageText;
	private String phoneNumber;
	private Date smsTimestamp;
	private String triggerId;
	private String shortCode;
	private String vendorTransactionid;
	private String vendorStatusMessage;
	private String messageDeliveryStatus;
	private SmsStatus status;
	
	@Id
	@GeneratedValue
	public int getSmsid() {
		return smsid;
	}
	public void setSmsid(int smsid) {
		this.smsid = smsid;
	}
	
	@ManyToOne
	@JoinColumn(name="appointmentinfoid")
	public AppointmentInfo getAppointmentInfo() {
		return appointmentInfo;
	}
	public void setAppointmentInfo(AppointmentInfo appointmentInfo) {
		this.appointmentInfo = appointmentInfo;
	}
	
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Date getSmsTimestamp() {
		return smsTimestamp;
	}
	public void setSmsTimestamp(Date smsTimestamp) {
		this.smsTimestamp = smsTimestamp;
	}
	public String getTriggerId() {
		return triggerId;
	}
	public void setTriggerId(String triggerId) {
		this.triggerId = triggerId;
	}
	public String getShortCode() {
		return shortCode;
	}
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}
	public String getVendorTransactionid() {
		return vendorTransactionid;
	}
	public void setVendorTransactionid(String vendorTransactionid) {
		this.vendorTransactionid = vendorTransactionid;
	}
	public String getVendorStatusMessage() {
		return vendorStatusMessage;
	}
	public void setVendorStatusMessage(String vendorStatusMessage) {
		this.vendorStatusMessage = vendorStatusMessage;
	}
	
	public String getMessageDeliveryStatus() {
		return messageDeliveryStatus;
	}
	public void setMessageDeliveryStatus(String messageDeliveryStatus) {
		this.messageDeliveryStatus = messageDeliveryStatus;
	}
	
	@Enumerated(EnumType.STRING)
	public SmsStatus getStatus() {
		return status;
	}
	public void setStatus(SmsStatus status) {
		this.status = status;
	}
	
}
