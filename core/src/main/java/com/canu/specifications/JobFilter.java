package com.canu.specifications;

import com.canu.model.JobModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class JobFilter implements Specification<JobModel> {
    private List<Long> services = Collections.emptyList();
    String nation;
    String city;
    Long owner;
    JobModel.JobStatus status;
    Long pickupUserId;
    Long requestedUserId;


    @Override
    public Predicate toPredicate(Root<JobModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (services.size() > 0) {
            Join skillSetJoin = root.join("skillSets");
            Expression<String> skillSetsExp = skillSetJoin.get("id");
            Predicate skillSetsPredicate = skillSetsExp.in(services);
            predicates.add(skillSetsPredicate);
        }

        if(nation != null){
            predicates.add(builder.equal(root.get("nation"), nation));
        }

        if(city != null){
            predicates.add(builder.equal(root.get("city"), city));
        }

        if(owner != null){
            predicates.add(builder.equal(root.get("creationUser"), owner));
        }

        if(status != null){
            predicates.add(builder.equal(root.get("status"), status));
        }

        if(requestedUserId != null){
            predicates.add(builder.equal(root.get("requestedUser"), requestedUserId));
        } else if(pickupUserId != null) {
            predicates.add(builder.equal(root.join("canus").get("id"), pickupUserId));
        }


        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

