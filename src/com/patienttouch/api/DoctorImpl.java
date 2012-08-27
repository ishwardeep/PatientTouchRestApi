package com.patienttouch.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.DoctorInfo;
import com.patienttouch.client.OfficeInfo;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;

public class DoctorImpl {
	/**
	 * This method is used to add a doctor to a practice
	 * @param jsonRequest - All the information required to add a doctor is received in a jason request
	 * @return jason response with status of api execution
	 */
	public static String addDoctor(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		Session session;
		DoctorInfo request;

		// Parse jsonString
		request = gson.fromJson(jsonRequest, DoctorInfo.class);

		HashSet<String> inputOffices = new HashSet<String>();
		// Validate Mandatory Parameters
		int ret = DoctorImpl.validateParameter(request, inputOffices);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		session = DbOperations.getDbSession();

		// Get practice
		Practice p = PracticeImpl.getPractice(session,
				request.getPracticeName(), true, false, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Get list of office
		List<Office> offices = p.getOffice();
		List<Office> validOfficeListForDoctor = new ArrayList<Office>();

		for (Office o : offices) {
			if (inputOffices.contains(o.getName())) {
				validOfficeListForDoctor.add(o);
			}
		}

		if (validOfficeListForDoctor.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.ADD_DOCTOR_INVALID_OFFICE_NAMES);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.ADD_DOCTOR_INVALID_OFFICE_NAMES]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		List<Map<String, Object>> doctors = request.getDoctors();
		for (Map<String, Object> doc : doctors) {
			// Build Doctor object
			Doctor doctor = new Doctor();

			doctor.setFirstName(request.getFirstName(doc));
			doctor.setLastName(request.getLastName(doc));
			doctor.setNickName(request.getNickName(doc));
			doctor.setDel(0);
			doctor.setPractice(p);
			doctor.setOffice(validOfficeListForDoctor);

			
			ret = DbOperations.addToDb(null, doctor);
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
	 * This method is used to update details of an existing doctor
	 * 
	 * @param jsonRequest
	 * @return
	 */
	public static String updateDoctor(String jsonRequest) {
		Gson gson = new Gson();
		Response response = new Response();
		String query, doctorid;
		Session session = null;
		DoctorInfo request;

		// Parse jsonString
		request = gson.fromJson(jsonRequest, DoctorInfo.class);

		HashSet<String> inputOffices = new HashSet<String>();
		// Validate Mandatory Parameters
		int ret = DoctorImpl.validateParameter(request, inputOffices);
		if (ret != ErrorCodes.SUCCESS) {
			response.getResults().setStatus(ret);
			response.getResults().setEdesc(ErrorCodes.edesc[ret]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Get practice
		Practice p = PracticeImpl.getPractice(null, request.getPracticeName(),
				true, false, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		// Get list of office
		List<Office> offices = p.getOffice();
		List<Office> validOfficeListForDoctor = new ArrayList<Office>();

		for (Office o : offices) {
			System.out.println("Search Office [" + o.getName() + "]");
			if (inputOffices.contains(o.getName())) {
				validOfficeListForDoctor.add(o);
			}
		}

		if (validOfficeListForDoctor.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.ADD_DOCTOR_INVALID_OFFICE_NAMES);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.ADD_DOCTOR_INVALID_OFFICE_NAMES]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		List<Map<String, Object>> doctors = request.getDoctors();

		try {
			session = DbOperations.getDbSession();
			session.getTransaction().begin();
			
			for (Map<String, Object> doc : doctors) {
				doctorid = request.getDoctorid(doc);
				// doctorid is not null and is numeric
				if (doctorid != null && !doctorid.isEmpty()
						&& doctorid.matches("\\d+")) {
					query = "from Doctor where doctorid = " + doctorid
							+ " and practiceid = " + p.getPracticeid();
				} else {
					if (session != null && session.getTransaction().isActive()) {
						session.getTransaction().rollback();
					}
					response.getResults().setStatus(
							ErrorCodes.MANDATORY_PARAMETER_DOCTOR_ID_MISSING);
					response.getResults().setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_DOCTOR_ID_MISSING]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				// fetch doctor info
				Doctor doctor = DoctorImpl.getDoctorDetails(session, query);
				if (doctor == null) {
					response.getResults().setStatus(
							ErrorCodes.INVALID_DOCTOR_ID);
					response.getResults().setEdesc(
							ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				doctor.setFirstName(request.getFirstName(doc));
				doctor.setLastName(request.getLastName(doc));
				doctor.setNickName(request.getNickName(doc));
				doctor.setDel(0);
				doctor.setPractice(p);
				doctor.setOffice(validOfficeListForDoctor);

				//session = DbOperations.getDbSession();
				session.update(doctor);
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

			return gson.toJson(response);
		}

		return gson.toJson(response);
	}

	/**
	 * This method is used to delete a doctor based on doctorid
	 * 
	 * @param doctorid
	 *            - This parameter uniquely identifies a doctor in the system
	 * @return - jason response to specify the status of the api request
	 */

	public static String deleteDoctor(String doctorid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;
		
		if (doctorid == null || doctorid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!doctorid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Doctor where doctorid =" + doctorid;

		Doctor doc = DoctorImpl.getDoctorDetails(null, query);
		if (doc == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		session = DbOperations.getDbSession();
		ret = DbOperations.deleteFromDb(session, doc);
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
	 * This api is used to return all doctors for a practice (only practice name
	 * doctor are specified) or info about a specific doctor.
	 * @param practiceName - The practice to which a doctor belongs
	 * @param officeName - Office for which we need to fetch doctors
	 * @param nickName - nick name of a doctor
	 * @param firstName - First name of doctor
	 * @param lastName - last name of doctor
	 * @return
	 */
	public static String getDoctor(String practiceName, String officeName,
			String nickName, String firstName, String lastName) {
		int resultCount = 0;
		Gson gson = new Gson();
		boolean fetchDoctorsForPractice = false, fetchDoctorsForPracticeOffice = false;
		boolean fetchDetailsForOfficeDoctor = false, fetchDetailsForDoctor = false;
		Response response = new Response();
		Session session = null;
		HashSet<String> uniqDocs = new HashSet<String>();
		List<Doctor> tmpDoctors, doctors = null;
		List<Office> offices = null;

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

		if ((officeName == null || officeName.isEmpty())
				&& (nickName == null || nickName.isEmpty())
				&& (firstName == null || firstName.isEmpty())
				&& (lastName == null || lastName.isEmpty())) {
			// Fetch all doctors for a practice
			fetchDoctorsForPractice = true;
		} else if (officeName != null && !officeName.isEmpty()
				&& (nickName == null || nickName.isEmpty())
				&& (firstName == null || firstName.isEmpty())
				&& (lastName == null || lastName.isEmpty())) {
			// Fetch all doctors for <practice, Office>
			fetchDoctorsForPracticeOffice = true;
		} else if ((officeName != null && !officeName.isEmpty())
				&& ((nickName != null && !nickName.isEmpty())
						|| (firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName
						.isEmpty()))) {
			fetchDetailsForOfficeDoctor = true;
		} else if ((officeName == null || officeName.isEmpty())
				&& (nickName != null && !nickName.isEmpty())
				|| (firstName != null && !firstName.isEmpty())
				|| (lastName != null && !lastName.isEmpty())) {
			fetchDetailsForDoctor = true;
		}

		session = DbOperations.getDbSession();

		// Lookup db to fetch practice details
		Practice p = PracticeImpl.getPractice(session, practiceName, false,
				fetchDoctorsForPractice, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (fetchDoctorsForPracticeOffice || fetchDetailsForOfficeDoctor) {
			//Office name may contain multiple offices delimited by ~ (tilde). We need to fetch
			//Doctors for all these offices
			String officeNames[] = officeName.split("~");
			
			for (String oName : officeNames) {
				Office office = DoctorImpl.getDoctorsForOffice(null,
					p.getPracticeid(), oName);
				if (office == null) {
					response.getResults().setStatus(ErrorCodes.INVALID_OFFICE_NAME);
					response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_OFFICE_NAME]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}

				if (doctors == null) {
					doctors = new ArrayList<Doctor>();
				}
				
				tmpDoctors = office.getDoctor();
				for (Doctor d : tmpDoctors) {
					if (!uniqDocs.contains(Integer.toString(d.getDoctorid()))) {
						uniqDocs.add(Integer.toString(d.getDoctorid()));
						doctors.add(d);
					}
				}
			}
		} else if (fetchDetailsForDoctor) {

			Doctor doc = DoctorImpl.getDoctorDetails(null, p.getPracticeid(),
					firstName, lastName, nickName);
			if (doc == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			doctors = new ArrayList<Doctor>();
			doctors.add(doc);
			offices = doc.getOffice();
		} else {
			doctors = p.getDoctor();
		}

		DoctorInfo doctor = new DoctorInfo();
		doctor.setPracticeName(p.getName());
		
		if (fetchDoctorsForPracticeOffice || fetchDetailsForOfficeDoctor) {
			for (String o : officeName.split("~")) {
				doctor.setOfficeName(o);
			}
		} else if (fetchDetailsForDoctor) {
			for (Office off : offices) {
				doctor.setOfficeName(off.getName());
			}
		}

		for (Doctor doc : doctors) {
			if (fetchDetailsForOfficeDoctor) {
				if (firstName != null && !firstName.isEmpty()) {
					if (!doc.getFirstName().equalsIgnoreCase(firstName)) {
						// first name does not match
						continue;
					}
				}

				if (lastName != null && !lastName.isEmpty()) {
					if (!doc.getLastName().equalsIgnoreCase(lastName)) {
						// last name does not match
						continue;
					}
				}

				if (nickName != null && !nickName.isEmpty()) {
					if (!doc.getNickName().equalsIgnoreCase(nickName)) {
						// nick name does not match
						continue;
					}
				}
			}
			doctor.setDoctorInfo(Integer.toString(doc.getDoctorid()),
					doc.getFirstName(), doc.getLastName(), doc.getNickName());
			resultCount++;
			if (fetchDetailsForOfficeDoctor) {
				break;
			}
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(doctor.getRequest());

		return gson.toJson(response);

	}

	/**
	 * This api is used to return doctor details for doctorid
	 * 
	 * @param doctorid
	 * @return
	 */

	public static String getDoctorById(String doctorid) {
		int resultCount = 0;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;
		Practice p;
		List<Office> offices = null;

		// parameter validation
		if (doctorid == null || doctorid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		// doctor id must be numeric
		if (!doctorid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Doctor where doctorid =" + doctorid;

		Doctor doc = DoctorImpl.getDoctorDetails(null, query);
		if (doc == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_DOCTOR_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_DOCTOR_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		offices = doc.getOffice();
		p = doc.getPractice();

		DoctorInfo doctor = new DoctorInfo();
		doctor.setPracticeName(p.getName());
		for (Office off : offices) {
			doctor.setOfficeName(off.getName());
		}

		doctor.setDoctorInfo(Integer.toString(doc.getDoctorid()),
				doc.getFirstName(), doc.getLastName(), doc.getNickName());
		resultCount++;

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(doctor.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This method is used to validate that all the mandatory parameters have been received in the request
	 * @param request
	 * @param inputOffices - This parameter is a Hashset that contains all the unique office names received in the request
	 * @return
	 */
	private static int validateParameter(DoctorInfo request,
			HashSet<String> inputOffices) {
		String name;

		String practiceName = request.getPracticeName();
		if (practiceName == null || practiceName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING;
		}

		List<Map<String, Object>> offices = request.getOffices();
		if (offices == null) {
			return ErrorCodes.MANDATORY_PARAMETER_OFFICE_MISSING;
		}

		for (Map<String, Object> office : offices) {
			name = request.getOfficeName(office);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_OFFICE_NAME_MISSING;
			}
			// Add office name to HashSet so that we can verify these later
			inputOffices.add(name);
		}

		List<Map<String, Object>> doctors = request.getDoctors();
		if (doctors == null) {
			return ErrorCodes.MANDATORY_PARAMETER_DOCTOR_MISSING;
		}

		for (Map<String, Object> doctor : doctors) {
			name = request.getFirstName(doctor);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_DOCTOR_FIRST_NAME_MISSING;
			}

			name = request.getLastName(doctor);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_DOCTOR_LAST_NAME_MISSING;
			}

			name = request.getNickName(doctor);
			if (name == null || name.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_DOCTOR_NICK_NAME_MISSING;
			}
		}

		return ErrorCodes.SUCCESS;
	}

	/**
	 * This method is used to fetch all doctors for an office
	 * @param dbsession - This parameter contains a handle to the database
	 * @param practiceid - This parameter has the internally generated id for a practice 
	 * @param officeName - This parameter contains office name for which we need to fetch doctors
	 * @return
	 */
	private static Office getDoctorsForOffice(Session dbsession, int practiceid,
			String officeName) {
		Session session = null;
		Office officeInfo = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				session = DbOperations.getDbSession();
			}

			session.getTransaction().begin();

			Query q = session
					.createQuery("from Office where name= :officename and practiceid = :practiceid");
			q.setString("officename", officeName);
			q.setInteger("practiceid", practiceid);

			List<Office> results = q.list();

			if (results == null || results.isEmpty()) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Office off : results) {
				System.out.println(off.getName());

				List<Doctor> doctors = off.getDoctor();
				for (Doctor d : doctors) {
					break;
				}
				officeInfo = off;
			}
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return officeInfo;
	}

	/**
	 * This method is used to fetch details of a doctor based on practice id or first name or last name 
	 * or nick name
	 * 
	 * @param dbsession - This parameter contains handle to the database.
	 * @param practiceid - This parameter contains internally generated id for a practice
	 * @param firstName - This parameter contains the first name for doctor if we need to search for a doctor using first name
	 * @param lastName - This parameter contains the last name for doctor if we need to search for a doctor using lastname
	 * @param nickName - This parameter contains the nick name for doctor if we need to search for a doctor using nickname
	 * @return
	 */
	private static Doctor getDoctorDetails(Session dbsession, int practiceid,
			String firstName, String lastName, String nickName) {
		StringBuffer query = new StringBuffer(
				"from Doctor where practiceid = :practiceid ");
		Session session = null;
		Doctor docInfo = null;

		try {
			if (dbsession != null) {
				session = dbsession;
			} else {
				session = DbOperations.getDbSession();
			}

			if (firstName != null && !firstName.isEmpty()) {
				query.append("and firstName = :firstName ");
			}

			if (lastName != null && !lastName.isEmpty()) {
				query.append("and lastName = :lastName ");
			}

			if (nickName != null && !nickName.isEmpty()) {
				query.append("and nickName = :nickName ");
			}

			session.getTransaction().begin();

			Query q = session.createQuery(query.toString());
			q.setInteger("practiceid", practiceid);

			if (firstName != null && !firstName.isEmpty()) {
				q.setString("firstName", firstName);
			}

			if (lastName != null && !lastName.isEmpty()) {
				q.setString("lastName", lastName);
			}

			if (nickName != null && !nickName.isEmpty()) {
				q.setString("nickName", nickName);
			}

			List<Doctor> results = q.list();

			if (results == null || results.isEmpty()) {
				return null;
			}

			for (Doctor doc : results) {
				System.out.println(doc.getFirstName());

				List<Office> offices = doc.getOffice();
				for (Office o : offices) {
					break;
				}
				docInfo = doc;
			}
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return docInfo;
	}

	/**
	 * This is a utility method used to fetch details of a doctor based on input
	 * query
	 * 
	 * @param dbsession
	 *            - this parameter contains database session
	 * @param query
	 *            - this parameter contains database query to be executed
	 * @return
	 */
	public static Doctor getDoctorDetails(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		Doctor docInfo = null;

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

			List<Doctor> results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (Doctor doc : results) {
				System.out.println(doc.getFirstName());
				Practice p = doc.getPractice();
				List<Office> offices = doc.getOffice();
				for (Office o : offices) {
					break;
				}
				docInfo = doc;
			}
			if (startTransaction) {
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			if (startTransaction == true && session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			e.printStackTrace();
		}

		return docInfo;
	}
}
