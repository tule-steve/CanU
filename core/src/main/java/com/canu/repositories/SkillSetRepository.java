package com.canu.repositories;

import com.canu.model.AuthProviderModel;
import com.canu.model.SkillSetModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillSetRepository extends JpaRepository<SkillSetModel, Long>,
        JpaSpecificationExecutor<SkillSetModel> {
}
