package com.sjms.job;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobContext implements Serializable {

	private Map<String, Object> jobDetails = new ConcurrentHashMap<>();
	private Map<String, Object> data = new ConcurrentHashMap<>();

	public Map<String, Object> getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(Map<String, Object> jobDetails) {
		this.jobDetails = jobDetails;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
