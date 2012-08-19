package com.patienttouch.client;

import java.util.HashMap;
import java.util.Map;

public class Request {
	//List<Map<String, Object>> request = new ArrayList<Map<String, Object>>();
	Map<String, Object> request = new HashMap<String, Object>();
	
	//public List<Map<String, Object>> getRequest() {
	public Map<String, Object> getRequest() {
		return request;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}
	
}
