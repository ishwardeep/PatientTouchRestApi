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

import com.patienttouch.api.UserImpl;


@Path("/user")
public class UserApi {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String addUser(String content) {
		String response = null;
        try {
        	response = UserImpl.addUser(content);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/get")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getUser(
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
	@Path("/getbyid")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getUserById(
			@QueryParam("userid") @DefaultValue("") String userid) {
		String response = null;
        try {
        	response = UserImpl.getUserById(userid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getall")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getAllUsers(
			@QueryParam("practice") @DefaultValue("") String practiceName) {
		String response = null;
        try {
        	response = UserImpl.getAllUsers(practiceName);
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
	public String updateUser(String content) {
		String response = null;
		try {
			response = UserImpl.updateUser(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@DELETE
	@Path("/delete/{userid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deleteDoctor(@PathParam("userid") @DefaultValue("") String userid) {
		String response = null;
		try {
			response = UserImpl.deleteUser(userid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}