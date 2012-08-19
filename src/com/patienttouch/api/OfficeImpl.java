package com.patienttouch.api;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.OfficeInfo;
import com.patienttouch.client.PracticeInfo;

import com.patienttouch.client.Response;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsTemplates;

public class OfficeImpl {

	/**
	 * This method is used to add 1 or more office to an existing practice.
	 * 
	 * @param jsonRequest
	 * @return jason response indicating if the operation was successful
	 */

	public static String addOffice(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		OfficeInfo request;
		Session session;

		// Parse jsonString
		request = gson.fromJson(jsonRequest, OfficeInfo.class);

		
		// Validate Mandatory Parameters
		int ret = OfficeImpl.validateParameter(request);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		session = DbOperations.getDbSession();

		// Get practice
		Practice p = PracticeImpl.getPractice(session,
				request.getPracticeName(), false, false, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		List<Map<String, Object>> offices = request.getOffices();
		for (Map<String, Object> off : offices) {
			// Build Office object
			Office office = new Office();

			office.setName(request.getOfficeName(off));
			office.setStreetAddress1(request.getOfficeStreetAddress1(off));
			office.setStreetAddress2(request.getOfficeStreetAddress2(off));
			office.setCity(request.getOfficeCity(off));
			office.setCityShort(request.getOfficeCityShort(off));
			office.setState(request.getOfficeSate(off));
			office.setZip(request.getOfficeZip(off));
			office.setPhone(request.getOfficePhone(off));
			office.setMainOffice(false);
			office.setPractice(p);

			ret = DbOperations.addToDb(null, office);
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
	 * This method is used to update details of an existing office. Fetch
	 * existing office details using officeid or name and then update required
	 * information to save to database.
	 * 
	 * @param jsonRequest
	 * @return
	 */

	public static String updateOffice(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		OfficeInfo request;
		Session session = null;
		String officeid, query;
		Office o;

		// Parse jsonString
		request = gson.fromJson(jsonRequest, OfficeInfo.class);

		// Validate Mandatory Parameters
		int ret = OfficeImpl.validateParameter(request);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		try {
			// Get practice
			Practice p = PracticeImpl.getPractice(null,
					request.getPracticeName(), false, false, false, false);
			if (p == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			List<Map<String, Object>> offices = request.getOffices();

			session = DbOperations.getDbSession();

			session.getTransaction().begin();

			for (Map<String, Object> off : offices) {
				officeid = request.getOfficeId(off);
				// officeid is not null and is numeric
				if (officeid != null && !officeid.isEmpty()
						&& officeid.matches("\\d+")) {
					query = "from Office where officeid = " + officeid
							+ " and practiceid = " + p.getPracticeid();
				} else {
					if (session != null && session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					response.getResults().setStatus(
							ErrorCodes.MANDATORY_PARAMETER_OFFICE_ID_MISSING);
					response.getResults().setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_OFFICE_ID_MISSING]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				o = OfficeImpl.getOfficeInfo(session, query, false, false);
				if (o == null) {
					if (session != null && session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					response.getResults().setStatus(
							ErrorCodes.INVALID_OFFICE_ID);
					response.getResults().setEdesc(
							ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				// Build Office object
				o.setName(request.getOfficeName(off));
				o.setStreetAddress1(request.getOfficeStreetAddress1(off));
				o.setStreetAddress2(request.getOfficeStreetAddress2(off));
				o.setCity(request.getOfficeCity(off));
				o.setCityShort(request.getOfficeCityShort(off));
				o.setState(request.getOfficeSate(off));
				o.setZip(request.getOfficeZip(off));
				o.setPhone(request.getOfficePhone(off));
				o.setPractice(p);
				
				session.update(o);
			}
			session.getTransaction().commit();
			response.getResults().setStatus(ErrorCodes.SUCCESS);
		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			response.getResults().setStatus(
					ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}

	/*
	 * This api is used to fetch all offices of a practice or info for a
	 * specific office. If only practiceName is specified all the offices for a
	 * practice will be returned else details of only 1 office will be returned
	 */
	public static String getOffice(String practiceName, String officeName) {
		int resultCount = 0;
		Gson gson = new Gson();
		Response response = new Response();

		// parameter validation
		if (practiceName == null || practiceName.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Lookup db to fetch practice details
		Practice p = PracticeImpl.getPractice(null, practiceName, true, false,
				false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		OfficeInfo office = new OfficeInfo();
		office.setPracticeName(p.getName());

		List<Office> offices = p.getOffice();
		for (Office o : offices) {
			// if office name is specified then we need to return only
			if (officeName != null && !officeName.isEmpty()) {
				if (!officeName.equalsIgnoreCase(o.getName())) {
					continue;
				}
			}
			office.setOfficeInfo(Integer.toString(o.getOfficeid()),
					o.getName(), o.getStreetAddress1(), o.getStreetAddress2(),
					o.getCity(), o.getCityShort(), o.getState(), o.getZip(),
					o.getPhone(), o.isMainOffice());
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(office.getRequest());

		return gson.toJson(response);
	}

	/*
	 * This api is used to fetch all offices of a practice or info for a
	 * specific office. If only practiceName is specified all the offices for a
	 * practice will be returned else details of only 1 office will be returned
	 */
	public static String getOfficeById(String officeid) {
		int resultCount = 0;
		Gson gson = new Gson();
		Response response = new Response();

		if (officeid == null || officeid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!officeid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Office where officeid = " + officeid + "";
		Office o = OfficeImpl.getOfficeInfo(null, query, true, false);
		if (o == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		OfficeInfo office = new OfficeInfo();
		office.setPracticeName(o.getPractice().getName());
		office.setOfficeInfo(Integer.toString(o.getOfficeid()), o.getName(),
				o.getStreetAddress1(), o.getStreetAddress2(), o.getCity(),
				o.getCityShort(), o.getState(), o.getZip(), o.getPhone(), o.isMainOffice());
		resultCount++;

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(office.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This method is used to delete a office based on officeid
	 * 
	 * @param officeid
	 *            - this parameter uniquely identifies a office in the system
	 * @return - jason response to specify the status of the api request
	 */

	public static String deleteOffice(String officeid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();

		if (officeid == null || officeid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!officeid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Office where officeid = " + officeid + "";
		Office office = OfficeImpl.getOfficeInfo(null, query, false, false);
		if (office == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		Session session = DbOperations.getDbSession();

		ret = DbOperations.deleteFromDb(session, office);
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
	 * This parameter is used to validate parameters in office request
	 * @param request
	 * @return
	 */
	
	private static int validateParameter(OfficeInfo request) {

		List<Map<String, Object>> offices = request.getOffices();
		if (offices == null) {
			return ErrorCodes.MANDATORY_PARAMETER_OFFICE_MISSING;
		}

		for (Map<String, Object> office : offices) {
			String name = request.getOfficeName(office);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_OFFICE_NAME_MISSING;
			}

			String oStreet1 = request.getOfficeStreetAddress1(office);
			if (oStreet1 == null || oStreet1.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_STREET_ADDRESS1_MISSING;
			}

			String oCity = request.getOfficeCity(office);
			if (oCity == null || oCity.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_CITY_MISSING;
			}

			oCity = request.getOfficeCityShort(office);
			if (oCity == null || oCity.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_CITY_SHORT_MISSING;
			}

			String oState = request.getOfficeSate(office);
			if (oState == null || oState.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_STATE_MISSING;
			}

			String oZip = request.getOfficeZip(office);
			if (oZip == null || oZip.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_ZIP_MISSING;
			}

			String oPhone = request.getOfficePhone(office);
			if (oPhone == null || oPhone.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_PHONE_MISSING;
			}
		}
		return ErrorCodes.SUCCESS;
	}

	/**
	 * This method is used to fetch office information from the database.
	 * 
	 * @param dbsession
	 * @param query
	 * @param fetchPractice
	 * @param fetchAllDoctor
	 * @return
	 */

	public static Office getOfficeInfo(Session dbsession, String query,
			boolean fetchPractice, boolean fetchAllDoctor) {
		boolean startTransaction = false;
		Session session = null;
		Office office = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				startTransaction = true;
				session = DbOperations.getDbSession();
			}

			if (startTransaction) {
				session.getTransaction().begin();
			}

			Query q = session.createQuery(query);

			List<Office> results = q.list();

			if (results == null || results.isEmpty()) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Office o : results) {
				System.out.println(o.getName() + "[" + o.getOfficeid() + "]");
				if (fetchPractice) {
					o.getPractice();
				}

				if (fetchAllDoctor) {
					List<Doctor> doctors = o.getDoctor();
					for (Doctor d : doctors) {
						break;
					}
				}

				office = o;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return office;
	}
}
