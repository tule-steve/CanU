package com.canu.specifications;

import com.canu.model.CountryModel;
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

    CountryModel.Locale locale = CountryModel.Locale.en;

    PropertyModel.Type type;

    @Override
    public Predicate toPredicate(Root<PropertyModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("locale"), locale));

        if(type != null){
            predicates.add(builder.equal(root.get("type"), type));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

