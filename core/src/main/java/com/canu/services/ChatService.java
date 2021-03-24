package com.canu.services;

import com.canu.dto.MessageBean;
import com.canu.dto.responses.ParticipantDto;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    final MessageRepository messRepo;

    final private CanURepository canURepo;

    final private EntityManager em;

    public MessageBean saveMessage(MessageBean message) {
        return messRepo.save(message);
    }

    public List<MessageBean> getChatHistory(Long withUsrId, Pageable p) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        return messRepo.getMessageHistory(uUser.getId(), withUsrId, p);
    }

    public List<ParticipantDto> getParticipant() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        List result = em.createNativeQuery(
                "select mess.userId," +
                " u.first_name, u.last_name, u.avatar, mess.message , mess.createdAt" +
                " from" +
                "(select case when m.to_user = :userId then m.from_user else m.to_user end as userId, " +
                "   m.message as message,  m.created_at as createdAt" +
                " from chat_message m " +
                " where m.id in " +
                "(select max(id)  as id from chat_message " +
                " where from_user = :userId or to_user = :userId" +
                " group by conservation_id)) as mess" +
                " inner join user u on u.id = mess.userId")
                        .setParameter("userId", uUser.getId()).getResultList();

        List<ParticipantDto> responseData = new ArrayList<>();
        String firstName;
        String lastName;
        String avatar;
        for (Object data : result) {
            Object[] row = (Object[]) data;
            firstName = row[1] != null ? row[1].toString() + " ": "";
            lastName = row[2] != null ? row[2].toString() : "";
            avatar = row[3] != null ? row[3].toString() : null;
            responseData.add(ParticipantDto.builder()
                                           .id(Long.parseLong(row[0].toString()))
                                           .name(firstName + lastName)
                                           .avatar(avatar)
                                           .lastMessage(row[4].toString())
                                           .createdAt(((Timestamp)row[5]).toLocalDateTime())
                                           .build());

        }

        return responseData;
    }

}
