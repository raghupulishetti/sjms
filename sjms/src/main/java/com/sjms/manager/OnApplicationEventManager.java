package com.sjms.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.sjms.entity.JobActivity;
import com.sjms.service.JobService;

/**
 * OnApplicationEventManager is event listener class, on application start jobs
 * will be identified and schedule as per the configurations.
 * 
 * @author Raghu
 *
 */
@Component
public class OnApplicationEventManager {
	@Autowired
	private JobService jobService;

	@Autowired
	ScheduleManager scheduleManager;

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<JobActivity> activities = jobService.getAllFutureJobActivities();
		activities.stream().forEach(item -> {
			scheduleManager.submitJobForSchedule(item);
		});
	}
}
