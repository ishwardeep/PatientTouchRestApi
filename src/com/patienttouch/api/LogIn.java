package com.patienttouch.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.google.gson.Gson;
import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.Response;
import com.patienttouch.hibernate.Billing;
import com.patienttouch.hibernate.DbOperations;
import com.patienttouch.hibernate.Doctor;
import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.SmsTemplates;
import com.patienttouch.hibernate.User;

public class LogIn {

	public static String login(String user, String password) {
		int ret = ErrorCodes.SUCCESS;
		Date d;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;

		try {
			// parameter validation
			if (user == null || user.isEmpty()) {
				response.getResults().setStatus(
						ErrorCodes.MANDATORY_PARAMETER_USERNAME_MISSING);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_USERNAME_MISSING]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			if (password == null || password.isEmpty()) {
				response.getResults().setStatus(
						ErrorCodes.MANDATORY_PARAMETER_PASSWORD_MISSING);
				response.getResults()
						.setEdesc(
								ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_PASSWORD_MISSING]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			// Lookup db to fetch user details to validate password
			User u = LogIn.dbLookup(user);

			if (u == null) {
				response.getResults().setStatus(ErrorCodes.INVALID_USERNAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_USERNAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			// Validate password
			if (!u.getPassword().equals(password)) {
				response.getResults().setStatus(ErrorCodes.INVALID_PASSWORD);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PASSWORD]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			d = new Date(System.currentTimeMillis());

			if (u.getFirstLogin() == null) {
				u.setFirstLogin(d);
			}
			u.setLastLogin(d);

			session = DbOperations.getDbSession();
			ret = DbOperations.updateDb(session, u);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);
				
				return gson.toJson(response);
			}
			
			List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
			Map<String, Object> result = new HashMap<String, Object>();

			result.put("user", u.getUsername());
			result.put("role", u.getRole().toString());
			
			//Practice will be null if the user is a SUPERUSER role who can access
			//all the practice setup in the system
			if (u.getPractice() != null) {
				result.put("practice", u.getPractice().getName());
				result.put("practiceid", u.getPractice().getPracticeid());
			}
			
			results.add(result);
			response.getResults().setStatus(ErrorCodes.SUCCESS);
			response.getResults().setNumResults(1);
			response.getResults().setResults(results);
		} catch (Throwable t) {
			t.printStackTrace();
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}

	public static User dbLookup(String username) {
		Session session = null;
		User userInfo = null;

		try {
			session = DbOperations.getDbSession();
			session.getTransaction().begin();

			Query q = session
					.createQuery("from User where username= :username and del =0");
			q.setString("username", username);
			List<User> results = q.list();

			if (results == null || results.isEmpty()) {
				if (session != null && session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				System.out.println("User [" + username + "] not found");
				return null;
			}

			for (User u : results) {
				System.out.println(u.getUsername() + "[" + u.getPassword()
						+ "]");

				userInfo = u;
			}

			session.getTransaction().commit();

		} catch (Throwable t) {
			if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("dbLookup Exception");
			t.printStackTrace();
		}

		return userInfo;
	}

}
