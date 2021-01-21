package com.canu.repositories;

import com.canu.model.AuthProviderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<AuthProviderModel, Long> {
}
