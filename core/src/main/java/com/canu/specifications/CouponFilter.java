package com.canu.specifications;

import com.canu.model.CouponModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CouponFilter implements Specification<CouponModel> {

    Long userId;

    String code;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate toDate;

    Long fromValue;

    Long toValue;

    boolean isAdmin;

    List<CouponModel.Status> status;

    @Override
    public Predicate toPredicate(Root<CouponModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        if (code != null) {
            predicates.add(builder.equal(root.get("code"), code));
        }

        if (userId != null) {
            predicates.add(builder.equal(root.join("userCoupons").get("owner").get("id"), userId));
        }

        if(!isAdmin) {
            predicates.add(builder.greaterThan(root.get("toDate"), LocalDateTime.now()));
            predicates.add(builder.equal(root.join("userCoupons").get("status"), CouponModel.Status.AVAILABLE));
            predicates.add(builder.equal(root.get("status"), CouponModel.Status.AVAILABLE));
        } else {
            if(fromDate != null){

                predicates.add(builder.greaterThan(root.get("toDate"), fromDate.atStartOfDay()));
            }

            if(toDate != null) {
                predicates.add(builder.lessThan(root.get("toDate"), toDate.atTime(23, 59, 59)));
            }

            if(fromValue != null){
                predicates.add(builder.greaterThan(root.get("value"), fromValue));
            }

            if(toValue != null){
                predicates.add(builder.lessThan(root.get("value"), toValue));
            }

            if(status != null){
                predicates.add(builder.in(root.get("status")).value(status));
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

