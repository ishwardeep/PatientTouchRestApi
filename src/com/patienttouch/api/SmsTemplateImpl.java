package com.patienttouch.api;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.Response;
import com.patienttouch.client.SmsTemplateInfo;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsTemplateType;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.UserRole;

public class SmsTemplateImpl {
	/**
	 * This method is used to add Sms Template to a practice
	 * 
	 * @param jsonRequest
	 *            - This parameter contains a json formated request parameters.
	 * @return - The json formated response contains the status of api
	 *         invocation
	 */
	public static String addTemplate(String jsonRequest) {
		Gson gson = new Gson();
		String practiceName;
		Response response = new Response();
		Session session;
		UserRole role;
		Practice p = null;
		SmsTemplateInfo request;
	
		// Parse jsonString
		request = gson.fromJson(jsonRequest, SmsTemplateInfo.class);

		// Validate Mandatory Parameters
		int ret = SmsTemplateImpl.validateParameter(request);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Get practice
		practiceName = request.getPracticeName();
		if (practiceName != null && !practiceName.isEmpty()) {
			p = PracticeImpl.getPractice(null,
				request.getPracticeName(), false, false, false, false);
			if (p == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
		}
		
		List<Map<String, Object>> smstemplates = request.getSmsTemplates();
		for (Map<String, Object> template : smstemplates) {
			role = request.getTemplateRole(template);
			if ((p == null && role != UserRole.SUPERUSER) ||
					(p != null && role == UserRole.SUPERUSER)) {
				response.getResults().setStatus(ErrorCodes.SMS_TEMPLATE_ROLE_MISMATCH);
				response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SMS_TEMPLATE_ROLE_MISMATCH]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			// Build Doctor object
			SmsTemplates smsTmpl = new SmsTemplates();

			if (p != null) {
				smsTmpl.setPractice(p);
			}
			
			smsTmpl.setName(request.getTemplateName(template));
			smsTmpl.setType(request.getTemplateType(template));
			smsTmpl.setRole(role);
			smsTmpl.setMessage(request.getTemplateMessage(template));
			smsTmpl.setConfirmMessage(request
					.getTemplateConfirmMessage(template));
			smsTmpl.setRescheduleMessage(request
					.getTemplateReschduleMessage(template));
			smsTmpl.setDel(0);

			ret = DbOperations.addToDb(null, smsTmpl);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
		}
		response.getResults().setStatus(ErrorCodes.SUCCESS);

		return gson.toJson(response);
	}

	/**
	 * This method is used to update an existing Sms Template using template id
	 * as a key
	 * 
	 * @param jsonRequest
	 *            - This parameter contains a json formated request parameters.
	 * @return - The json formated response contains the status of api
	 *         invocation
	 */
	public static String updateTemplate(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;
		SmsTemplateInfo request;
		String query, templateid;

		// Parse jsonString
		request = gson.fromJson(jsonRequest, SmsTemplateInfo.class);

		// Validate Mandatory Parameters
		int ret = SmsTemplateImpl.validateParameter(request);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		try {
			session = DbOperations.getDbSession();

			// Get practice
			/*Practice p = PracticeImpl.getPractice(session,
					request.getPracticeName(), false, false, false, false);
			if (p == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}*/

			List<Map<String, Object>> smstemplates = request.getSmsTemplates();

			session = DbOperations.getDbSession();

			session.getTransaction().begin();

			for (Map<String, Object> template : smstemplates) {
				templateid = request.getTemplateId(template);
				// templateid is not null and is numeric
				if (templateid != null && !templateid.isEmpty()
						&& templateid.matches("\\d+")) {
					query = "from SmsTemplates where smstemplateid = "
							+ templateid;
					//+ " and practiceid = " + p.getPracticeid();
				} else {
					if (session != null && session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					response.getResults()
							.setStatus(
									ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING);
					response.getResults()
							.setEdesc(
									ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
				SmsTemplates smsTmpl = getTemplateInfo(session, query, true);
				if (smsTmpl == null) {
					if (session != null && session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					response.getResults().setStatus(
							ErrorCodes.INVALID_SMSTEMPLATE_ID);
					response.getResults()
							.setEdesc(
									ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				//smsTmpl.setPractice(p);
				smsTmpl.setName(request.getTemplateName(template));
				smsTmpl.setType(request.getTemplateType(template));
				smsTmpl.setRole(request.getTemplateRole(template));
				smsTmpl.setMessage(request.getTemplateMessage(template));
				smsTmpl.setConfirmMessage(request
						.getTemplateConfirmMessage(template));
				smsTmpl.setRescheduleMessage(request
						.getTemplateReschduleMessage(template));
				smsTmpl.setDel(0);

				session.update(smsTmpl);
			}
			session.getTransaction().commit();
			response.getResults().setStatus(ErrorCodes.SUCCESS);
		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		return gson.toJson(response);
	}

	/**
	 * This api is used to fetch all templates for a practice or all templates
	 * for <practice, type> or all templates for <practice, type, template name>
	 * 
	 * @param practiceName
	 *            - This parameter contains name of the practice for which we
	 *            need to fetch all the templates
	 * @param type
	 * @param templateName
	 * @return
	 */

	public static String getTemplate(String practiceName, String type,
			String templateName) {
		int resultCount = 0;
		Gson gson = new Gson();
		boolean typePresent = false, templatePresent = false;
		Practice p = null;
		SmsTemplateType templateType = null;
		Response response = new Response();
		Session session = null;
		List<SmsTemplates> templates = null;

		// PracticeName is no longer a mandatory parameter as default templates
		// are not associated with a Practice
		/*if (practiceName == null || practiceName.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}*/

		if (type != null && !type.isEmpty()) {
			if (type.equalsIgnoreCase("REMINDER")) {
				templateType = SmsTemplateType.REMINDER;
			} else if (type.equalsIgnoreCase("WAITLIST")) {
				templateType = SmsTemplateType.WAITLIST;
			} else {
				response.getResults().setStatus(
						ErrorCodes.INVALID_SMS_TEMPLATE_TYPE);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_SMS_TEMPLATE_TYPE]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			typePresent = true;
		}

		if (templateName != null && !templateName.isEmpty()) {
			templatePresent = true;
		}

		//Get all the default templates - whose owner is SUPERUSER 
		String query = "from SmsTemplates where role = 'SUPERUSER'";
		if (type != null && !type.isEmpty()) {
			query = query + " and type= '" + type + "'";
		}
				
		List<SmsTemplates> defaultTemplates = SmsTemplateImpl.getDefaultTemplateInfo(null, query);
		
		//Fetch all practice specific templates
		if (practiceName != null && !practiceName.isEmpty())
		{
			// Lookup db to fetch practice details
			p = PracticeImpl.getPractice(null, practiceName, false,
				false, true, false);
			if (p == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}	
			templates = p.getSmstemplate();
		}
		
		if (defaultTemplates != null) {
			if (templates == null) {
				templates = defaultTemplates;
			}
			else {
				templates.addAll(defaultTemplates);
			}
		}
		
		SmsTemplateInfo templateInfo = new SmsTemplateInfo();
		for (SmsTemplates tmpl : templates) {
			if (typePresent) {
				// Filter records that do not match template type
				if (tmpl.getType() != templateType)
					continue;
			}

			if (templatePresent) {
				// Filter that do not match template name
				if (!tmpl.getName().equalsIgnoreCase(templateName)) {
					continue;
				}
			}

			templateInfo.setSmsTemplateInfo(
					Integer.toString(tmpl.getSmstemplateid()), tmpl.getName(),
					tmpl.getType(), tmpl.getMessage(),
					tmpl.getConfirmMessage(), tmpl.getRescheduleMessage(),
					tmpl.getRole());

			resultCount++;

			//if (typePresent || templatePresent) {
				//break;
			//}
		}

		if (resultCount > 0) {
			if (p != null) {
				templateInfo.setPracticeName(p.getName());
			}
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(templateInfo.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This method is used to fetch template info based on template id
	 * 
	 * @param smstemplateid
	 * @return
	 */

	public static String getTemplateById(String smstemplateid) {
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		SmsTemplates tmpl;

		// parameter validation
		if (smstemplateid == null || smstemplateid.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Template id must be numeric
		if (!smstemplateid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		query = "from SmsTemplates where smstemplateid = " + smstemplateid;

		tmpl = SmsTemplateImpl.getTemplateInfo(null, query, true);
		if (tmpl == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		SmsTemplateInfo templateInfo = new SmsTemplateInfo();
		templateInfo.setSmsTemplateInfo(
				Integer.toString(tmpl.getSmstemplateid()), tmpl.getName(),
				tmpl.getType(), tmpl.getMessage(), tmpl.getConfirmMessage(),
				tmpl.getRescheduleMessage(), tmpl.getRole());

		resultCount++;

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(templateInfo.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This method is used to fetch template info based on template id
	 * 
	 * @param smstemplateid
	 * @return
	 */

	public static String deleteTemplate(String smstemplateid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		SmsTemplates tmpl;

		// parameter validation
		if (smstemplateid == null || smstemplateid.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_SMSTEMPLATE_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Template id must be numeric
		if (smstemplateid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		query = "from SmsTemplates where smstemplateid = " + smstemplateid;

		tmpl = SmsTemplateImpl.getTemplateInfo(null, query, true);
		if (tmpl == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_SMSTEMPLATE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_SMSTEMPLATE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		Session session = DbOperations.getDbSession();

		ret = DbOperations.deleteFromDb(session, tmpl);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(0);

		return gson.toJson(response);
	}

	/**
	 * This method is used to validate all the parameters in the json request
	 * 
	 * @param request
	 * @return
	 */

	private static int validateParameter(SmsTemplateInfo request) {
		String name, message, practiceName;
		SmsTemplateType type;
		UserRole role;

		/* Practice is no longer a mandatory parameter as when superuser logs in
		 * this parameter is not mandatory
		 * String practiceName = request.getPracticeName();
		if (practiceName == null || practiceName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING;
		}*/

		List<Map<String, Object>> templates = request.getSmsTemplates();
		if (templates == null) {
			return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_MISSING;
		}

		for (Map<String, Object> template : templates) {
			name = request.getTemplateName(template);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_NAME_MISSING;
			}

			type = request.getTemplateType(template);
			if (type == null) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_TYPE_MISSING;
			}

			role = request.getTemplateRole(template);
			if (role == null) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_ROLE_MISSING;
			}
			
			message = request.getTemplateMessage(template);
			if (message == null || message.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_TEMPLATE_MESSAGE_MISSING;
			}
		}

		return ErrorCodes.SUCCESS;
	}

	/**
	 * This method is internally used to fetch details of a specific template
	 * 
	 * @param dbsession
	 * @param query
	 * @param fetchPractice
	 * @return
	 */

	public static SmsTemplates getTemplateInfo(Session dbsession,
			String query, boolean fetchPractice) {
		boolean startTransaction = false;
		Session session = null;
		SmsTemplates template = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.beginTransaction();
			}
			Query q = session.createQuery(query);

			List<SmsTemplates> results = q.list();

			if (results == null || results.isEmpty()) {
				if (startTransaction && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (SmsTemplates t : results) {
				System.out.println(t.getName() + "[" + t.getSmstemplateid()
						+ "]");
				if (fetchPractice) {
					t.getPractice();
				}

				template = t;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return template;
	}
	
	/**
	 * This method is internally used to fetch details of a specific template
	 * 
	 * @param dbsession
	 * @param query
	 * @param fetchPractice
	 * @return
	 */

	public static List<SmsTemplates> getDefaultTemplateInfo(Session dbsession,
			String query) {
		boolean startTransaction = false;
		Session session = null;
		List<SmsTemplates> results = null;
		
		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.beginTransaction();
			}
			Query q = session.createQuery(query);

			results = q.list();

			if (results == null || results.isEmpty()) {
				if (startTransaction && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
}
