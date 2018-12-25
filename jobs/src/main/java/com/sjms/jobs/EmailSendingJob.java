package com.sjms.jobs;

import com.sjms.job.Job;
import com.sjms.job.JobContext;

/**
 * This is the Email Job simulation.
 * 
 * @author Raghu
 *
 */
public class EmailSendingJob extends Job {

	@Override
	public void doJob(JobContext context) {
		System.out.println("Sending Email...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Sent Email...");
	}

}
