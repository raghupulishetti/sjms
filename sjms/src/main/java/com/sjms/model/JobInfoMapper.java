package com.sjms.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.sjms.entity.JobInfo;

public class JobInfoMapper {

	public static JobInfoDto convertEntityToDto(JobInfo jobInfo) {
		JobInfoDto jobInfoDto = new JobInfoDto();
		jobInfoDto.setJobId(jobInfo.getJobId());
		jobInfoDto.setJobName(jobInfo.getJobName());
		jobInfoDto.setActive(jobInfo.isActive());
		jobInfoDto.setEffectiveDate(jobInfo.getEffectiveDate());
		jobInfoDto.setEndDate(jobInfo.getEndDate());
		jobInfoDto.setJobClass(jobInfo.getJobClass());
		jobInfoDto.setSchedule(jobInfo.getSchedule());
		jobInfoDto.setPriority(jobInfo.getPriority().name());
		return jobInfoDto;
	}

	public static List<JobInfoDto> convertEntitiesToDtos(List<JobInfo> jobInfos) {
		return jobInfos.stream().map((jobInfo) -> convertEntityToDto(jobInfo)).collect(Collectors.toList());

	}

	public static JobInfo convertDtoToEntity(JobInfoDto jobInfoDto) {
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobId(jobInfoDto.getJobId());
		jobInfo.setJobName(jobInfoDto.getJobName());
		jobInfo.setActive(jobInfoDto.isActive());
		if (null == jobInfoDto.getEffectiveDate()) {
			jobInfo.setEffectiveDate(new Date());
		} else {
			jobInfo.setEffectiveDate(jobInfoDto.getEffectiveDate());
		}
		jobInfo.setEndDate(jobInfoDto.getEndDate());
		
		jobInfo.setJobClass(jobInfoDto.getJobClass());
		jobInfo.setSchedule(jobInfoDto.getSchedule());
		if (jobInfoDto.getPriority() != null) {
			jobInfo.setPriority(JobPriority.valueOf(jobInfoDto.getPriority()));
		} else {
			jobInfo.setPriority(JobPriority.LOW);
		}
		return jobInfo;
	}
}
