package com.canu.repositories;

import com.canu.model.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobModel, Long>, JpaSpecificationExecutor<JobModel> {

//    Optional<JobModel> findByIdAndCreationUser(Long id, CanUModel user);

}
