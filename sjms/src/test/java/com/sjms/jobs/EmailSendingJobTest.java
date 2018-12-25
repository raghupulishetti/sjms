package com.sjms.jobs;

import com.sjms.job.Job;
import com.sjms.job.JobContext;

/**
 * This is the Email Job simulation.
 * 
 * @author Raghu
 *
 */
public class EmailSendingJobTest extends Job {

	@Override
	public void doJob(JobContext context) {
		System.out.println("Sending Email...");
		
		System.out.println("Sent Email...");
	}

}
