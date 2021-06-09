package com.canu.specifications;

import com.canu.model.PropertyModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertyFilter implements Specification<PropertyModel> {
    PropertyModel.Type type;

    String key;

    boolean isFetchMultiLang = false;

    @Override
    public Predicate toPredicate(Root<PropertyModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();

        if(type != null){
            predicates.add(builder.equal(root.get("type"), type));
        }

        if(key != null){
            predicates.add(builder.equal(root.get("key"), key));
        }

        if(isFetchMultiLang){
            root.fetch("multiLanguage");
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

