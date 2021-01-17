package com.canu.repositories;

import com.canu.model.CanUModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanURepository extends JpaRepository<CanUModel, Long> {
}
