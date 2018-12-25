package com.sjms.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "job_activity")
public class JobActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long activityId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date scheduledTime;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date pickupTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "job_id")
	private JobInfo jobInfo;

	private String message;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public JobInfo getJobInfo() {
		return jobInfo;
	}

	public void setJobInfo(JobInfo jobInfo) {
		this.jobInfo = jobInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof JobActivity)) {
			return false;
		}
		JobActivity ja = (JobActivity) obj;
		return this.activityId == ja.getActivityId();
	}

}
