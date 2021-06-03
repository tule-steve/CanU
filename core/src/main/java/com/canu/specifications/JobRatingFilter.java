package com.canu.specifications;

import com.canu.model.JobReviewerModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class JobRatingFilter implements Specification<JobReviewerModel> {

    public Long userId;
    public Boolean isCanI = true;
    public Boolean isGetReview = false;

    @Override
    public Predicate toPredicate(Root<JobReviewerModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();


        if(userId != null) {
            if(isGetReview) {
                predicates.add(builder.equal(root.get("target"), userId));
            } else {
                predicates.add(builder.equal(root.get("reviewer"), userId));
            }

            if (isCanI) {
                predicates.add(builder.equal(root.join("job").get("requestedUser").get("id"), userId));
            } else {
                predicates.add(builder.equal(root.join("job").get("creationUser").get("id"), userId));
            }
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

