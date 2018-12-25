package com.sjms.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjms.entity.JobActivity;
import com.sjms.entity.JobInfo;
import com.sjms.manager.ScheduleManager;
import com.sjms.model.JobActivityDto;
import com.sjms.model.JobActivityMapper;
import com.sjms.model.JobInfoDto;
import com.sjms.model.JobInfoMapper;
import com.sjms.model.JobPriority;
import com.sjms.model.JobStatus;
import com.sjms.model.ResponseDTO;
import com.sjms.service.JobService;

@RunWith(SpringRunner.class)
@WebMvcTest(JobController.class)

public class JobControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	JobService jobService;

	@MockBean
	ScheduleManager scheduleManager;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void getAllJobsTest() throws Exception {

		List<JobInfoDto> jobInfos = new ArrayList<>();
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobId(1L);
		jobInfo.setActive(true);
		jobInfo.setEffectiveDate(new Date());
		jobInfo.setEndDate(new Date());
		jobInfo.setJobClass("com.sjms.jobs.EmailSendingJob");
		jobInfo.setJobName("Email Sending Job");
		jobInfo.setPriority(JobPriority.HIGH);
		jobInfo.setSchedule("@onetime");
		JobInfoDto dto = JobInfoMapper.convertEntityToDto(jobInfo);
		jobInfos.add(dto);
		ResponseDTO<List<JobInfoDto>> jobs = new ResponseDTO<>(1, "Success", jobInfos);
		given(this.jobService.getAllJobs()).willReturn(jobs);

		this.mvc.perform(get("/jobs").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is(1))).andExpect(jsonPath("$.message", notNullValue()));
	}

	@Test
	public void saveJobTest() throws Exception {

		JobInfoDto dto = new JobInfoDto();
		dto.setActive(true);
		dto.setEffectiveDate(new Date());
		dto.setEndDate(new Date());
		dto.setJobClass("com.sjms.jobs.EmailSendingJob");
		dto.setJobName("Email Sending Job");
		dto.setPriority(JobPriority.HIGH.name());
		dto.setSchedule("@onetime");

		ResponseDTO<JobInfoDto> job = new ResponseDTO<>(1, "Success", dto);
		given(this.jobService.saveJob(dto)).willReturn(job);

		String jobJson = "{\"jobId\": \"257\",\"jobName\": \"Email JOb\",\"jobClass\": \"com.sjms.jobs.EmailSendingJob\",\"active\": \"true\",\"schedule\": \"@onetime\",\"effectiveDate\": \"2018-12-24 19:38\",\"endDate\": \"\",\"priority\": \"LOW\"}";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/jobs/save").accept(MediaType.APPLICATION_JSON)
				.content(jobJson).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = this.mvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();
		assertEquals(HttpStatus.OK.value(), response.getStatus());

	}

	@Test
	public void getAllJobActivitiesTest() throws Exception {
		List<JobActivityDto> activities = new ArrayList<>();
		JobActivity activity = new JobActivity();
		activity.setActivityId(1L);
		activity.setPickupTime(new Date());
		activity.setScheduledTime(new Date());
		activity.setStatus(JobStatus.QUEUED.name());
		JobActivityDto dto = JobActivityMapper.convertEntityToDto(activity);
		activities.add(dto);
		ResponseDTO<List<JobActivityDto>> ac = new ResponseDTO<>(1, "Success", activities);
		given(this.jobService.getAllJobActivities()).willReturn(ac);

		this.mvc.perform(get("/jobs/jobactivities").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.code", is(1))).andExpect(jsonPath("$.message", notNullValue()));
	}
}
