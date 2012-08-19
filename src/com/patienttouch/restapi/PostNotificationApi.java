package com.patienttouch.restapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/postnotification")
public class PostNotificationApi {
	
	@POST
	@Consumes("application/xml")
	@Produces({MediaType.APPLICATION_XML})
	public String addPractice(String content) {
		String response = null;
        try {
        	System.out.println("Post Notification from 3ci server. content [" + content + "]");
        	response = "OK";
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
}