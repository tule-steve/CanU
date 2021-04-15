package com.canu.repositories;

import com.canu.model.GuidelineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GuidelineRepository extends JpaRepository<GuidelineModel, Long>,
        JpaSpecificationExecutor<GuidelineModel> {

}
