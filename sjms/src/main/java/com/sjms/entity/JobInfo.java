package com.sjms.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.sjms.model.JobPriority;

@Entity
@Table(name = "job_info")
public class JobInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long jobId;
	private String jobName;
	private String jobClass;
	private boolean active;
	private String schedule;

	@Enumerated(EnumType.STRING)
	private JobPriority priority;

	@Temporal(TemporalType.TIMESTAMP)
	private Date effectiveDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@OneToMany(mappedBy = "jobInfo", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<JobActivity> activities = new ArrayList<>();

	@Transient
	private boolean instantRun;

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

	public List<JobActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<JobActivity> activities) {
		this.activities = activities;
	}

	public JobPriority getPriority() {
		return priority;
	}

	public void setPriority(JobPriority priority) {
		this.priority = priority;
	}

	public boolean isInstantRun() {
		return instantRun;
	}

	public void setInstantRun(boolean instantRun) {
		this.instantRun = instantRun;
	}

}
