package com.patienttouch.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="Campaign")
public class Campaign {
	private int campaignid;
	private String name;
	private CampaignType type;
	private Practice practice;
	private SmsTemplates smstemplate;
	private String message;
	private String confirmMessage;
	private String rescheduleMessage;
	private boolean followUpCampaign;
	private Date scheduleTime;
	private CampaignStatus status;
	private Date lastUpdateTime;
	private List<AppointmentInfo> appointmentInfo;
	
	@Id
	@GeneratedValue
	public int getCampaignid() {
		return campaignid;
	}
	public void setCampaignid(int campaignid) {
		this.campaignid = campaignid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Enumerated(EnumType.STRING)
	public CampaignType getType() {
		return type;
	}
	public void setType(CampaignType type) {
		this.type = type;
	}
	
	@ManyToOne
	@JoinColumn(name="practiceid")
	public Practice getPractice() {
		return practice;
	}
	public void setPractice(Practice practice) {
		this.practice = practice;
	}
	
	@ManyToOne
	@JoinColumn(name="smstemplateid")
	public SmsTemplates getSmstemplate() {
		return smstemplate;
	}
	public void setSmstemplate(SmsTemplates smstemplate) {
		this.smstemplate = smstemplate;
	}
	
	public boolean isFollowUpCampaign() {
		return followUpCampaign;
	}
	public void setFollowUpCampaign(boolean followUpCampaign) {
		this.followUpCampaign = followUpCampaign;
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
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}
	
	@Enumerated(EnumType.STRING)
	public CampaignStatus getStatus() {
		return status;
	}
	public void setStatus(CampaignStatus status) {
		this.status = status;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@OneToMany(targetEntity=AppointmentInfo.class, mappedBy="campaign",
    		cascade=CascadeType.ALL ,fetch=FetchType.LAZY)
	public List<AppointmentInfo> getAppointmentInfo() {
		return appointmentInfo;
	}
	public void setAppointmentInfo(List<AppointmentInfo> appointmentInfo) {
		this.appointmentInfo = appointmentInfo;
	}
	
}
