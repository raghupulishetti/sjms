package com.sjms.model;

import java.util.List;
import java.util.stream.Collectors;

import com.sjms.entity.JobActivity;

/**
 * JobActivityMapper is to convert JobActivity entity to dto.
 * 
 * @author Raghu
 *
 */
public class JobActivityMapper {

	public static JobActivityDto convertEntityToDto(JobActivity activity) {
		JobActivityDto dto = new JobActivityDto();
		dto.setActivityId(activity.getActivityId());
		dto.setEndTime(activity.getEndTime());
		
		if (activity.getJobInfo() != null) {
			dto.setJobId(activity.getJobInfo().getJobId());
			dto.setJobName(activity.getJobInfo().getJobName());
			dto.setSchedule(activity.getJobInfo().getSchedule());
		}
		dto.setPickupTime(activity.getPickupTime());

		dto.setStatus(JobStatus.valueOf(activity.getStatus()));
		dto.setScheduledTime(activity.getScheduledTime());
		return dto;
	}

	public static List<JobActivityDto> convertEntityListToDtoList(List<JobActivity> activities) {
		return activities.stream().map((activity) -> convertEntityToDto(activity)).collect(Collectors.toList());
	}
}
