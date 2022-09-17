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

public class ChatPage extends JFrame {
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageText;
    private JButton sendButton;
    private JComboBox shopList;
    private JComboBox ownWorkersInChat;
    private JButton joinChatButton;

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

        if (BackendClient.instance.getWorkerInformation().getJobs().contains(Job.SHIFT_SUPERVISOR)) {
            BackendClient.instance.allWorkersInChat().forEach(x -> ownWorkersInChat.addItem(x));
        } else {
            ownWorkersInChat.setVisible(false);
            joinChatButton.setVisible(false);
        }
        joinChatButton.addActionListener(l -> {
            BackendClient.instance.addManagerToChat(String.valueOf(ownWorkersInChat.getSelectedItem()));
        });

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
                session.subscribe("/user/queue/specific-user", this);
                sendButton.addActionListener(e -> {
                    Message message = new Message();
                    message.setContent(messageText.getText());
                    message.setShopId((Long) shopList.getSelectedItem());
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
        jFrame.setSize(300, 400);
        jFrame.setVisible(true);
        return jFrame;
    }


}

