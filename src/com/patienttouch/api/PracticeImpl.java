package com.patienttouch.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.PracticeInfo;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.Billing;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;
import com.patienttouch.hibernate.UserRole;

public class PracticeImpl {

	/**
	 * This method is used to add a new practice to the database. This method is
	 * invoked by the RESTful Api.
	 * 
	 * @param jsonRequest
	 *            - contains all the parameters required when we sign up a new
	 *            practice
	 * @return - json response that contains the status of the request. The
	 *         status parameter in the response specifies if addition was
	 *         successful or the request failed
	 * 
	 */
	public static String addPractice(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		PracticeInfo request;
		Session session = null;
		try {
			// Parse jsonString
			request = gson.fromJson(jsonRequest, PracticeInfo.class);

			// Validate Mandatory Parameters
			int ret = PracticeImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			ret = PracticeImpl.validateLoginInfoParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			Practice practice = new Practice();

			Map<String, Object> contact, ho, billing, loginInfo;
			contact = request.getContact();
			ho = request.getHeadOffice();
			billing = request.getBillingInfo();
			loginInfo = request.getLoginInfo();

			practice.setName(request.getPracticeName());
			practice.setEmail(request.getContactEmail(contact));
			practice.setPhone(request.getContactPhone(contact));

			Office o = new Office();
			o.setName(request.getHeadOfficeName(ho));
			o.setStreetAddress1(request.getHeadOfficeStreetAddress1(ho));
			o.setStreetAddress2(request.getHeadOfficeStreetAddress2(ho));
			o.setCity(request.getHeadOfficeCity(ho));
			o.setCityShort(request.getHeadOfficeCityShort(ho));
			o.setState(request.getHeadOfficeSate(ho));
			o.setZip(request.getHeadOfficeZip(ho));
			o.setPhone(request.getHeadOfficePhone(ho));
			o.setMainOffice(true);
			o.setPractice(practice);

			List<Office> offices = new ArrayList<Office>();
			offices.add(o);

			User u = new User();
			u.setFirstName(request.getContactFirstName(contact));
			u.setLastName(request.getContactLastName(contact));
			u.setUsername(request.getLoginUserName(loginInfo));
			u.setPassword(request.getLoginPassword(loginInfo));
			u.setRole(UserRole.ADMIN);
			u.setDel(0);
			u.setPractice(practice);
			List<User> users = new ArrayList<User>();
			users.add(u);

			Billing b = new Billing();
			b.setContactFirstName(request.getContactFirstName(contact));
			b.setContactLastName(request.getContactLastName(contact));
			b.setStreetAddress1(request.getBillingStreetAddress1(billing));
			b.setStreetAddress2(request.getBillingStreetAddress2(billing));
			b.setCity(request.getBillingCity(billing));
			b.setState(request.getBillingSate(billing));
			b.setZip(request.getBillingZip(billing));
			b.setPractice(practice);

			practice.setOffice(offices);
			practice.setUser(users);
			practice.setBilling(b);

			session = DbOperations.getDbSession();

			ret = DbOperations.addToDb(session, practice);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

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
	 * This method is used to update information in an existing practice.
	 * 
	 * @param jsonRequest
	 *            - contains all the parameters required when we sign up a new
	 *            practice
	 * @return - json response that contains the status of the request. The
	 *         status parameter in the response specifies if addition was
	 *         successful or the request failed
	 */
	public static String updatePractice(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		PracticeInfo request;
		Session session = null;
		try {
			// Parse jsonString
			request = gson.fromJson(jsonRequest, PracticeInfo.class);

			// Validate Mandatory Parameters
			int ret = PracticeImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			Practice practice = PracticeImpl.getPractice(null,
					request.getPracticeName(), true, false, false, false);
			if (practice == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			Map<String, Object> contact, ho, billing, loginInfo;
			contact = request.getContact();
			ho = request.getHeadOffice();
			billing = request.getBillingInfo();

			// Update practice Object
			practice.setEmail(request.getContactEmail(contact));
			practice.setPhone(request.getContactPhone(contact));

			// Find head office
			Office headOffice = null;
			List<Office> offices = practice.getOffice();
			for (Office off : offices) {
				if (off.isMainOffice()) {
					headOffice = off;
					break;
				}
			}

			// This case should never happen
			if (headOffice == null) {
				// head office information missing. Update new headoffice
				headOffice = new Office();
				offices.add(headOffice);
			}

			headOffice.setName(request.getHeadOfficeName(ho));
			headOffice.setStreetAddress1(request
					.getHeadOfficeStreetAddress1(ho));
			headOffice.setStreetAddress2(request
					.getHeadOfficeStreetAddress2(ho));
			headOffice.setCity(request.getHeadOfficeCity(ho));
			headOffice.setCityShort(request.getHeadOfficeCityShort(ho));
			headOffice.setState(request.getHeadOfficeSate(ho));
			headOffice.setZip(request.getHeadOfficeZip(ho));
			headOffice.setPhone(request.getHeadOfficePhone(ho));
			headOffice.setMainOffice(true);
			headOffice.setPractice(practice);

			Billing b = practice.getBilling();
			b.setContactFirstName(request.getContactFirstName(contact));
			b.setContactLastName(request.getContactLastName(contact));
			b.setStreetAddress1(request.getBillingStreetAddress1(billing));
			b.setStreetAddress2(request.getBillingStreetAddress2(billing));
			b.setCity(request.getBillingCity(billing));
			b.setState(request.getBillingSate(billing));
			b.setZip(request.getBillingZip(billing));
			b.setPractice(practice);

			practice.setOffice(offices);
			practice.setBilling(b);

			session = DbOperations.getDbSession();

			ret = DbOperations.updateDb(session, practice);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

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
	 * This method is used to delete a practice based on practiceid
	 * 
	 * @param practiceid
	 *            - this parameter uniquely identifies a practice in the system
	 * @return - jason response to specify the status of the api request
	 */

	public static String deletePractice(String practiceid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();

		if (practiceid == null || practiceid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!practiceid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Practice where practiceid = " + practiceid
				+ " and del = 0";
		Practice practice = PracticeImpl.getPracticeInfo(null, query, false,
				false, false);
		if (practice == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		Session session = DbOperations.getDbSession();

		ret = DbOperations.deleteFromDb(session, practice);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);

		return gson.toJson(response);
	}

	/**
	 * This method is used to fetch all the details for a practice
	 * 
	 * @param dbsession
	 *            - This parameter contains a valid hibernate session that used
	 *            to execute db query
	 * @param practicename
	 *            - This parameter contains the practice whose details we need
	 *            to fetch from the database
	 * @param fetchAllOffice
	 *            - This is a boolean parameter used to specify if we need to
	 *            fetch details of offices with in a practice
	 * @param fetchAllDoctor
	 *            - This is a boolean parameter used to specify if we need to
	 *            fetch details of doctors with in a practice
	 * @param fetchAllSmsTemplate
	 *            - This is a boolean parameter used to specify if we need to
	 *            fetch all the sms templates setup for a practice
	 * @return
	 */

	public static Practice getPractice(Session dbsession, String practicename,
			boolean fetchAllOffice, boolean fetchAllDoctor,
			boolean fetchAllSmsTemplate, boolean fetchAllUsers) {
		Session session = null;
		Practice practiceInfo = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				session = DbOperations.getDbSession();
			}

			session.getTransaction().begin();

			Query q = session
					.createQuery("from Practice where name= :practicename and del =0");
			q.setString("practicename", practicename);
			List<Practice> results = q.list();

			if (results == null || results.isEmpty()) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Practice p : results) {
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				if (fetchAllOffice) {
					List<Office> offices = p.getOffice();
					for (Office o : offices) {
						break;
					}
				}

				if (fetchAllDoctor) {
					List<Doctor> doctors = p.getDoctor();
					for (Doctor d : doctors) {
						break;
					}
				}

				if (fetchAllSmsTemplate) {
					List<SmsTemplates> smstemplates = p.getSmstemplate();
					for (SmsTemplates s : smstemplates) {
						break;
					}
				}
				
				if (fetchAllUsers) {
					List<User> users = p.getUser();
					for (User u : users) {
						break;
					}
				}
				practiceInfo = p;
			}

			session.getTransaction().commit();

		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			t.printStackTrace();
		}

		return practiceInfo;
	}

	/**
	 * This method is used to fetch all the practice setup in the system.
	 * 
	 * @param dbsession
	 *            - Valid database session
	 * @return - jsonresponse that contains details for all the practice
	 */

	public static String getAllPractice() {
		int resultCount = 0;
		Session session = null;
		Practice practice = null;
		PracticeInfo practiceInfo;
		Gson gson = new Gson();
		Response response = new Response();

		List<Map<String, Object>> allPractice = new ArrayList<Map<String, Object>>();

		try {
			session = DbOperations.getDbSession();

			session.getTransaction().begin();

			Query q = session.createQuery("from Practice where del =0");

			List<Practice> results = q.list();

			if ((results == null) || (results.isEmpty())) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			Office headOffice = null;
			User contact = null;
			Billing billing = null;

			for (Practice p : results) {
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				List<Office> offices = p.getOffice();
				for (Office o : offices) {
					System.out.println("Office name [" + o.getName() + "]");
					if (o.isMainOffice()) {
						headOffice = o;
						break;
					}
				}

				List<User> users = p.getUser();
				for (User u : users) {
					System.out.println("User name [" + u.getUsername() + "]");
					if (u.getRole() == UserRole.ADMIN) {
						contact = u;
						break;
					}
				}

				practice = p;
				billing = practice.getBilling();

				practiceInfo = new PracticeInfo();

				practiceInfo.setPracticeId(Integer.toString(practice
						.getPracticeid()));
				practiceInfo.setPracticeName(practice.getName());
				practiceInfo.setContact(billing.getContactFirstName(),
						billing.getContactLastName(), practice.getEmail(),
						practice.getPhone());
				practiceInfo.setLoginInfo(contact.getUsername(),
						contact.getPassword(), contact.getPassword());
				practiceInfo.setHeadOffice(headOffice.getName(),
						headOffice.getStreetAddress1(),
						headOffice.getStreetAddress2(), headOffice.getCity(),
						headOffice.getCityShort(), headOffice.getState(),
						headOffice.getZip(), headOffice.getPhone());
				practiceInfo.setBillingInfo(billing.getStreetAddress1(),
						billing.getStreetAddress2(), billing.getCity(),
						billing.getState(), billing.getZip());

				allPractice.add(practiceInfo.getRequest());
				resultCount++;
			}

			session.getTransaction().commit();

			//if (allPractice.isEmpty()) {	
				//return null;
			//}
			response.getResults().setStatus(0);
			response.getResults().setNumResults(resultCount);
			if (!allPractice.isEmpty()) {
				response.getResults().setResults(allPractice);
			}

		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			t.printStackTrace();
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(resultCount);
		}

		return gson.toJson(response);
	}

	/**
	 * This method is used to validate if all mandatory parameters are received
	 * in the request
	 * 
	 * @param request
	 *            - java object created after parsing input json request
	 * @return SUCCESS if parameter validation was successful.
	 */
	private static int validateParameter(PracticeInfo request) {

		Map<String, Object> contact = request.getContact();
		if (contact == null) {
			return ErrorCodes.MANDATORY_PARAMETER_CONTACT_INFO_MISSING;
		}

		String fn = request.getContactFirstName(contact);
		if (fn == null || fn.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_FIRST_NAME_MISSING;
		}

		String ln = request.getContactLastName(contact);
		if (ln == null || ln.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_LAST_NAME_MISSING;
		}

		String email = request.getContactEmail(contact);
		if (email == null || email.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_EMAIL_MISSING;
		}
		String phone = request.getContactPhone(contact);
		if (phone == null || phone.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PHONE_MISSING;
		}

		Map<String, Object> ho = request.getHeadOffice();
		if (ho == null) {
			return ErrorCodes.MANDATORY_PARAMETER_OFFICE_MISSING;
		}
		String hoName = request.getHeadOfficeName(ho);
		if (hoName == null || hoName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_OFFICE_NAME_MISSING;
		}

		String hoStreet1 = request.getHeadOfficeStreetAddress1(ho);
		if (hoStreet1 == null || hoStreet1.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_STREET_ADDRESS1_MISSING;
		}

		String hoCity = request.getHeadOfficeCity(ho);
		if (hoCity == null || hoCity.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_CITY_MISSING;
		}

		hoCity = request.getHeadOfficeCityShort(ho);
		if (hoCity == null || hoCity.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_CITY_SHORT_MISSING;
		}

		String hoState = request.getHeadOfficeSate(ho);
		if (hoState == null || hoState.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_STATE_MISSING;
		}

		String hoZip = request.getHeadOfficeZip(ho);
		if (hoZip == null || hoZip.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_ZIP_MISSING;
		}

		String hoPhone = request.getHeadOfficePhone(ho);
		if (hoPhone == null || hoPhone.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PHONE_MISSING;
		}

		Map<String, Object> billing = request.getBillingInfo();
		if (billing == null) {
			return ErrorCodes.MANDATORY_PARAMETER_BILLING_INFO_MISSING;
		}

		String billingStreet1 = request.getBillingStreetAddress1(billing);
		if (billingStreet1 == null || billingStreet1.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_BILLING_STREET_ADDRESS1_MISSING;
		}

		String billingCity = request.getBillingCity(billing);
		if (billingCity == null || billingCity.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_BILLING_CITY_MISSING;
		}

		String billingState = request.getBillingSate(billing);
		if (billingState == null || billingState.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_BILLING_STATE_MISSING;
		}

		String billingZip = request.getHeadOfficeZip(billing);
		if (billingZip == null || billingZip.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_BILLING_ZIP_MISSING;
		}

		return ErrorCodes.SUCCESS;
	}

	/**
	 * This method is validate login info parameters
	 * 
	 * @param request
	 *            - This is parameter contains input request parameters
	 * @return SUCCESS - if validation passes else error
	 */

	private static int validateLoginInfoParameter(PracticeInfo request) {
		Map<String, Object> login = request.getLoginInfo();
		if (login == null) {
			return ErrorCodes.MANDATORY_LOGIN_INFO_MISSING;
		}

		String user = request.getLoginUserName(login);
		if (user == null || user.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_USERNAME_MISSING;
		}

		String password = request.getLoginPassword(login);
		if (password == null || password.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PASSWORD_MISSING;
		}

		String confirmPassword = request.getLoginConfirmPassword(login);
		if (confirmPassword == null || confirmPassword.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_CONFIRM_PASSWORD_MISSING;
		}

		if (!password.equals(confirmPassword)) {
			return ErrorCodes.PASSWORD_MISMATCH;
		}

		return ErrorCodes.SUCCESS;
	}

	public static String getPracticeByName(String practiceName) {
		Gson gson = new Gson();
		Response response = new Response();

		if ((practiceName == null) || (practiceName.isEmpty())) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING);
			response.getResults()
					.setEdesc(
							com.patienttouch.client.ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		PracticeInfo p = dbLookup(practiceName, 0);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults()
					.setEdesc(
							com.patienttouch.client.ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		response.getResults().setStatus(0);
		response.getResults().setNumResults(1);
		response.getResults().setResults(p.getRequest());

		return gson.toJson(response);
	}

	public static String getPracticeById(String practiceid) {
		int pid;
		Gson gson = new Gson();
		Response response = new Response();

		if ((practiceid == null) || (practiceid.isEmpty())) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_PRACTICE_ID_MISSING);
			response.getResults()
					.setEdesc(
							com.patienttouch.client.ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PRACTICE_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		// Verify if
		if (practiceid.matches("\\d+")) {
			pid = Integer.parseInt(practiceid);
		} else {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_ID);
			response.getResults()
					.setEdesc(
							com.patienttouch.client.ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		PracticeInfo p = dbLookup(null, pid);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults()
					.setEdesc(
							com.patienttouch.client.ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		response.getResults().setStatus(0);
		response.getResults().setNumResults(1);
		response.getResults().setResults(p.getRequest());

		return gson.toJson(response);
	}

	private static PracticeInfo dbLookup(String practicename, int practiceid) {
		Session session = null;

		Practice practiceInfo = null;
		PracticeInfo req = null;

		try {

			session = DbOperations.getDbSession();

			session.getTransaction().begin();

			Query q;

			if (practicename != null && !practicename.isEmpty()) {
				q = session
						.createQuery("from Practice where name= :practicename and del =0");
				q.setString("practicename", practicename);
			} else {

				q = session
						.createQuery("from Practice where id= :practiceid and del =0");
				q.setInteger("practiceid", practiceid);
			}

			List<Practice> results = q.list();
			Office headOffice = null;
			User contact = null;
			Billing billing = null;

			if ((results == null) || (results.isEmpty())) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Practice p : results) {
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				List<Office> offices = p.getOffice();
				for (Office o : offices) {
					System.out.println("Office name [" + o.getName() + "]");
					if (o.isMainOffice()) {
						headOffice = o;
						break;
					}
				}

				List<User> users = p.getUser();
				for (User u : users) {
					System.out.println("User name [" + u.getUsername() + "]");
					if (u.getRole() == UserRole.ADMIN) {
						contact = u;
						break;
					}
				}

				practiceInfo = p;
			}

			billing = practiceInfo.getBilling();

			req = new PracticeInfo();

			req.setPracticeId(Integer.toString(practiceInfo.getPracticeid()));
			req.setPracticeName(practiceInfo.getName());
			req.setContact(billing.getContactFirstName(),
					billing.getContactLastName(), practiceInfo.getEmail(),
					practiceInfo.getPhone());
			req.setLoginInfo(contact.getUsername(), contact.getPassword(),
					contact.getPassword());
			req.setHeadOffice(headOffice.getName(),
					headOffice.getStreetAddress1(),
					headOffice.getStreetAddress2(), headOffice.getCity(),
					headOffice.getCityShort(), headOffice.getState(),
					headOffice.getZip(), headOffice.getPhone());
			req.setBillingInfo(billing.getStreetAddress1(),
					billing.getStreetAddress2(), billing.getCity(),
					billing.getState(), billing.getZip());

			session.getTransaction().commit();
		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			t.printStackTrace();
		}

		return req;
	}

	/**
	 * This is a reusable function to which we pass the query to be executed to
	 * fetch practice details
	 * 
	 * @param dbsession
	 * @param query
	 *            - This parameter contains the query to be executed
	 * @param fetchAllOffice
	 *            - This parameter is used to specify if we need to fetch all
	 *            offices for a practice
	 * @param fetchAllDoctor
	 *            - This parameter is used to specify if we need to fetch all
	 *            doctors for a practice
	 * @param fetchAllSmsTemplate
	 *            - This parameter is used to specify if we need to fetch all
	 *            sms templated for a practice
	 * @return Practice object with all the information
	 * 
	 */
	private static Practice getPracticeInfo(Session dbsession, String query,
			boolean fetchAllOffice, boolean fetchAllDoctor,
			boolean fetchAllSmsTemplate) {
		Session session = null;
		Practice practiceInfo = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				session = DbOperations.getDbSession();
			}

			session.getTransaction().begin();

			Query q = session.createQuery(query);

			List<Practice> results = q.list();

			if (results == null || results.isEmpty()) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Practice p : results) {
				System.out.println(p.getName() + "[" + p.getPracticeid() + "]");
				if (fetchAllOffice) {
					List<Office> offices = p.getOffice();
					for (Office o : offices) {
						break;
					}
				}

				if (fetchAllDoctor) {
					List<Doctor> doctors = p.getDoctor();
					for (Doctor d : doctors) {
						break;
					}
				}

				if (fetchAllSmsTemplate) {
					List<SmsTemplates> smstemplates = p.getSmstemplate();
					for (SmsTemplates s : smstemplates) {
						break;
					}
				}
				practiceInfo = p;
			}

			session.getTransaction().commit();

		} catch (Throwable t) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			t.printStackTrace();
		}

		return practiceInfo;
	}

}
