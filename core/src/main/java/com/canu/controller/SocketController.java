package com.canu.controller;

import com.canu.dto.MessageBean;
import com.canu.dto.requests.File64Dto;
import com.canu.model.NotificationModel;
import com.canu.services.CanUService;
import com.canu.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SocketController {

    final SimpMessagingTemplate simpMessagingTemplate;

    final ChatService chatSvc;

    final CanUService canuSvc;

    @Value("${app.baseUrl}")
    private String domainLink;

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @MessageMapping("/send-message")
    public void send(@Validated @Payload MessageBean message) {
        message.updateConversationId();
        message = chatSvc.saveMessage(message);
        logger.error("Starting to send to User");
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getToUser(), message);
        logger.error("Starting to send from User");
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getFromUser(), message);
        logger.error("Sending all");
    }

    @MessageMapping("/send-file")
    public void send(@Validated @Payload File64Dto message) throws IOException {
        byte[] decodeFile = Base64.getDecoder()
                                  .decode(message.getEncrypt64File().getBytes(StandardCharsets.UTF_8));
        MessageBean messageEntity = new MessageBean();
        messageEntity.setToUser(message.getToUser());
        messageEntity.setFromUser(message.getFromUser());
        messageEntity.updateConversationId();
        messageEntity.setIsUploadedFile(true);

        Path uri = Paths.get("/images/static/",messageEntity.getConservationId(),
                             UUID.randomUUID().toString(),
                             message.getFileName());
        Path destinationFile = Paths.get(System.getProperty("user.dir") ,
                                         uri.toString());
        Files.createDirectories(destinationFile.getParent());
        Files.write(destinationFile, decodeFile, StandardOpenOption.CREATE_NEW);
        messageEntity.setMessage(domainLink + uri.toString());
        messageEntity = chatSvc.saveMessage(messageEntity);
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getToUser(), messageEntity);
        simpMessagingTemplate.convertAndSend("/api/topic/user/" + message.getFromUser(), messageEntity);
    }


    @MessageMapping("/read-message")
    public void read(@Validated @Payload MessageBean message) {
        message.updateConversationId();
        chatSvc.markReadMessage(message);
    }

    @MessageMapping("/read-notification")
    public void readNotification(@Validated @Payload NotificationModel notification) {
        canuSvc.markNotificationRead(notification);
    }

    //    @SubscribeMapping("/api/ws")
    //    public MessageBean sendWelcomeMessageOnSubscription() {
    //        MessageBean welcomeMessage = new MessageBean();
    //        welcomeMessage.setMessage("Hello World!");
    //        return welcomeMessage;
    //    }


}
