package com.sjms.manager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import com.sjms.entity.JobActivity;
import com.sjms.entity.JobInfo;
import com.sjms.exception.JobExpiredException;
import com.sjms.exception.SchedulingParserException;
import com.sjms.job.Job;
import com.sjms.job.JobContext;
import com.sjms.model.JobStatus;
import com.sjms.repository.JobActivityRepository;
import com.sjms.service.JobService;
import com.sjms.service.ServiceBeanFactory;

/**
 * ScheduleManager is main scheduling class where jobs will be scheduled and
 * execute the jobs based on the schedule using Executor Service.
 * 
 * @author Raghu
 *
 */
@Component
public class ScheduleManager implements IScheduleManager {
	protected static final Logger LOGGER = LoggerFactory.getLogger(ScheduleManager.class);

	private List<String> scheduleFrequency;

	private Queue<JobActivity> jobQueue = new ConcurrentLinkedQueue<>();
	private ScheduledExecutorService instantexecutor;

	private ExecutorService executor;

	@Autowired
	JobActivityRepository jobActivityRepository;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		scheduleFrequency = new ArrayList<>();
		scheduleFrequency.add("years");
		scheduleFrequency.add("months");
		scheduleFrequency.add("weeks");
		scheduleFrequency.add("days");
		scheduleFrequency.add("hours");
		scheduleFrequency.add("minutes");

		instantexecutor = Executors.newScheduledThreadPool(5);
		executor = Executors.newFixedThreadPool(20);

		Thread t = new Thread(() -> {
			while (true) {
				try {
					Thread.yield();
					Thread.sleep(10000);
					Calendar now = Calendar.getInstance();
					now.set(Calendar.SECOND, 0);
					now.set(Calendar.MILLISECOND, 0);
					Comparator<JobActivity> comp = new Comparator<JobActivity>() {
						@Override
						public int compare(JobActivity o1, JobActivity o2) {
							Integer o1priority = Integer.valueOf(o1.getJobInfo().getPriority().getValue());
							Integer o2priority = Integer.valueOf(o2.getJobInfo().getPriority().getValue());
							return -o1priority.compareTo(o2priority);
						}
					};
					final BlockingQueue<JobActivity> priorityQueue = new PriorityBlockingQueue<>(5, comp);
					jobQueue.stream().forEach((act) -> {
						if (act.getScheduledTime().compareTo(now.getTime()) == 0) {
							if (act.getStatus().equals(JobStatus.QUEUED.name())) {
								try {
									priorityQueue.put(act);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							jobQueue.remove(act);
						}
					});
					priorityQueue.stream().sorted(comp).forEach((activity) -> {
						Class<Job> clazz;
						try {
							clazz = (Class<Job>) Class.forName(activity.getJobInfo().getJobClass());
							Constructor<?> ctor = clazz.getConstructor();
							JobContext context = new JobContext();
							context.getJobDetails().put("currentActivity", activity);
							context.getJobDetails().put("job", activity.getJobInfo());
							Job job = (Job) ctor.newInstance();
							job.setJobContext(context);
							executor.execute(job);
						} catch (ClassNotFoundException | NoSuchMethodException | SecurityException
								| InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							JobService jobService = ServiceBeanFactory.getBean(JobService.class);
							jobService.updateJobStatus(JobStatus.FAILED, new Date(), new Date(),
									activity.getScheduledTime(), activity.getActivityId(), e.toString());
							LOGGER.error("Error occured while starting the job...");
							LOGGER.error("", e);
						}
					});

				} catch (Exception e) {
					LOGGER.error("Error occured while running the deamon thread");
					LOGGER.error("", e);
				}
			}
		});
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

	}

	public void shutDown() {
		executor.shutdown();
	}

	@SuppressWarnings("unchecked")
	public void runJobInstantly(JobInfo jobInfo, JobActivity ac) {
		Class<Job> clazz;
		try {
			clazz = (Class<Job>) Class.forName(jobInfo.getJobClass());
			Constructor<?> ctor = clazz.getConstructor();
			JobContext context = new JobContext();
			context.getJobDetails().put("currentActivity", ac);
			context.getJobDetails().put("job", jobInfo);
			Job job;
			job = (Job) ctor.newInstance();
			job.setJobContext(context);
			instantexecutor.execute(job);

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static Date parseCronExpression(final String cronExpression) {
		final CronSequenceGenerator generator = new CronSequenceGenerator(cronExpression);
		return generator.next(new Date());
	}

	/**
	 * This method add the job activity into the queue, from where Deamon thread
	 * picks the jobs based on schedule.
	 * 
	 * @param activity
	 */
	public void submitJobForSchedule(JobActivity activity) {
		jobQueue.add(activity);
	}

	/**
	 * Set the status of the activity to cancelled and those will be removed by
	 * daemon thread. These will not be part of schedule again.
	 * 
	 * @param activityIds
	 */
	public void removeJobFromSchedule(List<Long> activityIds) {
		jobQueue.stream().forEach((act) -> {
			if (activityIds.contains(act.getActivityId())) {
				act.setStatus(JobStatus.CANCELLED.name());
			}
		});

		LOGGER.debug("Removed jobs from schedule {}", activityIds);

	}

	/**
	 * This method will parse and calculate next fire time based on the given
	 * schedule.
	 * 
	 * @param schedule
	 * @param effectiveDate
	 * @param endDate
	 * @return
	 */
	public Date calculateNextFireTime(String schedule, Date effectiveDate, Date endDate) {
		Calendar nextFireTime = Calendar.getInstance();
		nextFireTime.set(Calendar.MILLISECOND, 0);
		nextFireTime.set(Calendar.SECOND, 0);

		if (effectiveDate != null && nextFireTime.getTime().before(effectiveDate)) {
			nextFireTime.setTime(effectiveDate);
			nextFireTime.set(Calendar.MILLISECOND, 0);
			nextFireTime.set(Calendar.SECOND, 0);
		}

		String[] scheduleArr = schedule.trim().split(" ");

		if (scheduleArr[0].startsWith("@onetime")) {
			if (effectiveDate != null && nextFireTime.getTime().after(effectiveDate)) {
				throw new JobExpiredException("Job already Expired..");
			}
			return nextFireTime.getTime();
		}

		if (scheduleArr.length != 3) {
			String error = "Schedule Expression must have 3 tokens.";
			LOGGER.error(error);
			throw new SchedulingParserException(error);
		}

		if (!scheduleArr[0].startsWith("@every")) {
			String error = "Schedule Expression must start with @every";
			LOGGER.error(error);
			throw new SchedulingParserException(error);
		}

		Integer scheduledNumber;
		try {
			scheduledNumber = Integer.parseInt(scheduleArr[1]);
		} catch (NumberFormatException ne) {
			String error = "Schedule Expression must have second token as a number";
			LOGGER.error(error);
			throw new SchedulingParserException(error);
		}

		if (!scheduleFrequency.contains(scheduleArr[2])) {
			String error = "Schedule Expression must have third token as either of {years, months, weeks, days, hours, minutes}";
			LOGGER.error(error);
			throw new SchedulingParserException(error);
		}

		if (endDate != null && nextFireTime.getTime().after(endDate)) {
			throw new JobExpiredException("Job already Expired..");
		}

		switch (scheduleArr[2]) {
		case "minutes": {
			if (scheduledNumber > 59 || scheduledNumber < 0) {
				String error = "Seconds must be between 0 to 59.";
				LOGGER.error(error);
				throw new SchedulingParserException(error);
			} else {
				nextFireTime.add(Calendar.MINUTE, scheduledNumber);
			}
			break;
		}
		case "hours": {
			if (scheduledNumber > 23 || scheduledNumber < 0) {
				String error = "Hours must be between 0 to 23.";
				LOGGER.error(error);
				throw new SchedulingParserException(error);
			} else {
				nextFireTime.add(Calendar.HOUR, scheduledNumber);
			}
			break;

		}
		case "days": {
			nextFireTime.add(Calendar.DATE, scheduledNumber);
			break;

		}
		case "weeks": {
			nextFireTime.add(Calendar.WEEK_OF_YEAR, scheduledNumber);
			break;

		}
		case "months": {
			nextFireTime.add(Calendar.MONTH, scheduledNumber);
			break;

		}
		default:
			nextFireTime.add(Calendar.YEAR, scheduledNumber);
			break;
		}

		return nextFireTime.getTime();
	}

}
