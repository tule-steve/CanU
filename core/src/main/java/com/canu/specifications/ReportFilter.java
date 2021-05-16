package com.canu.specifications;

import com.canu.model.JobModel;
import com.canu.model.SupportRequestModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReportFilter implements Specification<SupportRequestModel> {

    String email;

    String phone;

    JobModel.JobStatus status;

    @Override
    public Predicate toPredicate(Root<SupportRequestModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        if (email != null) {
            predicates.add(builder.equal(root.get("email"), email));
        }

        if (phone != null) {
            predicates.add(builder.equal(root.get("phone"), phone));
        }

        if (status != null) {
            predicates.add(builder.equal(root.get("status"), status));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

