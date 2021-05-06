package com.canu.services;

import com.canu.dto.requests.CollectCouponRequest;
import com.canu.exception.GlobalValidationException;
import com.canu.model.CanUModel;
import com.canu.model.CouponModel;
import com.canu.model.UserCouponModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.CouponRepository;
import com.canu.repositories.UserCouponRepository;
import com.canu.specifications.CouponFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final UserCouponRepository userCouponRepo;

    private final CouponRepository couponRepo;

    final private CanURepository canURepo;

    public Object getCoupons(CouponFilter filter, Pageable p){
        return couponRepo.findAll(filter, p);
    }

    public void addCoupon(CollectCouponRequest request) {
        CouponModel coupon = couponRepo.findFirstByCode(request.getCouponCode())
                  .orElseThrow(() -> new GlobalValidationException(
                          "Cannot find the voucher with code " + request.getCouponCode()));

        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        if(!userCouponRepo.existsDistinctByOwnerAndCoupon(uUser, coupon)){
            UserCouponModel userCoupon = new UserCouponModel();
            userCoupon.setCoupon(coupon);
            userCoupon.setOwner(uUser);
            userCouponRepo.save(userCoupon);
        } else {
            throw new GlobalValidationException("");
        }
    }
}
