package com.canu.repositories;

import com.canu.model.NotificationDetailModel;
import com.canu.model.TemplateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateModel, Long>{

    TemplateModel findFirstByType(NotificationDetailModel.Type type);



}
