package com.canu.repositories;

import com.canu.dto.responses.Member;
import com.canu.model.CanUModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanURepository extends JpaRepository<CanUModel, Long> {

    CanUModel findByEmail(String email);

    @Query(nativeQuery = true)
    List<Member> getMembership(Pageable pageable);

    Optional<CanUModel> findByToken(String token);

//    @Query(nativeQuery = true)
//    List<Member> getMembership(Sort sort);
}
