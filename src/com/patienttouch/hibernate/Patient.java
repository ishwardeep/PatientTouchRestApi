package com.patienttouch.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Patient")
public class Patient {
	private int patientid;
	private String firstName;
	private String lastName;
	private String phone;
	private int del;

	@Id
	@GeneratedValue
	public int getPatientid() {
		return patientid;
	}

	public void setPatientid(int patientid) {
		this.patientid = patientid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getDel() {
		return del;
	}

	public void setDel(int del) {
		this.del = del;
	}
}
