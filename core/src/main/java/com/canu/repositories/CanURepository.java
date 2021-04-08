package com.canu.repositories;

import com.canu.dto.responses.Member;
import com.canu.model.CanUModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CanURepository extends JpaRepository<CanUModel, Long> {

    CanUModel findByEmail(String email);

    @Query(name = "CanUModel.getMembership", nativeQuery = true)
    Page<Member> getMembership(Pageable pageable);

    Optional<CanUModel> findByToken(String token);

    @Query(value = "select distinct u from SkillSetModel sk inner join sk.canIs ci inner join ci.canUModel u where sk.id in ?1")
    List<CanUModel> findCanIByServices(Set<Long> services);


    //    @Query(nativeQuery = true)
    //    List<Member> getMembership(Sort sort);
}
