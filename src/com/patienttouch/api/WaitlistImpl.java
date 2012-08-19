package com.patienttouch.api;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.Response;
import com.patienttouch.client.WaitlistInfo;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.Waitlist;

public class WaitlistImpl {
	/**
	 * This api is used to return waitlist for a practice or <practice, office> or 
	 * <practice, office, doctor) or info about a specific doctor.
	 * @param practiceName - The practice to which a doctor belongs
	 * @param officeid - Officeid for which we need to fetch waitlist
	 * @param doctorid - doctorid for which we need to fetch doctorid
	 * @return
	 */
	public static String getWaitlist(String practiceName, String officeid,
			String doctorid) {
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		Session session = null;
		List<Waitlist> waitlist;
		
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
		Practice p = PracticeImpl.getPractice(session, practiceName, false,
						false, false, false);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);
			
			return gson.toJson(response);
		}
				
		query = "from Waitlist where practiceid =" + p.getPracticeid();
		if (officeid != null && !officeid.isEmpty()) {
			query = query + " and officeid =" + officeid;
		}
		
		if (doctorid != null && !doctorid.isEmpty()) {
			query = query + " and doctorid =" + doctorid;
		}
		
		query = query + " order by priority desc";
		
		waitlist = WaitlistImpl.getWaitlist(null, query);
		if (waitlist == null) {
			//Empty waitlist
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		
		WaitlistInfo waitlistInfo = new WaitlistInfo();
		waitlistInfo.setPractice(practiceName);
		
		for(Waitlist wl : waitlist) {
			waitlistInfo.setOffice(wl.getOffice().getName());
			waitlistInfo.setDoctor(wl.getDoctor().getNickName());
			waitlistInfo.setWaitlistInfo(Integer.toString(wl.getWaitlistid()), Integer.toString(wl.getPatient().getPatientid()), 
						wl.getPatient().getPhone(), wl.getPatient().getFirstName(), wl.getPatient().getLastName());
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(waitlistInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to fetch waitlist info by waitlistid
	 * @param waitlistid
	 * @return
	 */
	
	public static String getWaitlistById(String waitlistid) 
	{
		int resultCount = 0;
		Gson gson = new Gson();
		String query;
		Response response = new Response();
		List<Waitlist> waitlist;
						
		query = "from Waitlist where waitlistid =" + waitlistid;
		query = query + " order by priority desc";
		
		waitlist = WaitlistImpl.getWaitlist(null, query);
		if (waitlist == null) {
			//Empty waitlist
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		
		WaitlistInfo waitlistInfo = new WaitlistInfo();

		for(Waitlist wl : waitlist) {
			waitlistInfo.setPractice(wl.getPractice().getName());
			waitlistInfo.setOffice(wl.getOffice().getName());
			waitlistInfo.setDoctor(wl.getDoctor().getNickName());
			waitlistInfo.setWaitlistInfo(Integer.toString(wl.getWaitlistid()), Integer.toString(wl.getPatient().getPatientid()), 
						wl.getPatient().getPhone(), wl.getPatient().getFirstName(), wl.getPatient().getLastName());
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(waitlistInfo.getRequest());

		return gson.toJson(response);

	}
	
	/**
	 * This method is used to delete a waitlist based on waitlistid
	 * 
	 * @param waitlistid
	 *            - This parameter uniquely identifies a waitlist entry in the system
	 * @return - jason response to specify the status of the api request
	 */

	public static String deleteFromWaitlist(String waitlistid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;
		
		if (waitlistid == null || waitlistid.isEmpty()) {
			response.getResults().setStatus(ErrorCodes.MANDATORY_PARAMETER_WAITLIST_ID_MISSING);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_WAITLIST_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!waitlistid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_WAITLIST_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_WAITLIST_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from Waitlist where waitlistid =" + waitlistid;

		List<Waitlist> waitlist = WaitlistImpl.getWaitlist(null, query);
		if (waitlist == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_WAITLIST_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_WAITLIST_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		
		Waitlist wl = waitlist.get(0);
		
		session = DbOperations.getDbSession();
		ret = DbOperations.deleteFromDb(session, wl);
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
	 * This method is used to fetch details for an appointment
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	public static List<Waitlist> getWaitlist(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		List<Waitlist> results = null;
		
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
