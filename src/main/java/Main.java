import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
       LoginPage.instance();
    }
//    public static void main(String[] args) {
//        WebSocketClient client = new StandardWebSocketClient();
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(client);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
//        webSocketHttpHeaders.add("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIiwiQ0FTSElFUiIsIlNFTExFUiIsIlNISUZUX1NVUEVSVklTT1IiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2FwaS9sb2dpbiIsImV4cCI6MTY1ODI1OTM3MH0.VDKReeWbdSJE-6oC3aYGrxrgBb_Nk6knTvi_47hhfiI");
//
//        StompSessionHandler sessionHandler = new StompSessionHandler() {
//
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                return Message.class;
//            }
//
//            @Override
//            public void handleFrame(StompHeaders headers, Object payload) {
//                Message casted = (Message) payload;
//                System.out.println(casted);
//
//            }
//
//            @Override
//            public void afterConnected(
//                    StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/topic/messages", this);
//                Message message = new Message();
//                message.setFrom("BLeicher");
//                message.setText("Bleich");
//                session.send("/app/chat", message);
//            }
//
//            @Override
//            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
//                System.out.println(exception);
//
//            }
//
//            @Override
//            public void handleTransportError(StompSession session, Throwable exception) {
//                System.out.println(exception);
//
//            }
//        };
//        stompClient.connect("ws://localhost:8080/chat", webSocketHttpHeaders, sessionHandler);
//
//        new Scanner(System.in).nextLine(); // Don't close immediately.
//
//    }
}
