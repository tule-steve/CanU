package com.canu;

import com.canu.dto.MessageBean;
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

import java.lang.reflect.Type;
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
        message.setMessage("Hello, Mike!");
        message.setFromUser(36L);
        message.setToUser(12L);
        session.send("/api/send-message", message);

        String test = blockingQueue.poll(1, TimeUnit.SECONDS).getMessage();
        session.disconnect();
    }

    private String getWsPath() {
        return String.format("wss://canui.tech/api/ws", port);
    }
}
