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

import com.patienttouch.api.PracticeImpl;

@Path("/practice")
public class PracticeApi {

	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String addPractice(String content) {
		String response = null;
		try {
			response = PracticeImpl.addPractice(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}

	@GET
	@Path("/get")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getPractice(
			@QueryParam("practice") @DefaultValue("") String practiceName) {
		String response = null;
		try {
			response = PracticeImpl.getPracticeByName(practiceName);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@GET
	@Path("/getbyid")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getPracticeById(
			@QueryParam("practiceid") @DefaultValue("") String practiceId) {
		String response = null;
		try {
			response = PracticeImpl.getPracticeById(practiceId);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@GET
	@Path("/getall")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getAllPractice() {
		String response = null;
		try {
			response = PracticeImpl.getAllPractice();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}

	@PUT
	@Path("/update")
	@Consumes("application/json")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String updatePractice(String content) {
		String response = null;
		try {
			response = PracticeImpl.updatePractice(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}

	@DELETE
	@Path("/delete/{practiceid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deletePractice(@PathParam("practiceid") @DefaultValue("") String practiceid) {
		String response = null;
		try {
			response = PracticeImpl.deletePractice(practiceid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}
