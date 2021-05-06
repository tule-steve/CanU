package com.canu.repositories;

import com.canu.model.JobModel;
import com.canu.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Long>, JpaSpecificationExecutor<PaymentModel> {

}
