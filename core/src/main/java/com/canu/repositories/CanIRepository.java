package com.canu.repositories;

import com.canu.model.CanIModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanIRepository extends JpaRepository<CanIModel, Long>, JpaSpecificationExecutor<CanIModel> {

    @Query("select s.id as serviceId, s.title as title, count(s.id) as count from CanIModel t inner join t.skillSets s group by s.id, s.title")
    List<Object[]> countCanIByService();
}
