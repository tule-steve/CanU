package com.canu.repositories;

import com.canu.model.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<JobModel, Long>, JpaSpecificationExecutor<JobModel> {

    @Query( value = "select a.* from job a where a.creation_user = :userId", nativeQuery = true)
    List<JobModel> findJobForCreationUser(Long userId);

    @Query("select t.nation, t.status, count(t.id) from JobModel t group by t.nation, t.status")
    List<Object[]> findJobReport();

    @Query("select t from JobModel t join fetch t.creationUser where t.status = 'PENDING' and t.requestedUser is not null and t.updatedAt < ?2 and t.updatedAt > ?1")
    List<JobModel> findUnToppedUpJob(LocalDateTime time, LocalDateTime nextTime);

//    @Modifying
//    @Query( value = "update JobModel a set a.requestedUser = null where a.id = :id")
//    int removeRequestedUser(Long id);

    @Query("select t from JobModel  t left join fetch t.creationUser left join fetch  t.requestedUser where t.id = ?1")
    Optional<JobModel> findByIdFetchCreateAndRequestUser(Long id);

    @Query("select t from JobModel  t left join fetch t.creationUser where t.id = ?1")
    Optional<JobModel> findByIdFetchCreateUser(Long id);

}
