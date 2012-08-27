package com.patienttouch.api;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;


import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.UserApiInvocationLog;
import com.patienttouch.hibernate.UserApi;

public class UserApiLogImpl {
	/**
	 * This method used to query the api log to find the time when this api was last accessed by the user. 
	 * All records that have a timestamp later than this timestamp are marked as new so that the user can
	 * view them.
	 * 
	 * @param session
	 * @param userid
	 * @param api
	 * @return
	 */
	public static UserApiInvocationLog getUserApiLog(Session session, String userid, UserApi api) {
		String query = "from UserApiInvocationLog where userid = " + userid +
				       " and api = '" + api + "'";
		UserApiInvocationLog apiLog;
		List<UserApiInvocationLog> apiLogList;
		
		apiLogList = UserApiLogImpl.getUserApiLog(session, query);
		if (apiLogList == null || apiLogList.isEmpty()) {
			return null;
		}
		
		//This query should return only 1 result
		apiLog = apiLogList.get(0);
		
		return apiLog;
	}
	
	/**
	 * This method is used to fetch details for an appointment
	 * @param sesssion
	 * @param Query
	 * @return
	 */

	private static List<UserApiInvocationLog> getUserApiLog(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		List<UserApiInvocationLog> results = null;
		
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
