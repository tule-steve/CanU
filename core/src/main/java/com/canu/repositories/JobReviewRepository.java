package com.canu.repositories;

import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.model.JobReviewerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobReviewRepository
        extends JpaRepository<JobReviewerModel, Long>, JpaSpecificationExecutor<JobReviewerModel> {

    boolean existsDistinctByJobAndReviewerAndTarget(JobModel job, CanUModel review, CanUModel target);
}
