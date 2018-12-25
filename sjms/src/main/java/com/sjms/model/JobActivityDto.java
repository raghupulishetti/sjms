package com.sjms.model;

import java.util.Date;

public class JobActivityDto {

	private Long jobId;
	private String jobName;
	private String schedule;
	private Long activityId;
	private Date scheduledTime;
	private JobStatus status;
	private Date pickupTime;
	private Date endTime;

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public Date getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Date pickupTime) {
		this.pickupTime = pickupTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
