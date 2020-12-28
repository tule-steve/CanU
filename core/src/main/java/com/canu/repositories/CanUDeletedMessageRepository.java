package com.canu.repositories;

import com.canu.model.CanUDeletedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanUDeletedMessageRepository extends JpaRepository<CanUDeletedMessage, Long> {

    Optional<CanUDeletedMessage> findFirstByUserIdAndParticipantId(Long userId, Long participantId);
}
