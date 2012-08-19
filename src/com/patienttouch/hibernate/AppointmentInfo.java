package com.patienttouch.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="AppointmentInfo")
public class AppointmentInfo {
	private int appointmentinfoid;
	private Campaign campaign;
	private Doctor doctor;
	private Office office;
	private Patient appointee;
	private Date appointmentDate;
	private String appointmentTime;
	private Date updateTime;
	private AppointmentStatus status;
	private List<SmsMessage> smsMessages;
	
	@Id
	@GeneratedValue
	public int getAppointmentinfoid() {
		return appointmentinfoid;
	}
	public void setAppointmentinfoid(int appointmentinfoid) {
		this.appointmentinfoid = appointmentinfoid;
	}
	
	@ManyToOne
	@JoinColumn(name="campaignid")
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	@OneToOne
	@JoinColumn(name="doctorid")
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	
	@OneToOne
	@JoinColumn(name="officeid")
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	
	@OneToOne
	@JoinColumn(name="patientid")
	public Patient getAppointee() {
		return appointee;
	}
	public void setAppointee(Patient appointee) {
		this.appointee = appointee;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Enumerated(EnumType.STRING)
	public AppointmentStatus getStatus() {
		return status;
	}
	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
	
	@OneToMany(targetEntity=SmsMessage.class, mappedBy="appointmentInfo", 
				cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<SmsMessage> getSmsMessages() {
		return smsMessages;
	}
	public void setSmsMessages(List<SmsMessage> smsMessages) {
		this.smsMessages = smsMessages;
	}

}
