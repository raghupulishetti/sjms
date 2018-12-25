package com.sjms.manager;

import com.sjms.entity.JobActivity;

public interface IScheduleManager {
	public void submitJobForSchedule(JobActivity activity);
}
