package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patienttouch.hibernate.AppointmentStatus;
import com.patienttouch.hibernate.CampaignStatus;
import com.patienttouch.hibernate.SmsStatus;

public class CampaignInfo extends Request {
	
	public String getPracticeName() {
		return (String) this.request.get("practice");
	}
	
	public void setPracticeName(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}
	
	public List<Map<String,Object>> getWaitlistCampaigns() {
		return (ArrayList<Map<String, Object>>) this.request.get("waitlistCampaign");
	}
	
	public String getCampaignId(Map<String,Object> waitlist) {
		return (String) waitlist.get("campaignid");
	}
	
	public String getCampaignName(Map<String,Object> waitlist) {
		return (String) waitlist.get("campaignName");
	}
	
	public String getOfficeId(Map<String,Object> waitlist) {
		return (String) waitlist.get("officeid");
	}
	
	public String getOfficeName(Map<String,Object> waitlist) {
		return (String) waitlist.get("office");
	}
	
	public String getDoctorId(Map<String,Object> waitlist) {
		return (String) waitlist.get("doctorid");
	}
	
	public String getDoctorName(Map<String,Object> waitlist) {
		return (String) waitlist.get("doctor");
	}
	
	public String getAppointmentDate(Map<String,Object> waitlist) {
		return (String) waitlist.get("appointmentDate");
	}
	
	public String getAppointmentTime(Map<String,Object> waitlist) {
		return (String) waitlist.get("appointmentTime");
	}
	
	public void setCampaignInfo(String campaignid, String campaignName, String officeid, String office, 
			String doctorid, String doctor, String appDate, String appTime, CampaignStatus status) {
		List<Map<String,Object>> campaignList;
		Map<String, Object> campaign = new HashMap<String, Object>();
		
		campaignList = (ArrayList<Map<String, Object>>) this.request.get("waitlistCampaign");
		if (campaignList == null) {
			campaignList = new ArrayList<Map<String, Object>>();
			this.request.put("waitlistCampaign", campaignList);
		}
		campaignList.add(campaign);
		
		if (campaignid != null && !campaignid.isEmpty()) {
			campaign.put("campaignid", campaignid);
		}
		
		if (officeid != null && !officeid.isEmpty()) {
			campaign.put("officeid", officeid);
		}
		
		if (doctorid != null && !doctorid.isEmpty()) {
			campaign.put("doctorid", doctorid);
		}
		campaign.put("campaignName", campaignName);
		campaign.put("office", office);
		campaign.put("doctor", doctor);
		campaign.put("appointmentDate", appDate);
		campaign.put("appointmentTime", appTime);	
		campaign.put("status", status);
	}
	
	public void setCampaignAppointmentInfo(String campaignid, String campaignName, String officeid, String office, 
			String doctorid, String doctor, String appDate, String appTime, String appointmentInfoid, String appointeeFirstName, 
			String appointeeLastName, AppointmentStatus status) {
		List<Map<String,Object>> campaignList;
		Map<String, Object> campaign = new HashMap<String, Object>();
		
		campaignList = (ArrayList<Map<String, Object>>) this.request.get("waitlistCampaign");
		if (campaignList == null) {
			campaignList = new ArrayList<Map<String, Object>>();
			this.request.put("waitlistCampaign", campaignList);
		}
		campaignList.add(campaign);
		
		if (campaignid != null && !campaignid.isEmpty()) {
			campaign.put("campaignid", campaignid);
		}
		
		if (officeid != null && !officeid.isEmpty()) {
			campaign.put("officeid", officeid);
		}
		
		if (doctorid != null && !doctorid.isEmpty()) {
			campaign.put("doctorid", doctorid);
		}
		
		if (appointmentInfoid != null && !appointmentInfoid.isEmpty()) {
			campaign.put("appointmentinfoid", appointmentInfoid);
		}
		
		campaign.put("campaignName", campaignName);
		campaign.put("office", office);
		campaign.put("doctor", doctor);
		campaign.put("appointmentDate", appDate);
		campaign.put("appointmentTime", appTime);
		campaign.put("appointeeFirstName", appointeeFirstName);
		campaign.put("appointeeLastName", appointeeLastName);
		campaign.put("status", status);
	}
	
	public void setAppointmentSmsInfo(String campaignid, String appointmentInfoid, String smsid,
			String smsText, String smsTimestamp, String phoneNumber, SmsStatus status) {
		List<Map<String,Object>> campaignList;
		Map<String, Object> campaign = new HashMap<String, Object>();
		
		campaignList = (ArrayList<Map<String, Object>>) this.request.get("appointmentInfoDetail");
		if (campaignList == null) {
			campaignList = new ArrayList<Map<String, Object>>();
			this.request.put("appointmentInfoDetail", campaignList);
		}
		campaignList.add(campaign);
		
		if (campaignid != null && !campaignid.isEmpty()) {
			campaign.put("campaignid", campaignid);
		}
		
		if (appointmentInfoid != null && !appointmentInfoid.isEmpty()) {
			campaign.put("appointmentinfoid", appointmentInfoid);
		}
			
		campaign.put("smsid", smsid);
		campaign.put("smsText", smsText);
		campaign.put("smsTimestamp", smsTimestamp);
		campaign.put("phone", phoneNumber);
		campaign.put("status", status);
	}
}
