package com.canu.services;

import com.canu.dto.MessageBean;
import com.canu.model.CanUModel;
import com.canu.repositories.CanURepository;
import com.canu.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    final MessageRepository messRepo;

    final private CanURepository canURepo;

    public MessageBean saveMessage(MessageBean message){
        return messRepo.save(message);
    }

    public List<MessageBean> getChatHistory(Long withUsrId, Pageable p){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CanUModel uUser = canURepo.findByEmail(user.getUsername());
        return messRepo.getMessageHistory(uUser.getId(), withUsrId, p);
    }

//    public void getParticipant(){
//        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        CanUModel uUser = canURepo.findByEmail(user.getUsername());
//
//        messRepo
//
//
//    }


}
