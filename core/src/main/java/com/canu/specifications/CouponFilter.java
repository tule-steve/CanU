package com.canu.specifications;

import com.canu.model.CouponModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class CouponFilter implements Specification<CouponModel> {

    String userId;

    String code;

    @Override
    public Predicate toPredicate(Root<CouponModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (code != null) {
            predicates.add(builder.equal(root.get("code"), code));
        }

        if (userId != null) {
            predicates.add(builder.equal(root.join("userCoupons").get("owner").get("id"), userId));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

