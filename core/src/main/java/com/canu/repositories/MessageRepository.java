package com.canu.repositories;

import com.canu.dto.MessageBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageBean, Long> {

    @Query("select u from MessageBean u where u.toUser in (?1, ?2) and u.fromUser in (?1, ?2)")
    List<MessageBean> getMessageHistory(Long currUsrId, Long withUsrId, Pageable p);

//    @Query("select u from MessageBean m where m.id in (select max(l.id) from MessageBean l group by )")
//    List<MessageBean> getMessageHistory(Long currUsrId, Long withUsrId, Pageable p);
}
