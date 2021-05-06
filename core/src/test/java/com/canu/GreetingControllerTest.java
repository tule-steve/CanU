package com.canu;

import com.canu.dto.MessageBean;
import com.canu.dto.requests.File64Dto;
import com.sun.tools.javac.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GreetingControllerTest {

    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setup() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

//    @Test
    public void verifyGreetingIsReceived() throws Exception {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        BlockingQueue<MessageBean> blockingQueue = new ArrayBlockingQueue(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/api/topic/user/12", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageBean.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((MessageBean) payload);
            }
        });
        MessageBean message = new MessageBean();
        message.setMessage("Hello, Mike!123");
        message.setFromUser(36L);
        message.setToUser(12L);
        session.send("/api/send-message", message);
//        Thread.sleep(3000);
        blockingQueue.poll(5, TimeUnit.SECONDS);
//        session.disconnect();
    }

//    @Test
    public void sendFile() throws Exception {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));

        BlockingQueue<MessageBean> blockingQueue = new ArrayBlockingQueue(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(getWsPath(), new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/api/topic/user/15", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageBean.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((MessageBean) payload);
            }
        });
        File64Dto message = new File64Dto();
        String base64File = "";
        File file = new File("/Users/yamada/Downloads/CanU.csv");

        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a file from file system
            byte fileData[] = new byte[(int) file.length()];
            imageInFile.read(fileData);
            base64File = Base64.getEncoder().encodeToString(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the file " + ioe);
        }
        message.setEncrypt64File(base64File);
        message.setFileName("CanU.csv");
        message.setFromUser(36L);
        message.setToUser(15L);
        session.send("/api/send-file", message);

        MessageBean result = blockingQueue.poll(10, TimeUnit.SECONDS);
        result = null;

//        session.disconnect();
    }

    private String getWsPath() {
        return String.format("wss://localhost:9129/api/ws", port);
    }
}
