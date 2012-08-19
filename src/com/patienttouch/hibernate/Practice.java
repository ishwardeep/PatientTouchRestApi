package com.patienttouch.hibernate;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="Practice")
public class Practice {
	private int practiceid;
	private String name;
	private String email;
	private String phone;
	private int del;
	List<Office> office;
	List<User> user;
	List<SmsTemplates> smstemplate;
	List<Doctor> doctor;
	Billing billing;
	
	@Id
	@GeneratedValue
	public int getPracticeid() {
		return practiceid;
	}
	public void setPracticeid(int practiceid) {
		this.practiceid = practiceid;
	}
	
	@Column(nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(nullable=false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(nullable=false)
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Column(nullable=false)
	public int getDel() {
		return del;
	}
	public void setDel(int del) {
		this.del = del;
	}
	
    @OneToMany(targetEntity=Office.class, mappedBy="practice",
    		cascade=CascadeType.ALL ,fetch=FetchType.LAZY)
	public List<Office> getOffice() {
		return office;
	}
	public void setOffice(List<Office> office) {
		this.office = office;
	}
	
	@OneToMany(targetEntity=User.class, mappedBy="practice",
    		cascade=CascadeType.ALL ,fetch=FetchType.LAZY)
	public List<User> getUser() {
		return user;
	}
	public void setUser(List<User> user) {
		this.user = user;
	}
	
	@OneToMany(targetEntity=SmsTemplates.class, mappedBy="practice",
    		cascade=CascadeType.ALL ,fetch=FetchType.LAZY)
	public List<SmsTemplates> getSmstemplate() {
		return smstemplate;
	}
	public void setSmstemplate(List<SmsTemplates> smstemplate) {
		this.smstemplate = smstemplate;
	}
	
	@OneToMany(targetEntity=Doctor.class, mappedBy="practice",
    		cascade=CascadeType.ALL ,fetch=FetchType.LAZY)
	public List<Doctor> getDoctor() {
		return doctor;
	}
	public void setDoctor(List<Doctor> doctor) {
		this.doctor = doctor;
	}
	
	@OneToOne(targetEntity=Billing.class, mappedBy="practice", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public Billing getBilling() {
		return billing;
	}
	public void setBilling(Billing billing) {
		this.billing = billing;
	}
}
