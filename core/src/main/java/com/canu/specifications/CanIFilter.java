package com.canu.specifications;

import com.canu.model.CanIModel;
import com.canu.model.SkillSetModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class CanIFilter implements Specification<CanIModel> {
    private String slug;


    @Override
    public Predicate toPredicate(Root<CanIModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (slug != null) {
            predicates.add(builder.equal(root.get("slug"), slug));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

