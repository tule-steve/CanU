package com.canu.specifications;

import com.canu.model.PaymentModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PaymentFilter implements Specification<PaymentModel> {
    Long userId;

    Long owner;

    Long requestedUser;

    String transactionId;

    String paypal;

    Long id;

    @Enumerated(EnumType.STRING)
    List<PaymentModel.Status> status = Arrays.asList(PaymentModel.Status.TOPPED_UP, PaymentModel.Status.PAID);

    @Override
    public Predicate toPredicate(Root<PaymentModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();

        if(id != null){
            predicates.add(builder.equal(root.get("id"), id));
        }

        if (userId != null) {
            predicates.add(builder.equal(root.get("owner"), userId));
        }

        if(owner != null|| requestedUser != null){
            Join jobJoin = root.join("job");
            if(owner != null) {
                predicates.add(builder.equal(jobJoin.get("creationUser"), owner));
            }

            if(requestedUser != null) {
                predicates.add(builder.equal(jobJoin.get("requestedUser"), requestedUser));
            }
        }

        if(transactionId != null){
            predicates.add(builder.equal(root.get("transactionId"), transactionId));
        }

        if(paypal != null){
            predicates.add(builder.equal(root.get("userPaypal"), paypal));
        }

        predicates.add(builder.in(root.get("status")).value(status));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

