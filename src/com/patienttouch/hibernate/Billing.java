package com.patienttouch.hibernate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="Billing")
public class Billing {
	private int billingid;
	private String contactFirstName;
	private String contactLastName;
	private String streetAddress1;
	private String streetAddress2;
	private String city;
	private String state;
	private String zip;
	private Practice practice;
	
	@Id
	@GeneratedValue
	public int getBillingid() {
		return billingid;
	}
	public void setBillingid(int billingid) {
		this.billingid = billingid;
	}
	
	public String getContactFirstName() {
		return contactFirstName;
	}
	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}
	public String getContactLastName() {
		return contactLastName;
	}
	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}
	public String getStreetAddress1() {
		return streetAddress1;
	}
	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@OneToOne
	@JoinColumn(name="practiceid")
	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
	}
	
}
