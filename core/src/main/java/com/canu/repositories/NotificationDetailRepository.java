package com.canu.repositories;

import com.canu.model.NotificationDetailModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDetailRepository extends JpaRepository<NotificationDetailModel, Long>{

    List<NotificationDetailModel> findByIdGreaterThanEqual(Long id);

    Page<NotificationDetailModel> findByType(NotificationDetailModel.Type type, Pageable p);



}
