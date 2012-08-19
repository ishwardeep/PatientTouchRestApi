package com.patienttouch.restapi;

import javax.ws.rs.Consumes;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.patienttouch.api.ReminderCampaignImpl;
import com.patienttouch.api.WaitlistCampaignImpl;


@Path("/campaign")
public class CampaignApi {
	
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
			@QueryParam("officeid") @DefaultValue("") String officeid,
			@QueryParam("doctorid")  @DefaultValue("") String doctorid) 
	{
		String response = null;
        try {
        	//response = ReminderCampaignImpl.(practiceName, officeid, doctorid);
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
			@QueryParam("practice") @DefaultValue("") String practiceName
	) 
	{
		String response = null;
        try {
        	response = WaitlistCampaignImpl.getWaitlistStatus(practiceName);
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
        	response = WaitlistCampaignImpl.getWaitlistAppointmentInfoStatus(campaignid);
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
        	response = WaitlistCampaignImpl.getWaitlistAppointmentDetailStatus(appointmentinfoid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
		
}