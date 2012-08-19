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

import com.patienttouch.api.OfficeImpl;
import com.patienttouch.api.PracticeImpl;

@Path("/office")
public class OfficeApi {
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String addOffice(String content) {
		String response = null;
		try {
			response = OfficeImpl.addOffice(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@GET
	@Path("/get")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getOffice(
			@QueryParam("practice") @DefaultValue("") String practiceName,
			@QueryParam("officename") @DefaultValue("") String officeName) {
		String response = null;
        try {
        	response = OfficeImpl.getOffice(practiceName, officeName);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getbyid")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getOfficeById(
			@QueryParam("officeid") @DefaultValue("") String officeid) {
		String response = null;
        try {
        	response = OfficeImpl.getOfficeById(officeid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getall")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getAllOffices(
			@QueryParam("practice") @DefaultValue("") String practiceName) {
		String response = null;
        try {
        	response = OfficeImpl.getOffice(practiceName, "");
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
	public String updateOffice(String content) {
		String response = null;
		try {
			response = OfficeImpl.updateOffice(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@DELETE
	@Path("/delete/{officeid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deleteOffice(@PathParam("officeid") @DefaultValue("") String officeid) {
		String response = null;
		try {
			response = OfficeImpl.deleteOffice(officeid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}
