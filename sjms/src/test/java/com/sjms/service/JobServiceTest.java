package com.sjms.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import com.sjms.SjmsApplication;
import com.sjms.entity.JobActivity;
import com.sjms.model.JobActivityDto;
import com.sjms.model.JobInfoDto;
import com.sjms.model.JobPriority;
import com.sjms.model.JobStatus;
import com.sjms.model.ResponseDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SjmsApplication.class)
@Profile("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobServiceTest {
	@Autowired
	JobService jobService;

	@Test
	public void a_saveJobTest() throws InterruptedException {

		JobInfoDto dto = new JobInfoDto();
		dto.setActive(true);
		dto.setEffectiveDate(new Date());
		dto.setEndDate(new Date());
		dto.setJobClass("com.sjms.jobs.EmailSendingJobTest");
		dto.setJobName("Email Sending Job");
		dto.setPriority(JobPriority.HIGH.name());
		dto.setSchedule("@onetime");

		ResponseDTO<JobInfoDto> responseDto = jobService.saveJob(dto);

		assertEquals(responseDto.getCode(), Integer.valueOf(1));
		waitForSimulationOfJob(10000L);
		ResponseDTO<List<JobActivityDto>> activities = jobService.getAllJobActivities();
		assertNotNull(activities);
		assertEquals(activities.getT().size(), 1);
		assertEquals(activities.getT().get(0).getStatus(), JobStatus.SUCCESS);

	}

	@Test
	public void b_getAllJobsTest() {
		ResponseDTO<List<JobInfoDto>> list = jobService.getAllJobs();
		assertEquals(list.getCode(), Integer.valueOf(1));
		assertEquals(list.getT().size(), 1);
	}

	@Test
	public void c_saveJobActivityTest() {
		JobActivity ac = new JobActivity();
		ac.setStatus(JobStatus.QUEUED.name());
		JobActivity activity = jobService.saveJobActivity(1L, ac);
		assertEquals(activity.getActivityId() > 0, true);
	}

	@Test
	public void d_runJobInstantlyTest() throws InterruptedException {
		ResponseDTO<JobInfoDto> infoDto = jobService.runJobInstantly(1L);
		assertEquals(infoDto.getCode(), Integer.valueOf(1));
		ResponseDTO<List<JobActivityDto>> activities = jobService.getAllJobActivities();
		assertNotNull(activities);
		assertEquals(activities.getT().size(), 3);
		assertEquals(activities.getT().get(2).getStatus(), JobStatus.RUNNING);
		waitForSimulationOfJob(5000L);
		activities = jobService.getAllJobActivities();
		assertEquals(activities.getT().get(2).getStatus(), JobStatus.SUCCESS);
	}
	
	@Test
	public void e_cancelJobTest()throws Exception{
		JobInfoDto dto = new JobInfoDto();
		dto.setActive(true);
		dto.setEffectiveDate(new Date());
		dto.setJobClass("com.sjms.jobs.EmailSendingJobTest");
		dto.setJobName("Email Sending Job");
		dto.setPriority(JobPriority.HIGH.name());
		dto.setSchedule("@every 4 minutes");

		ResponseDTO<JobInfoDto> responseDto = jobService.saveJob(dto);
		assertEquals(responseDto.getCode(), Integer.valueOf(1));
		waitForSimulationOfJob(2000L);
		List<JobActivity> acts = jobService.getAllFutureJobActivities();
		
		assertTrue(acts.size()>0);
		ResponseDTO<JobActivityDto> rjacd = jobService.cancelJob(acts.get(0).getActivityId());
		assertNotNull(rjacd);
		JobActivityDto act = rjacd.getT();
		assertEquals(act.getStatus(), JobStatus.CANCELLED);
	}

	private void waitForSimulationOfJob(long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

}
