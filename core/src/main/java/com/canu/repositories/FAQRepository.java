package com.canu.repositories;

import com.canu.model.FAQModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQModel, Long>,
        JpaSpecificationExecutor<FAQModel> {

}
