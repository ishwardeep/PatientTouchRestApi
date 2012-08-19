package com.patienttouch.hibernate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


@Entity
@Table(name="SmsTemplates")
public class SmsTemplates {
    private int smstemplateid;
    private String name;
    private String message;
    private String confirmMessage;
    private String rescheduleMessage;
    private int del;
    private Practice practice;
    private SmsTemplateType type;
    private UserRole role;
    
    @Id
    @GeneratedValue
	public int getSmstemplateid() {
		return smstemplateid;
	}
	public void setSmstemplateid(int smstemplateid) {
		this.smstemplateid = smstemplateid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getConfirmMessage() {
		return confirmMessage;
	}
	public void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}
	public String getRescheduleMessage() {
		return rescheduleMessage;
	}
	public void setRescheduleMessage(String rescheduleMessage) {
		this.rescheduleMessage = rescheduleMessage;
	}
	public int getDel() {
		return del;
	}
	public void setDel(int del) {
		this.del = del;
	}
	
	@ManyToOne
	@JoinColumn(name="practiceid")
	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
	}
	
	@Enumerated(EnumType.STRING)
	public SmsTemplateType getType() {
		return type;
	}
	public void setType(SmsTemplateType type) {
		this.type = type;
	}
	
	@Enumerated(EnumType.STRING)
	public UserRole getRole() {
		return role;
	}
	
	public void setRole(UserRole role) {
		this.role = role;
	}

}
