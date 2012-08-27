package com.patienttouch.restapi;

import javax.ws.rs.Consumes;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.patienttouch.api.CampaignImpl;
import com.patienttouch.api.CampaignManager;
import com.patienttouch.api.ReminderCampaignImpl;
import com.patienttouch.api.WaitlistCampaignImpl;

@Path("/campaign")
public class CampaignApi {

	static {
		//Trying to fetch this instance only to create a schedule thread for Campaign Analyzer
		CampaignManager.getInstance();
	}
	
	@POST
	@Path("/setupreminder")
	@Consumes("application/json")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String addDoctor(String content) {
		String response = null;
        try {
        	response = ReminderCampaignImpl.setupReminderCampaign(content);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getreminderstatus")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getDoctor(
			@QueryParam("practice") @DefaultValue("") String practiceName,
			@QueryParam("userid") @DefaultValue("") String userId,
			@QueryParam("appointmentStartDate") @DefaultValue("") String appointmentStartDate,
			@QueryParam("appointmentEndDate")  @DefaultValue("") String appointmentEndDate) 
	{
		String response = null;
        try {
        	response = ReminderCampaignImpl.getReminderStatus(practiceName, userId, appointmentStartDate, appointmentEndDate);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@POST
	@Path("/setupwaitlist")
	@Consumes("application/json")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String setUpWaitlistCampaign(String content) {
		String response = null;
        try {
        	response = WaitlistCampaignImpl.setupWaitlistCampaign(content);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getwaitliststatus")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getWaitlistStatus(
			@QueryParam("practice") @DefaultValue("") String practiceName,
			@QueryParam("userid") @DefaultValue("") String userId,
			@QueryParam("campaignStartDate") @DefaultValue("") String campaignStartDate,
			@QueryParam("campaignEndDate")  @DefaultValue("") String campaignEndDate
	) 
	{
		String response = null;
        try {
        	response = WaitlistCampaignImpl.getWaitlistStatus(practiceName, userId, campaignStartDate, campaignEndDate);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}

	@GET
	@Path("/getcampaigndetail")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getCampaignDetail(
			@QueryParam("campaignid") @DefaultValue("") String campaignid) {
		String response = null;
        try {
        	response = CampaignImpl.getCampaignAppointmentInfoStatus(campaignid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getappointmentdetail")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getAppointmentDetail(
			@QueryParam("appointmentinfoid") @DefaultValue("") String appointmentinfoid) {
		String response = null;
        try {
        	response = CampaignImpl.getCampaignAppointmentDetailStatus(appointmentinfoid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
		
}