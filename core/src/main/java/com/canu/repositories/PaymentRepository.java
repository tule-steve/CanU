package com.canu.repositories;

import com.canu.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentModel, Long>, JpaSpecificationExecutor<PaymentModel> {

    @Query("select t from PaymentModel t left join fetch t.job j left join fetch j.requestedUser left join fetch j.creationUser where t.id = ?1")
    Optional<PaymentModel> findByIdFetchJobAndUser(Long id);
}
