package com.canu.specifications;

import com.canu.model.NotificationDetailModel;
import com.canu.model.TemplateModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateFilter implements Specification<TemplateModel> {
    @Enumerated(EnumType.STRING)
    NotificationDetailModel.Type type;

    @Override
    public Predicate toPredicate(Root<TemplateModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (type != null) {
            predicates.add(builder.equal(root.get("type"), type));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

