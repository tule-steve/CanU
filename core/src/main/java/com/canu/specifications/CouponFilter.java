package com.canu.specifications;

import com.canu.model.CouponModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CouponFilter implements Specification<CouponModel> {

    Long userId;

    String code;

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

        predicates.add(builder.greaterThan(root.get("toDate"), LocalDateTime.now()));
        predicates.add(builder.equal(root.join("userCoupons").get("status"), CouponModel.Status.AVAILABLE));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

