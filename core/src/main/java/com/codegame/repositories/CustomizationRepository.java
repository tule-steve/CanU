package com.codegame.repositories;

import com.codegame.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomizationRepository {
    final EntityManager em;

    public List<ItemDto> getItemDataList(){
        StringBuilder sb = new StringBuilder();
        sb.append("select i.id, i.description, count(gc.id), i.price");
        sb.append(" from Item i ");
        sb.append(" join i.codes gc ");
        sb.append(" group by i.id");

        Query query = em.createQuery(sb.toString());
//        query=  query.unwrap(org.hibernate.query.Query.class)
//                    .setResultTransformer(
//                            new ResultTransformer() {
//                                @Override
//                                public Object transformTuple(
//                                        Object[] tuple,
//                                        String[] aliases) {
//                                    return new ItemDto(
//                                            (Long) tuple[0],
//                                            (String) tuple[1],
//                                            (int) tuple[2],
//                                            (int) tuple[3],
//                                            null,
//                                            null);
//                                }
//
//                                @Override
//                                public List transformList(List collection) {
//                                    return collection;
//                                }
//                            }
//                    );

        return query.getResultList();
    }
}
