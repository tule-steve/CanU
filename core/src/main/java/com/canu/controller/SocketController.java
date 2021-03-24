package com.canu.controller;

import com.canu.dto.MessageBean;
import com.canu.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SocketController {

    final SimpMessagingTemplate simpMessagingTemplate;

    final ChatService chatSvc;

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @MessageMapping("/send-message")
    public void send(@Validated @Payload MessageBean message) {
        logger.error("receive message", message);
        message.updateConversationId();
        message = chatSvc.saveMessage(message);
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getToUser(), message);
    }


    //    @SubscribeMapping("/api/ws")
    //    public MessageBean sendWelcomeMessageOnSubscription() {
    //        MessageBean welcomeMessage = new MessageBean();
    //        welcomeMessage.setMessage("Hello World!");
    //        return welcomeMessage;
    //    }
}
