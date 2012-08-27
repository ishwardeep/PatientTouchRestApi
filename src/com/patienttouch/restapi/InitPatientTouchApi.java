package com.patienttouch.restapi;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.patienttouch.api.CampaignManager;


@Path("/init")
public class InitPatientTouchApi {
	
	@GET
	@Produces({MediaType.TEXT_HTML})
	public String initApplication() 
	{
		String response = null;
        try {
        	CampaignManager.getInstance();
        	response = "Ok";
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
}
