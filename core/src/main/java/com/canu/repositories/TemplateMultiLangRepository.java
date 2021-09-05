package com.canu.repositories;

import com.canu.model.TemplateMultiLangModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateMultiLangRepository extends JpaRepository<TemplateMultiLangModel, Long>,
        JpaSpecificationExecutor<TemplateMultiLangModel> {
}
