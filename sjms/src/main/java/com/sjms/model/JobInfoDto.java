package com.sjms.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JobInfoDto {

	private Long jobId;
	@NotNull(message = "Job Name is mandatory")
	private String jobName;

	@NotNull(message = "Job class is mandatory")
	private String jobClass;
	private boolean active;

	@NotNull(message = "Schedule is mandatory")
	private String schedule;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Kolkata")
	private Date effectiveDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Kolkata")
	private Date endDate;

	private String priority;

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

	public String getJobClass() {
		return jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

}
