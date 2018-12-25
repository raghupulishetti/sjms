package com.sjms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sjms.entity.JobActivity;
import com.sjms.entity.JobInfo;
import com.sjms.exception.JobExpiredException;
import com.sjms.exception.SchedulingParserException;
import com.sjms.manager.ScheduleManager;
import com.sjms.model.JobActivityDto;
import com.sjms.model.JobActivityMapper;
import com.sjms.model.JobInfoDto;
import com.sjms.model.JobInfoMapper;
import com.sjms.model.JobStatus;
import com.sjms.model.ResponseDTO;
import com.sjms.repository.JobActivityRepository;
import com.sjms.repository.JobInfoRepository;

@Transactional
@Service
public class JobServiceImpl implements JobService {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);
	@Autowired
	JobInfoRepository jobInfoRepository;

	@Autowired
	JobActivityRepository jobActivityRepository;

	@Autowired
	ScheduleManager scheduleManager;

	@Override
	public ResponseDTO<List<JobInfoDto>> getAllJobs() {
		List<JobInfoDto> infoDtos = new ArrayList<>();
		List<JobInfo> jobInfos = jobInfoRepository.findAll();
		infoDtos = JobInfoMapper.convertEntitiesToDtos(jobInfos);
		return new ResponseDTO<List<JobInfoDto>>(1, "Success", infoDtos);
	}

	/**
	 * Saving the job details along with next fire Job Activity.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResponseDTO<JobInfoDto> saveJob(JobInfoDto jobInfoDto) {
		try {
			scheduleManager.calculateNextFireTime(jobInfoDto.getSchedule(), jobInfoDto.getEffectiveDate(),
					jobInfoDto.getEndDate());
		} catch (SchedulingParserException spe) {
			LOGGER.error("Error occured while parsing the expression {} of job {}", jobInfoDto.getSchedule(),
					jobInfoDto);
			return new ResponseDTO(0, "Failed due to " + spe.getMessage(), null);
		}

		try {

			JobInfo jobInfo = JobInfoMapper.convertDtoToEntity(jobInfoDto);
			jobInfo = jobInfoRepository.save(jobInfo);
			if (null != jobInfoDto.getJobId()) { // set status of future jobs to CANCELLED and create a new activity
													// based on new schedule.
				cancelExistingFutureJobs(jobInfo.getJobId());
			}

			JobActivity activity = new JobActivity();
			activity.setStatus(JobStatus.QUEUED.name());
			activity = saveJobActivity(jobInfo.getJobId(), activity);
			scheduleManager.submitJobForSchedule(activity);

			return new ResponseDTO(1, "Successfully saved.", JobInfoMapper.convertEntityToDto(jobInfo));
		} catch (Exception e) {
			LOGGER.error("Error occured while saving the job job {}", jobInfoDto);
			LOGGER.error("", e);
			return new ResponseDTO(0, "Failed due to " + e.getMessage(), null);

		}
	}

	private void cancelExistingFutureJobs(Long jobId) {
		List<JobActivity> activities = jobActivityRepository.getAllFutureJobActivitiesByJobId(jobId, new Date(),
				JobStatus.QUEUED.name());
		List<Long> activityIds = new ArrayList<>();

		for (JobActivity act : activities) {
			act.setStatus(JobStatus.CANCELLED.name());
			activityIds.add(act.getActivityId());
		}
		jobActivityRepository.saveAll(activities);
		scheduleManager.removeJobFromSchedule(activityIds);
	}

	/**
	 * Saves the individual job activity.
	 */
	@Override
	public JobActivity saveJobActivity(Long jobId, JobActivity activity) {

		try {
			JobInfo jobInfo = jobInfoRepository.getOne(jobId);
			Date nextFireTime = scheduleManager.calculateNextFireTime(jobInfo.getSchedule(), jobInfo.getEffectiveDate(),
					jobInfo.getEndDate());
			activity.setScheduledTime(nextFireTime);
			activity.setJobInfo(jobInfo);
			jobActivityRepository.save(activity);

		} catch (JobExpiredException e) {
			LOGGER.info("jobId={}, Job expired. Scheduling will not be done for future.");
			return null;
		}

		return activity;
	}

	@Override
	public ResponseDTO<List<JobActivityDto>> getAllJobActivities() {
		List<JobActivityDto> activityDtos = new ArrayList<>();
		List<JobActivity> activities = jobActivityRepository.findAll();
		activityDtos = JobActivityMapper.convertEntityListToDtoList(activities);
		return new ResponseDTO<List<JobActivityDto>>(1, "Success", activityDtos);
	}

	@Override
	public ResponseDTO<JobInfoDto> runJobInstantly(Long jobId) {
		Optional<JobInfo> jobInfo = jobInfoRepository.findById(jobId);
		if (jobInfo.isPresent()) {
			JobInfo info = jobInfo.get();
			info.setInstantRun(true);
			JobActivity ac = new JobActivity();
			ac.setStatus(JobStatus.RUNNING.name());
			ac = saveJobActivity(jobId, ac);
			scheduleManager.runJobInstantly(info, ac);
			return new ResponseDTO<>(1, "Success", null);
		} else {
			LOGGER.error("No such job exists with Job ID: {}", jobId);
			return new ResponseDTO<>(0, "Failure: No such job exists", null);
		}

	}

	@Override
	public void updateJobStatus(JobStatus running, Date pickupTime, Date endTime, Date scheduledTime, Long activityId,
			String message) {
		jobActivityRepository.updateJobStatus(running.name(), pickupTime, endTime, scheduledTime, activityId, message);

	}

	@Override
	public List<JobActivity> getAllFutureJobActivities() {
		return jobActivityRepository.getAllFutureJobActivities(new Date(), JobStatus.QUEUED.name());
	}

	@Override
	public JobInfo findJobById(Long jobId) {
		return jobInfoRepository.getOne(jobId);
	}

	@Override
	public ResponseDTO<JobActivityDto> cancelJob(Long activityId) {
		Optional<JobActivity> activity = jobActivityRepository.findById(activityId);
		if (activity.isPresent()) {
			JobActivity act = activity.get();
			act.setStatus(JobStatus.CANCELLED.name());
			act = jobActivityRepository.save(act);
			scheduleManager.removeJobFromSchedule(Arrays.asList(act.getActivityId()));
			
			
			JobActivity ac = new JobActivity();
			ac.setStatus(JobStatus.QUEUED.name());
			ac = saveJobActivity(act.getJobInfo().getJobId(), ac);
			if (ac != null && ac.getActivityId() != null) {
				scheduleManager.submitJobForSchedule(ac);
			}
			
			return new ResponseDTO<>(1, "Successfully canceled Job: " + activityId,
					JobActivityMapper.convertEntityToDto(act));
		} else {
			LOGGER.error("No such job exists with Job Activity ID: {}", activityId);
			return new ResponseDTO<>(0, "Failure: No such job activity exists with id: " + activityId, null);
		}

	}

}
