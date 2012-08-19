package com.patienttouch.restapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/send_message")
public class SendMsgApi {
	
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED})
	@Produces("application/xml")
	public String addPractice(
			@FormParam("username") @DefaultValue("") String username,
	        @FormParam("password") @DefaultValue("") String password,
	        @FormParam("trigger_id") @DefaultValue("") String triggerid,
	        @FormParam("phone_number") @DefaultValue("false") String phonenumber,
	        @FormParam("message") @DefaultValue("") String message) {
		String response = null;
        try {
        	System.out.println("U [" + username + "] P [" + password + "] T [" + triggerid + "] P [" + phonenumber +
        			"] Message [" + message + "]");
        	if (username.isEmpty() || password.isEmpty() || triggerid.isEmpty() || phonenumber.isEmpty() ||
        			message.isEmpty()) {
        		response = 	"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
        					"<errorNotification>" +
        					"<message>Incorrect user or password.</message>" +
        					"</errorNotification>";
        	}
        	else {
        	response = 
        			"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
        					"<successNotification>" +
        					"<message>Message Queued</message>" +
        					"<phoneNumber>" + phonenumber + "</phoneNumber>" +
        					"<message>" + message + "</message>" +
        					"<triggerId>" + triggerid + "</triggerId>" +
        					"</successNotification>";
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;
	}
}