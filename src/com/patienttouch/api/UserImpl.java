package com.patienttouch.api;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.gson.Gson;

import com.patienttouch.client.ErrorCodes;
import com.patienttouch.client.Response;
import com.patienttouch.client.UserInfo;

import com.patienttouch.hibernate.DbOperations;

import com.patienttouch.hibernate.Office;
import com.patienttouch.hibernate.Practice;
import com.patienttouch.hibernate.User;
import com.patienttouch.hibernate.UserRole;

public class UserImpl {

	/**
	 * This method is used to add a user to a practice
	 * 
	 * @param jsonRequest
	 *            - All the information required to add a user is received in a
	 *            jason request
	 * @return jason response with status of api execution
	 */
	
	public static String addUser(String jsonRequest) {
		Gson gson = new Gson();	
		User user;
		UserInfo request;
		Response response = new Response();

		try {
			// Parse jsonString
			request = gson.fromJson(jsonRequest, UserInfo.class);

			// Validate Mandatory Parameters
			int ret = UserImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}
			// Get practice
			Practice p = PracticeImpl.getPractice(null,
					request.getPracticeName(), false, false, false, true);
			if (p == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			List<Map<String, Object>> users = request.getUsers();
			for (Map<String, Object> u : users) {
				user = new User();

				user.setUsername(request.getUsername(u));
				user.setPassword(request.getPassword(u));
				user.setFirstName(request.getFirstName(u));
				user.setLastName(request.getLastName(u));
				user.setPractice(p);
				user.setRole(request.getRole(u));

				ret = DbOperations.addToDb(null, user);
				if (ret != ErrorCodes.SUCCESS) {
					response.getResults().setStatus(ret);
					response.getResults().setEdesc(ErrorCodes.edesc[ret]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
			}

			response.getResults().setStatus(ErrorCodes.SUCCESS);
			
		} catch (Throwable t) {	
			t.printStackTrace();
			
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}

	/**
	 * This method is used to add a user to a practice
	 * 
	 * @param jsonRequest
	 *            - All the information required to add a user is received in a
	 *            jason request
	 * @return jason response with status of api execution
	 */
	
	public static String updateUser(String jsonRequest) {
		Gson gson = new Gson();	
		User user;
		String userid, query;
		UserInfo request;
		Session session = null;
		Response response = new Response();

		try {
			// Parse jsonString
			request = gson.fromJson(jsonRequest, UserInfo.class);

			// Validate Mandatory Parameters
			int ret = UserImpl.validateParameter(request);
			if (ret != ErrorCodes.SUCCESS) {
				response.getResults().setStatus(ret);
				response.getResults().setEdesc(ErrorCodes.edesc[ret]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			// Get practice
			Practice p = PracticeImpl.getPractice(null,
					request.getPracticeName(), false, false, false, true);
			if (p == null) {
				response.getResults().setStatus(
						ErrorCodes.INVALID_PRACTICE_NAME);
				response.getResults().setEdesc(
						ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
				response.getResults().setNumResults(0);

				return gson.toJson(response);
			}

			List<Map<String, Object>> users = request.getUsers();
			for (Map<String, Object> u : users) {
				userid = request.getUserid(u);
				if (userid == null || userid.isEmpty()) {
					response.getResults().setStatus(ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING]);
					response.getResults().setNumResults(0);
					
					return gson.toJson(response);
				}
				if (!userid.matches("\\d+")) {
					response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
					response.getResults().setNumResults(0);
					
					return gson.toJson(response);
				}
				
				query = "from User where id =" + userid;
				user = UserImpl.getUserDetails(null, query);
				if (user == null) {
					response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
					response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
					response.getResults().setNumResults(0);
					
					return gson.toJson(response);
				}

				user.setUsername(request.getUsername(u));
				user.setPassword(request.getPassword(u));
				user.setFirstName(request.getFirstName(u));
				user.setLastName(request.getLastName(u));
				user.setRole(request.getRole(u));

				session = DbOperations.getDbSession();
				ret = DbOperations.updateDb(session, user);
				if (ret != ErrorCodes.SUCCESS) {
					response.getResults().setStatus(ret);
					response.getResults().setEdesc(ErrorCodes.edesc[ret]);
					response.getResults().setNumResults(0);

					return gson.toJson(response);
				}
			}

			response.getResults().setStatus(ErrorCodes.SUCCESS);
			
		} catch (Throwable t) {	
			t.printStackTrace();
			
			response.getResults().setStatus(ErrorCodes.SERVER_ERROR);
			response.getResults().setEdesc(ErrorCodes.edesc[ErrorCodes.SERVER_ERROR]);
			response.getResults().setNumResults(0);
		}
		
		return gson.toJson(response);
	}

	/**
	 * This api is used to fetch all users for a practice
	 * 
	 * @param practiceName
	 * @return
	 */
	public static String getAllUsers(String practiceName) {
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
		Practice p = PracticeImpl.getPractice(null, practiceName, false, false,
				false, true);
		if (p == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_PRACTICE_NAME);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_PRACTICE_NAME]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		UserInfo userinfo = new UserInfo();
		userinfo.setPracticeName(p.getName());

		List<User> users = p.getUser();
		for (User u : users) {
			userinfo.setUserInfo(Integer.toString(u.getUserid()),
					u.getUsername(), u.getPassword(), u.getPassword(), u.getFirstName(),
					u.getLastName(), u.getRole());
			resultCount++;
		}

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(userinfo.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This method is used to delete a user based on userid
	 * 
	 * @param userid
	 *            - This parameter uniquely identifies a doctor in the system
	 * @return - jason response to specify the status of the api request
	 */

	public static String deleteUser(String userid) {
		int ret = ErrorCodes.SUCCESS;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;

		if (userid == null || userid.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		if (!userid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from User where userid =" + userid;

		User user = UserImpl.getUserDetails(null, query);
		if (user == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		session = DbOperations.getDbSession();
		ret = DbOperations.deleteFromDb(session, user);
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
	 * This api is used to return user details for userid
	 * 
	 * @param userid
	 * @return
	 */

	public static String getUserById(String userid) {
		int resultCount = 0;
		Gson gson = new Gson();
		Response response = new Response();
		Session session = null;
		Practice p;
		List<Office> offices = null;

		// parameter validation
		if (userid == null || userid.isEmpty()) {
			response.getResults().setStatus(
					ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING);
			response.getResults()
					.setEdesc(
							ErrorCodes.edesc[ErrorCodes.MANDATORY_PARAMETER_USER_ID_MISSING]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}
		// doctor id must be numeric
		if (!userid.matches("\\d+")) {
			response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		String query = "from User where userid =" + userid;

		User user = UserImpl.getUserDetails(null, query);
		if (user == null) {
			response.getResults().setStatus(ErrorCodes.INVALID_USER_ID);
			response.getResults().setEdesc(
					ErrorCodes.edesc[ErrorCodes.INVALID_USER_ID]);
			response.getResults().setNumResults(0);

			return gson.toJson(response);
		}

		UserInfo userinfo = new UserInfo();
		userinfo.setPracticeName(user.getPractice().getName());

		userinfo.setUserInfo(Integer.toString(user.getUserid()),
				user.getUsername(), user.getPassword(), user.getPassword(), user.getFirstName(),
				user.getLastName(), user.getRole());
		resultCount++;

		response.getResults().setStatus(ErrorCodes.SUCCESS);
		response.getResults().setNumResults(resultCount);
		response.getResults().setResults(userinfo.getRequest());

		return gson.toJson(response);
	}

	/**
	 * This is a utility method used to fetch details of a user based on input
	 * query
	 * 
	 * @param dbsession
	 *            - this parameter contains database session
	 * @param query
	 *            - this parameter contains database query to be executed
	 * @return
	 */
	public static User getUserDetails(Session dbsession, String query) {
		boolean startTransaction = false;
		Session session = null;
		User userInfo = null;
		Practice p;
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

			List<User> results = q.list();
			if (results == null || results.isEmpty()) {
				if (startTransaction == true && session != null
						&& session.getTransaction().isActive()) {
					session.getTransaction().rollback();
				}
				return null;
			}

			for (User u : results) {
				System.out.println(u.getFirstName());
				p = u.getPractice();
				userInfo = u;
				break;
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

		return userInfo;
	}

	/**
	 * This method is used to validate that all the mandatory parameters have
	 * been received in the request
	 * 
	 * @param request
	 * @return
	 */
	private static int validateParameter(UserInfo request) {
		String username, password, confirmPassword, firstName, lastName;
		UserRole role;

		String practiceName = request.getPracticeName();
		if (practiceName == null || practiceName.isEmpty()) {
			return ErrorCodes.MANDATORY_PARAMETER_PRACTICE_NAME_MISSING;
		}

		List<Map<String, Object>> users = request.getUsers();
		if (users == null) {
			return ErrorCodes.MANDATORY_PARAMETER_USER_MISSING;
		}

		for (Map<String, Object> user : users) {
			username = request.getUsername(user);
			if (username == null || username.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_USERNAME_MISSING;
			}

			password = request.getPassword(user);
			if (password == null || password.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_PASSWORD_MISSING;
			}

			confirmPassword = request.getConfirmPassword(user);
			if (confirmPassword == null || confirmPassword.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_CONFIRM_PASSWORD_MISSING;
			}

			if (!password.equals(confirmPassword)) {
				return ErrorCodes.PASSWORD_MISMATCH;
			}
			
			firstName = request.getFirstName(user);
			if (firstName == null || firstName.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_FIRST_NAME_MISSING;
			}

			lastName = request.getLastName(user);
			if (lastName == null || lastName.isEmpty()) {
				return ErrorCodes.MANDATORY_PARAMETER_LAST_NAME_MISSING;
			}

			role = request.getRole(user);
			if (role == null) {
				return ErrorCodes.INVALID_USER_ROLE;
			}
		}

		return ErrorCodes.SUCCESS;
	}

}
