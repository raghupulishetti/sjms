package com.sjms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjms.model.JobActivityDto;
import com.sjms.model.JobInfoDto;
import com.sjms.model.ResponseDTO;
import com.sjms.service.JobService;

/**
 * JobController contains the end points for managing the jobs throught api.
 * 
 * @author Raghu
 *
 */
@RestController
@RequestMapping("/jobs")
public class JobController {

	@Autowired
	JobService jobService;

	/**
	 * This method is to list all the jobs in system.
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseDTO<List<JobInfoDto>> getAllJobs() {
		return jobService.getAllJobs();
	}

	/**
	 * This method is to save or update job.
	 * 
	 * @param jobInfoDto
	 * @return
	 */
	@PostMapping("/save")
	public ResponseDTO<JobInfoDto> saveJob(@RequestBody @Validated JobInfoDto jobInfoDto) {
		return jobService.saveJob(jobInfoDto);
	}

	/**
	 * This method is to get all job running instances for all jobs.
	 * 
	 * @return
	 */
	@GetMapping("/jobactivities")
	public ResponseDTO<List<JobActivityDto>> getAllJobActivities() {
		return jobService.getAllJobActivities();
	}

	/**
	 * This method is used to run the job instantly. It will immediatly start the
	 * Job.
	 * 
	 * @param jobId
	 * @return
	 */
	@PostMapping("/{jobId}/run")
	public ResponseDTO<JobInfoDto> runJobInstantly(@PathVariable Long jobId) {
		return jobService.runJobInstantly(jobId);
	}
	
	/**
	 * Deletes the job.  Its a soft delete, just set the active flag to 
	 * @param jobId
	 * @return
	 */
	@DeleteMapping("/activity/{activityId}/delete")
	public ResponseDTO<JobActivityDto> cancelJob(@PathVariable Long activityId){
		return jobService.cancelJob(activityId);
	}
	

}
