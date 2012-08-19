package com.patienttouch.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patienttouch.hibernate.UserRole;

public class UserInfo extends Request {
	public String getPracticeName() {
		return (String) this.request.get("practice");
	}
	
	public void setPracticeName(String practiceName) {
		if (practiceName == null || practiceName.isEmpty()) {
			return;
		}
		this.request.put("practice", practiceName);
	}	
	
	public List<Map<String, Object>> getUsers() {
		return (List<Map<String, Object>>) this.request.get("users");
	}
	
	public String getUserid(Map<String, Object> user) {
		return (String) user.get("userid");
	}
	
	public String getUsername(Map<String, Object> user) {
		return (String) user.get("username");
	}
	
	public String getPassword(Map<String, Object> user) {
		return (String) user.get("password");
	}
	
	public String getConfirmPassword(Map<String, Object> user) {
		return (String) user.get("confirmPassword");
	}
	
	public String getFirstName(Map<String, Object> user) {
		return (String) user.get("firstName");
	}
	
	public String getLastName(Map<String, Object> user) {
		return (String) user.get("lastName");
	}
	
	public UserRole getRole(Map<String, Object> user) {
		String role = (String) user.get("role");
		if (role == null) {
			return null;
		}
		
		if (role.equalsIgnoreCase("SUPERUSER")) {
			return UserRole.SUPERUSER;
		}
		else if (role.equalsIgnoreCase("ADMIN")) {
			return UserRole.ADMIN;
		}
		else if (role.equalsIgnoreCase("WEBUSER")) {
			return UserRole.WEBUSER;
		}
		
		return null;
	}
	
	public void setUserInfo(String userid, String username, String password, String confirmPassword, 
			String firstName, String lastName, UserRole role) {
		List<Map<String,Object>> users;
		Map<String, Object> user = new HashMap<String, Object>();
		
		users = (ArrayList<Map<String, Object>>) this.request.get("users");
		if (users == null) {
			users = new ArrayList<Map<String, Object>>();
			this.request.put("users", users);
		}
		users.add(user);
		
		if (userid != null && !userid.isEmpty()) {
			user.put("userid", userid);
		}
		user.put("username", username);
		user.put("password", password);
		user.put("confirmPassword", confirmPassword);
		user.put("firstName", firstName);
		user.put("lastName", lastName);
		user.put("role", role);
	}
}
