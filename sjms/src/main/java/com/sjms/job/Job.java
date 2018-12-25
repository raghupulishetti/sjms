package com.sjms.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sjms.entity.JobActivity;
import com.sjms.entity.JobInfo;
import com.sjms.manager.IScheduleManager;
import com.sjms.manager.ScheduleManager;
import com.sjms.model.JobStatus;
import com.sjms.service.JobService;
import com.sjms.service.ServiceBeanFactory;

/**
 * Job is abstract class by which user can write their own Jobs. This has to be
 * extended and implement doJob() method.
 * 
 * @author Raghu
 *
 */
public abstract class Job implements Runnable {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

	private JobContext context;
	private JobService jobService;
	private JobActivity jobActivity;
	private JobInfo job;
	private Date pickUpTime;
	private IScheduleManager scheduleManager;

	public Job() {

	}

	public void setJobContext(JobContext context) {
		this.context = context;
	}

	@Override
	final public void run() {

		try {
			beforeJob(context);
			doJob(context);
			afterJob();
		} catch (Exception e) {
			logException(e);
		} finally {
			doFinally();
		}
	}

	protected abstract void doJob(JobContext context);

	private void doFinally() {
		LOGGER.info("jobId={}, logId={}, Finalizing the job ...", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId());

		LOGGER.info("jobId={}, Calculating next fire time...");
		if (!job.getSchedule().startsWith("@onetime") && !job.isInstantRun()) {
			JobActivity ac = new JobActivity();
			ac.setStatus(JobStatus.QUEUED.name());
			ac = jobService.saveJobActivity(job.getJobId(), ac);
			if (ac != null && ac.getActivityId() != null) {
				scheduleManager.submitJobForSchedule(ac);
			}
		}

	}

	private void logException(Exception ex) {
		LOGGER.error("jobId={}, logId={}, Excepton occured while running the job...", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId());
		LOGGER.error("", ex);

		jobService.updateJobStatus(JobStatus.FAILED, pickUpTime, new Date(), jobActivity.getScheduledTime(),
				jobActivity.getActivityId(), ex.toString());
		LOGGER.info("jobId={}, logId={}, Updated status of Job Activity to : {}", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId(), JobStatus.FAILED);

	}

	private void afterJob() {
		LOGGER.info("jobId={}, logId={}, Ending ...", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId());
		jobService.updateJobStatus(JobStatus.SUCCESS, pickUpTime, new Date(), jobActivity.getScheduledTime(),
				jobActivity.getActivityId(), null);
		LOGGER.info("jobId={}, logId={}, Updated status of Job Activity to : {}", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId(), JobStatus.SUCCESS);
	}

	private void beforeJob(JobContext context) {
		pickUpTime = new Date();
		jobService = ServiceBeanFactory.getBean(JobService.class);
		scheduleManager = ServiceBeanFactory.getBean(ScheduleManager.class);
		jobActivity = (JobActivity) context.getJobDetails().get("currentActivity");
		job = (JobInfo) context.getJobDetails().get("job");
		LOGGER.info("jobId={}, logId={}, begining ...", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId());

		jobService.updateJobStatus(JobStatus.RUNNING, pickUpTime, null, jobActivity.getScheduledTime(),
				jobActivity.getActivityId(), null);

		LOGGER.info("jobId={}, logId={}, Updated status of Job Activity to : {}", job == null ? "" : job.getJobId(),
				jobActivity == null ? "" : jobActivity.getActivityId(), JobStatus.RUNNING);
	}

}
