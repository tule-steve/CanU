package com.canu.specifications;

import com.canu.model.CanUModel;
import com.canu.model.PaymentModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Data
public class PaymentFilter implements Specification<PaymentModel> {
    CanUModel userId;

    List<PaymentModel.Status> status = Arrays.asList(PaymentModel.Status.TOPPED_UP, PaymentModel.Status.PAID);

    @Override
    public Predicate toPredicate(Root<PaymentModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        if (userId != null) {
            predicates.add(builder.equal(root.get("owner"), userId));
        }

        predicates.add(builder.in(root.get("status")).value(status));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

