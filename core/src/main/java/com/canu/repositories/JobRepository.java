package com.canu.repositories;

import com.canu.model.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobModel, Long>, JpaSpecificationExecutor<JobModel> {

    @Query( value = "select a.* from job a where a.creation_user = :userId", nativeQuery = true)
    List<JobModel> findJobForCreationUser(Long userId);

}
