package com.canu.repositories;

import com.canu.model.AnnouncementModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<AnnouncementModel, Long>,
        JpaSpecificationExecutor<AnnouncementModel> {

}
