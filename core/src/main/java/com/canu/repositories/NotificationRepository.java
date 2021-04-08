package com.canu.repositories;

import com.canu.model.CanUModel;
import com.canu.model.NotificationModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long>{

    List<NotificationModel> findByOwner(CanUModel canu, Pageable p);

    @Modifying
    @Query(value = "update NotificationModel set isRead = true where owner.id = ?1 and detail.id < ?2")
    void markReadNotification(Long userId, Long detailId);

    Long countByIsReadFalseAndOwner(CanUModel canu);



}
