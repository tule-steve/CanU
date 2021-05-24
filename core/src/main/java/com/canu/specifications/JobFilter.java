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

    private List<String> keywords = Collections.emptyList();

    List<String> nation = Collections.emptyList();

    List<String> city = Collections.emptyList();

    Long owner;

    JobModel.JobStatus status;

    Long pickupUserId;

    Long requestedUserId;

    @Override
    public Predicate toPredicate(Root<JobModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        criteriaQuery.orderBy(builder.desc(root.get("id")));
        List<Predicate> predicates = new ArrayList<>();
        if (services.size() > 0) {
            Join skillSetJoin = root.join("skillSets");
            Expression<String> skillSetsExp = skillSetJoin.get("id");
            Predicate skillSetsPredicate = skillSetsExp.in(services);
            predicates.add(skillSetsPredicate);
        }

        if (keywords.size() > 0) {
            Join tagJoin = root.join("tags");
            Expression<String> tagExp = tagJoin.get("tag");
            Predicate tagPredicate = tagExp.in(keywords);
            predicates.add(tagPredicate);
        }

        if (nation.size() > 0) {
            predicates.add(builder.in(root.get("nation")).value(nation));
        }

        if (city.size() > 0) {
            predicates.add(builder.in(root.get("city")).value(city));
        }

        if (owner != null) {
            predicates.add(builder.equal(root.get("creationUser"), owner));
        }

        if (status != null) {
            predicates.add(builder.equal(root.get("status"), status));
        }

        if (requestedUserId != null) {
            predicates.add(builder.equal(root.get("requestedUser"), requestedUserId));
        } else if (pickupUserId != null) {
            predicates.add(builder.equal(root.join("canus",JoinType.LEFT).get("id"), pickupUserId));
        }

        predicates.add(builder.isNotNull(root.join("creationUser")));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

