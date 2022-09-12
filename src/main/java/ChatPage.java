import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.Scanner;

public class ChatPage extends JFrame {
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageText;
    private JButton sendButton;

    public ChatPage(){
        initialize();
    }

    private void initialize(){
        String userName = BackendClient.instance.getWorkerInformation().getConnectedWorkerName();
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", "Bearer " + BackendClient.instance.getWorkerInformation().getJwt().getAccessToken());
        StompSessionHandler sessionHandler = new StompSessionHandler() {

            @Override
            public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message casted = (Message) payload;
                System.out.println(casted);
            }

            @Override
            public void afterConnected(
                    StompSession session, StompHeaders connectedHeaders) {
                // Subscribe to the Public Topic
                session.subscribe("/topic/public", this);
                // Tell your username to the server
               // session.send("/app/chat.register",userName);
                sendButton.addActionListener(e -> {
                    Message message = new Message();
                    message.setSender(userName);
                    message.setContent(messageText.getText());
                    message.setType(Message.MessageType.CHAT);
                    session.send("/app/chat.send", message);
                   messageArea.append(message.getSender() + ": " + message.getContent() + "\n");
                });

            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                System.out.println(exception);

            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.out.println(exception);


            }
        };
        stompClient.connect("ws://localhost:8080/chat", webSocketHttpHeaders, sessionHandler);
    }



    public void onMessageReceived(Message message){

        if(message.getType() == Message.MessageType.JOIN) {

        } else if (message.getType() == Message.MessageType.LEAVE) {

        }
        else {

        }

    }

    public static JFrame instance() {
        ChatPage jFrame = new ChatPage();
        jFrame.setContentPane(new ChatPage().chatPanel);
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }
}

