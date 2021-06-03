package com.canu.specifications;

import com.canu.model.PropertyModel;
import com.canu.model.UserPropertyModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class SystemReviewFilter implements Specification<UserPropertyModel> {

    public Long userId;

    @Override
    public Predicate toPredicate(Root<UserPropertyModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        criteriaQuery.distinct(true);

        List<Predicate> predicates = new ArrayList<>();

        if (userId != null) {
            predicates.add(builder.equal(root.get("user"), userId));
        }
        if (Long.class != criteriaQuery.getResultType()) {
            criteriaQuery.multiselect(root, root.get("property"), root.get("user"));

            //fetch join property
            Join properties = (Join) root.fetch("property");
            predicates.add(builder.equal(properties.get("type"), PropertyModel.Type.RATING_CRITERIA));

            //fetch join user
            root.fetch("user");

        } else {
            predicates.add(builder.equal(root.join("property").get("type"), PropertyModel.Type.RATING_CRITERIA));
        }



        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

