package com.canu.repositories;

import com.canu.model.CouponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponModel, Long>, JpaSpecificationExecutor<CouponModel> {

    Optional<CouponModel> findFirstByCode(String code);
}
