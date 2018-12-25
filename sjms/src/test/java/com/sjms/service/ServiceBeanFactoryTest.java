package com.sjms.service;

import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import com.sjms.SjmsApplication;
import com.sjms.manager.ScheduleManager;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SjmsApplication.class)
@Profile("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceBeanFactoryTest {

	private JobService jobService;
	private ScheduleManager scheduleManager;

	@Test
	public void getBeanTest() {
		jobService = ServiceBeanFactory.getBean(JobService.class);
		scheduleManager = ServiceBeanFactory.getBean(ScheduleManager.class);
		assertNotNull(jobService);
		assertNotNull(scheduleManager);
	}

}
