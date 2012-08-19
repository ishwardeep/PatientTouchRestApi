package com.patienttouch.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="Waitlist")
public class Waitlist {
	private int waitlistid;
	private Practice practice;
	private Doctor doctor;
	private Office office;
	private Patient patient;
	private int priority;
	
	@Id
	@GeneratedValue
	public int getWaitlistid() {
		return waitlistid;
	}
	public void setWaitlistid(int waitlistid) {
		this.waitlistid = waitlistid;
	}
	
	@OneToOne
	@JoinColumn(name="practiceid")
	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
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
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
