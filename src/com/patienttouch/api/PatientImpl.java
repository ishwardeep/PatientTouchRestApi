package com.patienttouch.api;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.PatientInfo;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Patient;

public class PatientImpl {

	/**
	 * This utility method is used add a patient.
	 * 
	 * @param phone
	 *            - Phone number for the patient
	 * @param firstName
	 *            - Patient first name
	 * @param lastName
	 *            - Patient last name
	 * @return
	 */
	public static String addPatient(String phone, String firstName,
			String lastName) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();
		Patient patient;

		try {
			if (phone == null || phone.isEmpty()) {
				response.getResults()
						.setStatus(
								ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			if (!phone.matches("\\d+")) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PATIENT_PHONENUMBER);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.INVALID_PATIENT_PHONENUMBER]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			if (firstName == null || firstName.isEmpty()) {
				response.getResults()
						.setStatus(
								ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_FIRST_NAME_MISSING);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_FIRST_NAME_MISSING]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			if (lastName == null || lastName.isEmpty()) {
				response.getResults()
						.setStatus(
								ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_LAST_NAME_MISSING);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_LAST_NAME_MISSING]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			
			patient = new Patient();
			patient.setPhone(phone);
			patient.setFirstName(firstName);
			patient.setLastName(lastName);
			
			ret = DbOperations.addToDb(null, patient);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
		        response.getResults().setEdesc(ErrorCodes.edesc[ret]);
		        response.getResults().setNumResults(0);
                return gson.toJson(response);
			}
			
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(0);
			
		} catch (Throwable t) {
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}

	/**
	 * This method is used to fetch details of a patient using phonenumber as a
	 * key. It is possible that multiple patients have the same number (like
	 * members of the same family)
	 * 
	 * @param phonenumber
	 *            - This parameter contains phonenumber for which we need to
	 *            lookup in the database
	 * @return
	 */
	public static String getByPhoneNumber(String phonenumber) {
		int resultCount = 0;
		Gson gson = new Gson();
		Response response = new Response();
		String query;
		PatientInfo patientInfo;

		if (phonenumber == null || phonenumber.isEmpty()) {
			response.getResults()
					.setStatus(
							ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_APPOINTEE_PHONENUMBER_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!phonenumber.matches("\\d+")) {
			response.getResults().setStatus(
					ErrorCodes.INVALID_PATIENT_PHONENUMBER);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PATIENT_PHONENUMBER]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		query = "from Patient where phone = " + phonenumber;

		List<Patient> patients = PatientImpl.getPatientDetails(null, query);
		if (patients == null) {
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		patientInfo = new PatientInfo();

		for (Patient patient : patients) {
			patientInfo.setPatientInfo(
					Integer.toString(patient.getPatientid()),
					patient.getFirstName(), patient.getLastName(),
					patient.getPhone());
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(patientInfo.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This is a utility method used to fetch details of a patient
	 * 
	 * @param dbsession
	 *            - this parameter contains database session
	 * @param query
	 *            - this parameter contains database query to be executed
	 * @return
	 */
	public static List<Patient> getPatientDetails(Session dbsession,
			String query) {
		boolean startTransaction = false;
		Session session = null;
		List<Patient> results = null;

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

			results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null
					&& session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return results;
	}
}
