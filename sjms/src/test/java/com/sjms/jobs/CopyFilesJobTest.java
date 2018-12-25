package com.sjms.jobs;

import com.sjms.job.Job;
import com.sjms.job.JobContext;

/**
 * CopyFilesJob class is simulation job for copying files. No files are copied,
 * just its printing the statements and sleepinc for 3 seconds.
 * 
 * @author hanshu
 *
 */
public class CopyFilesJobTest extends Job {

	@Override
	public void doJob(JobContext context) {
		System.out.println("Copying Files...");
		System.out.println("Copying Files end...");
	}

}
