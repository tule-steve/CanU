package com.canu.specifications;

import com.canu.model.CountryModel;
import com.canu.model.FAQModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class FAQFilter implements Specification<FAQModel> {

    List<CountryModel.Locale> locale = Arrays.asList(CountryModel.Locale.en);

    @Override
    public Predicate toPredicate(Root<FAQModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder builder) {
        criteriaQuery.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        if (!(locale.size() == 1 && CountryModel.Locale.all.equals(locale.get(0)))) {
            predicates.add(builder.in(root.get("locale")).value(locale));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}

