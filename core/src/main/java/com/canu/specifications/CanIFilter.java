package com.canu.specifications;

import com.canu.model.CanIModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class CanIFilter implements Specification<CanIModel> {
    private List<Long> services = Collections.emptyList();

    String nation;

    List<String> city = Collections.emptyList();

    List<String> serviceType = Collections.emptyList();

    @Override
    public Predicate toPredicate(Root<CanIModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (services.size() > 0) {
            Join skillSetJoin = root.join("skillSets");
            Expression<String> skillSetsExp = skillSetJoin.get("id");
            Predicate skillSetsPredicate = skillSetsExp.in(services);
            predicates.add(skillSetsPredicate);
        }

        if(nation != null){
            predicates.add(builder.equal(root.get("national"), nation));
        }

        if(city.size() > 0){
            predicates.add(builder.in(root.get("area")).value(city));
        }

        if(serviceType.size() > 0){
            predicates.add(builder.in(root.get("serviceType").in(serviceType)));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

