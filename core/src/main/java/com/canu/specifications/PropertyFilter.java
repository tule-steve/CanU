package com.canu.specifications;

import com.canu.model.PropertyModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyFilter implements Specification<PropertyModel> {
    PropertyModel.Type type;

    String key;

    @Override
    public Predicate toPredicate(Root<PropertyModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();

        if (type != null) {
            predicates.add(builder.equal(root.get("type"), type));
        }

        if (key != null) {
            predicates.add(builder.equal(root.get("key"), key));
        }

        if (Long.class != criteriaQuery.getResultType()) {
            criteriaQuery.multiselect(root, root.get("multiLanguage"));
            root.fetch("multiLanguage", JoinType.LEFT);
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

