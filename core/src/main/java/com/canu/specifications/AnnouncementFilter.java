package com.canu.specifications;

import com.canu.model.AnnouncementModel;
import com.canu.model.CountryModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Data
public class AnnouncementFilter implements Specification<AnnouncementModel> {

    CountryModel.Locale locale = CountryModel.Locale.en;

    @Override
    public Predicate toPredicate(Root<AnnouncementModel> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.equal(root.get("locale"), locale));
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

