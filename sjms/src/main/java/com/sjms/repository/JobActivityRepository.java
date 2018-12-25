package com.sjms.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjms.entity.JobActivity;

@Repository
public interface JobActivityRepository extends JpaRepository<JobActivity, Long> {

	@Modifying
	@Query("update JobActivity ja set ja.status=:status, ja.pickupTime=:pickupTime, ja.endTime=:endTime, ja.scheduledTime=:scheduledTime, ja.message=:message where ja.activityId=:activityId")
	void updateJobStatus(@Param("status") String status, @Param("pickupTime") Date pickupTime,
			@Param("endTime") Date endTime, @Param("scheduledTime") Date scheduledTime,
			@Param("activityId") Long activityId, @Param("message") String message);

	@Query("select ja from JobActivity ja join ja.jobInfo ji where (ji.endDate is null or ji.endDate>:now) and ja.scheduledTime>:now and ja.status=:status")
	List<JobActivity> getAllFutureJobActivities(@Param("now") Date date, @Param("status") String status);

	@Query("select ja from JobActivity ja join ja.jobInfo ji where ji.jobId=:jobId and (ji.endDate is null or ji.endDate>:now) and ja.scheduledTime>:now and ja.status=:status")
	List<JobActivity> getAllFutureJobActivitiesByJobId(@Param("jobId") Long jobId, @Param("now") Date date,@Param("status") String status);
	

}
