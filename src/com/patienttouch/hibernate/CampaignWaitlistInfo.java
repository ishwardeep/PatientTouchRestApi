package com.patienttouch.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CampaignWaitlistInfo")
public class CampaignWaitlistInfo {
	private int waitlistid;
	private Office office;
	private Doctor doctor;
	private Patient patient;
	
	@Id
	@GeneratedValue
	public int getWaitlistid() {
		return waitlistid;
	}
	public void setWaitlistid(int waitlistid) {
		this.waitlistid = waitlistid;
	}
	
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
}
