package com.patienttouch.restapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.patienttouch.api.WaitlistImpl;


@Path("/waitlist")
public class WaitlistApi {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String addDoctor(String content) {
		String response = null;
        try {
        	//response = DoctorImpl.addDoctor(content);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/get")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getDoctor(
			@QueryParam("practice") @DefaultValue("") String practiceName,
			@QueryParam("officeid") @DefaultValue("") String officeid,
			@QueryParam("doctorid")  @DefaultValue("") String doctorid) 
	{
		String response = null;
        try {
        	response = WaitlistImpl.getWaitlist(practiceName, officeid, doctorid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}

	@GET
	@Path("/getbyid")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getDoctorById(
			@QueryParam("waitlistid") @DefaultValue("") String waitlistid) {
		String response = null;
        try {
        	response = WaitlistImpl.getWaitlistById(waitlistid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getall")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getAllDoctors(
			@QueryParam("practice") @DefaultValue("") String practiceName) {
		String response = null;
        try {
        	response = WaitlistImpl.getWaitlist(practiceName, "", "");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@PUT
	@Path("/update")
	@Consumes("application/json")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String updateDoctor(String content) {
		String response = null;
		try {
			//response = DoctorImpl.updateDoctor(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@DELETE
	@Path("/delete/{waitlistid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deleteDoctor(@PathParam("waitlistid") @DefaultValue("") String waitlistid) {
		String response = null;
		try {
			response = WaitlistImpl.deleteFromWaitlist(waitlistid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}
