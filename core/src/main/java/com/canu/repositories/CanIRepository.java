package com.canu.repositories;

import com.canu.model.CanIModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CanIRepository extends JpaRepository<CanIModel, Long>, JpaSpecificationExecutor<CanIModel> {
}
