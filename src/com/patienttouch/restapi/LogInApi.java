package com.patienttouch.restapi;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.patienttouch.api.LogIn;

@Path("/login")
public class LogInApi {
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String login(
			 @FormParam("username") @DefaultValue("") String user,
	         @FormParam("password") @DefaultValue("") String password,
	         @FormParam("debug") @DefaultValue("false") String debug
			) {
		String response = null;
        try {
        	response = LogIn.login(user, password);
            if (debug.equalsIgnoreCase("true")) {
                response = "<html><body><h1>" + response + "</h1></body></html>";
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
}
