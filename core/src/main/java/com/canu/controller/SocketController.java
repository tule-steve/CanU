package com.canu.controller;

import com.canu.dto.MessageBean;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@RequiredArgsConstructor
public class SocketController {

    final SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);
    @MessageMapping("/send-message")
    public void send(@Validated @Payload MessageBean message) {
        logger.error("receive message", message);
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + "12", message);
    }

//    @SubscribeMapping("/api/ws")
//    public MessageBean sendWelcomeMessageOnSubscription() {
//        MessageBean welcomeMessage = new MessageBean();
//        welcomeMessage.setMessage("Hello World!");
//        return welcomeMessage;
//    }
}
