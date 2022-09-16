import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatPage extends JFrame {
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageText;
    private JButton sendButton;
    private JComboBox shopList;

    public ChatPage() {
        initialize();
    }

    private void initialize() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        BackendClient.instance
                .getShops()
                .stream()
                .filter(x -> !x.getId().equals(BackendClient.instance.getWorkerInformation().getShopId()))
                .forEach(shop -> shopList.addItem(shop.getId()));
        webSocketHttpHeaders.add("Authorization", "Bearer " + BackendClient.instance.getWorkerInformation().getJwt().getAccessToken());
        StompSessionHandler sessionHandler = new StompSessionHandler() {

            @Override
            public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message casted = (Message) payload;
                messageArea.append(casted.getSender() + ": " + casted.getContent() + "\n");
                shopList.setEnabled(false);
            }

            @Override
            public void afterConnected(
                    StompSession session, StompHeaders connectedHeaders) {
                // Subscribe to the Public Topic
//                session.subscribe("/topic/public", this);
                session.subscribe("/user/queue/specific-user", this);
                // Tell your username to the server
                // session.send("/app/chat.register",userName);
                sendButton.addActionListener(e -> {
                    Message message = new Message();
                    message.setContent(messageText.getText());
                    message.setShopId((Long) shopList.getSelectedItem());
//                    session.send("/app/chat.send", message);
                    session.send("/app/secured/room", message);
                    messageArea.append(BackendClient.instance.getWorkerInformation().getConnectedWorkerName() + ": " + message.getContent() + "\n");
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


    public static JFrame instance() {
        ChatPage jFrame = new ChatPage();
        jFrame.setContentPane(jFrame.chatPanel);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(300,400);
        jFrame.setVisible(true);
//        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        jFrame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                jFrame.dispose();
//            }
//        });
        return jFrame;
    }


}

