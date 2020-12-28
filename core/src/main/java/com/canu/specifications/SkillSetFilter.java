package com.canu.specifications;

import com.canu.model.SkillSetModel;
import lombok.Data;
import org.hibernate.criterion.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SkillSetFilter implements Specification<SkillSetModel> {
    private String slug;

    @Override
    public Predicate toPredicate(Root<SkillSetModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (slug != null) {
            predicates.add(builder.equal(root.get("slug"), slug));
        }
        predicates.add(builder.equal(root.get("isDelete"), false));
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

