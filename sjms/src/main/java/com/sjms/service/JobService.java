package com.sjms.service;

import java.util.Date;
import java.util.List;

import com.sjms.entity.JobActivity;
import com.sjms.entity.JobInfo;
import com.sjms.model.JobActivityDto;
import com.sjms.model.JobInfoDto;
import com.sjms.model.JobStatus;
import com.sjms.model.ResponseDTO;

public interface JobService {

	ResponseDTO<List<JobInfoDto>> getAllJobs();

	ResponseDTO<JobInfoDto> saveJob(JobInfoDto jobInfoDto);

	ResponseDTO<List<JobActivityDto>> getAllJobActivities();

	ResponseDTO<JobInfoDto> runJobInstantly(Long jobId);

	void updateJobStatus(JobStatus running, Date pickupTime, Date endTime, Date scheduledTime, Long activityId,
			String message);

	public JobActivity saveJobActivity(Long jobId, JobActivity activity);

	List<JobActivity> getAllFutureJobActivities();

	JobInfo findJobById(Long jobId);

	ResponseDTO<JobActivityDto> cancelJob(Long activityId);
}
