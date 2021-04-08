package com.canu.repositories;

import com.canu.dto.MessageBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageBean, Long> {

    @Query("select u from MessageBean u where u.toUser in (?1, ?2) and u.fromUser in (?1, ?2)")
    List<MessageBean> getMessageHistory(Long currUsrId, Long withUsrId, Pageable p);

//    @Query(name = "select case when m.to_user = ?1 then m.from_user else m.to_user end as user, m.message as message from chat_message m" +
//           "            where m.id in" +
//           "            (select max(id)  as id from chat_message" +
//           "            where from_user = ?1 or to_user = ?1" +
//           "            group by conservation_id)", nativeQuery = true)
//    List<Object[]> findParticipant(Long userId);

    @Modifying
    @Query("update MessageBean u set u.isRead = true where u.toUser in (?1, ?2) and u.fromUser in (?1, ?2) and u.id <= ?3")
    void markMessAsRead(Long currUsrId, Long withUsrId, Long messId);
}
