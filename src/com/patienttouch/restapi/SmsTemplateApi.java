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
import com.patienttouch.api.SmsTemplateImpl;


@Path("/smstemplate")
public class SmsTemplateApi {
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String addSmsTemplate(String content) {
		String response = null;
        try {
        	response = SmsTemplateImpl.addTemplate(content);
        }
        catch (Throwable t) {
            t.printStackTrace();
            
        }
        return response;
	}
	
	@GET
	@Path("/get")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getSmsTemplate(
			@QueryParam("practice") @DefaultValue("") String practiceName,
			@QueryParam("type") @DefaultValue("") String smsTemplateType,
			@QueryParam("name")  @DefaultValue("") String smsTemplateName) {
		String response = null;
        try {
        	response = SmsTemplateImpl.getTemplate(practiceName, smsTemplateType, smsTemplateName);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getbyid")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getSmsTemplateById(
			@QueryParam("smstemplateid") @DefaultValue("") String smstemplateid) {
		String response = null;
        try {
        	response = SmsTemplateImpl.getTemplateById(smstemplateid);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return response;
	}
	
	@GET
	@Path("/getall")
	@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
	public String getAllSmsTemplates(
			@QueryParam("practice") @DefaultValue("") String practiceName) {
		String response = null;
        try {
        	response = SmsTemplateImpl.getTemplate(practiceName, "", "");
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
	public String updateSmsTemplate(String content) {
		String response = null;
		try {
			response = SmsTemplateImpl.updateTemplate(content);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
	
	@DELETE
	@Path("/delete/{templateid}")
	@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) 
	public String deleteSmsTemplate(@PathParam("templateid") @DefaultValue("") String templateid) {
		String response = null;
		try {
			response = SmsTemplateImpl.deleteTemplate(templateid);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return response;
	}
}