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


@Path("/patient")
public class PatientApi {
	
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
			@QueryParam("officename") @DefaultValue("") String officeName,
			@QueryParam("nickname")  @DefaultValue("") String nickName,
			@QueryParam("firstname")  @DefaultValue("") String firstName,
			@QueryParam("lastname")  @DefaultValue("") String lastName) {
		String response = null;
        try {
        	//response = DoctorImpl.getDoctor(practiceName, officeName, nickName, firstName, lastName);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}

	@GET
	@Path("/getbyphone")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getDoctorById(
			@QueryParam("phonenumber") @DefaultValue("") String phonenumber) {
		String response = null;
        try {
        	//response = DoctorImpl.getDoctorById(doctorid);
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
        	//response = DoctorImpl.getDoctor(practiceName, "", "", "", "");
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
	@Path("/delete/{doctorid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deleteDoctor(@PathParam("doctorid") @DefaultValue("") String doctorid) {
		String response = null;
		try {
			//response = DoctorImpl.deleteDoctor(doctorid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}
