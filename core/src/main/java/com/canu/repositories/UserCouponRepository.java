package com.canu.repositories;

import com.canu.model.CanUModel;
import com.canu.model.CouponModel;
import com.canu.model.UserCouponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCouponModel, Long> {

    boolean existsDistinctByOwnerAndCoupon(CanUModel canu, CouponModel coupon);

    @Query(value = "select t from UserCouponModel t join t.coupon c where t.owner = :canu and c.code = :code and t.status = :status")
    Optional<UserCouponModel> getTransactionVoucher(CanUModel canu, String code, CouponModel.Status status);
}
