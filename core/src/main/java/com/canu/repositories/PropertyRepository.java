package com.canu.repositories;

import com.canu.model.PropertyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PropertyRepository
        extends JpaRepository<PropertyModel, Long>, JpaSpecificationExecutor<PropertyModel> {

    //    @Query(value = "select t from PropertyModel t join fetch t.userAssoc a where a.user = ?1 and t.type = 'RATING_CRITERIA'")
    @Query(value = "select t from PropertyModel t left join fetch t.userAssoc a  left join fetch t.positions p where a.user.id = :userId and KEY(p) = :locale and t.type = 'RATING_CRITERIA' ")
    Set<PropertyModel> getRatingCriteria(long userId, String locale);

    @Query(value = "select t from PropertyModel t  left join fetch t.positions p where key(p) = :locale  and  t.type = 'RATING_CRITERIA' ")
    Set<PropertyModel> findAllByTypeAndLocale(String locale);

    PropertyModel findFirstByKey(String key);
}
