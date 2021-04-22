package com.canu.repositories;

import com.canu.model.SupportRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequestModel, Long>,
        JpaSpecificationExecutor<SupportRequestModel> {

}
