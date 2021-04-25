package com.canu.services;

import com.canu.dto.MessageBean;
import com.canu.dto.responses.ParticipantDto;
import com.canu.model.CanUDeletedMessage;
import com.canu.model.CanUModel;
import com.canu.model.JobModel;
import com.canu.repositories.CanUDeletedMessageRepository;
import com.canu.repositories.CanURepository;
import com.canu.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    private static final Logger logger = LoggerFactory.getLogger(RequiredArgsConstructor.class);

    final MessageRepository messRepo;

    final private CanURepository canURepo;

    final private EntityManager em;

    final private CanUDeletedMessageRepository deletedMessRepo;

    final SimpMessagingTemplate simpMessagingTemplate;

    public void sendPaymentCompleteMessage(JobModel job){
        try {
            MessageBean message = new MessageBean();
            message.setFromUser(job.getCreationUser().getId());
            message.setToUser(job.getRequestedUser().getId());
            message.setMessage("####PAYMENTED####_" + job.getId());
            message.updateConversationId();
            message = saveMessage(message);
            logger.error("Starting to send to User");
            simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getToUser(), message);
            logger.error("Starting to send from User");
            simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getFromUser(), message);
            logger.error("Sending all");
        } catch (Exception ex){
            logger.error("Cannot notice payment complete fro job {}", job.getId());
        }
    }

    public MessageBean saveMessage(MessageBean message) {
        return messRepo.save(message);
    }

    public void deleteConservation(Long withUsrId, Long lastedMessageId) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());

        CanUDeletedMessage deletedMessage = deletedMessRepo.findFirstByUserIdAndParticipantId(uUser.getId(), withUsrId)
                                                           .orElse(CanUDeletedMessage.builder()
                                                                                     .lastedMessage(lastedMessageId)
                                                                                     .userId(uUser.getId())
                                                                                     .participantId(withUsrId).build());
        deletedMessage.setLastedMessage(lastedMessageId);
        deletedMessRepo.save(deletedMessage);

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
                "select case when mess.to_user = :userId then mess.from_user  else mess.to_user end as userId," +
                "        u.first_name, " +
                "        u.last_name, " +
                "        u.avatar, " +
                "        mess.message, " +
                "        mess.created_at as createdAt, " +
                "        aggregate.unreadCount " +
                "    from chat_message as mess" +
                "    inner join(" +
                "                select max(u.id)  as id,\n" +
                "                        count(if(u.is_read, null, 1) ) as unreadCount\n" +
                "                from chat_message u" +
                "                left outer join user_deleted_message m on m.user_id = :userId and m.participant_id = case when u.to_user = :userId then u.from_user else u.to_user end " +
                "                where (from_user = :userId or to_user = :userId) and u.id >  coalesce(m.deleted_mess_id, 0)" +
                "                group by conservation_id) as aggregate on mess.id = aggregate.id" +
                "    inner join user u on u.id = case when mess.to_user = :userId then mess.from_user else mess.to_user end;")
                        .setParameter("userId", uUser.getId()).getResultList();

        List<ParticipantDto> responseData = new ArrayList<>();
        String firstName;
        String lastName;
        String avatar;
        for (Object data : result) {
            Object[] row = (Object[]) data;
            firstName = row[1] != null ? row[1].toString() + " " : "";
            lastName = row[2] != null ? row[2].toString() : "";
            avatar = row[3] != null ? row[3].toString() : null;
            responseData.add(ParticipantDto.builder()
                                           .id(Long.parseLong(row[0].toString()))
                                           .name(firstName + lastName)
                                           .avatar(avatar)
                                           .lastMessage(row[4].toString())
                                           .createdAt(((Timestamp) row[5]).toLocalDateTime())
                                           .unreadCount(Long.parseLong(row[6].toString()))
                                           .build());

        }

        return responseData;
    }

    public void markReadMessage(MessageBean mess) {
        try {
            messRepo.markMessAsRead(mess.getFromUser(), mess.getToUser(), mess.getId());
        } catch (Exception ex) {
            logger.error("error on update message", ex);
        }
    }

}
