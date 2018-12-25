package com.sjms.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sjms.entity.JobInfo;

@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Long> {
	@Query("select ji from JobInfo ji where ji.endDate>:now")
	List<JobInfo> getAllNonExpiredJobs(@Param("now") Date now);

}
