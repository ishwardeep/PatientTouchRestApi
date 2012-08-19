package com.patienttouch.client;

import java.util.List;
import java.util.Map;

public class Results {
    private int status;
    private int numResults;
    private String edesc;
    //List<Map<String, Object>> result;
    Object result;
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public String getEdesc() {
		return edesc;
	}

	public void setEdesc(String edesc) {
		this.edesc = edesc;
	}

	/*public List<Map<String, Object>> getResults() {
        return result;
    }

    public void setResults(List<Map<String, Object>> result) {
        this.result = result;
    }*/
    
    public Object getResults() {
        return result;
    }

    public void setResults(Object result) {
        this.result = result;
    }
}

