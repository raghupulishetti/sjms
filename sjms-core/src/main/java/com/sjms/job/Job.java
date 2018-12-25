package com.sjms.job;

import com.sjms.job.JobContext;

/**
 * This class is an abstract class which can be extended to create the custom
 * jobs.
 * 
 * @author Raghu
 *
 */
public abstract class Job {
	/**
	 * This method contains actual job implementation. Client is responsible to
	 * provide implementation.
	 * 
	 * @param context
	 */
	public abstract void doJob(JobContext context);
}
