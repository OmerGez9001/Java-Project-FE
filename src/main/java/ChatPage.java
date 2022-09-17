import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ChatPage extends JFrame {
    private JPanel chatPanel;
    private JTextArea messageArea;
    private JTextField messageText;
    private JButton sendButton;
    private JComboBox shopList;
    private JComboBox ownWorkersInChat;
    private JButton joinChatButton;
    private JButton saveChat;
    private StompSession session;

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

        saveChat.addActionListener(l -> {
            XWPFDocument document = new XWPFDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText("Chat data:");
            header.addBreak();
            String[] text = messageArea.getText().split("\r\n");
            for (String line : text) {
                XWPFRun body = paragraph.createRun();
                body.setText(line);
                body.addBreak();
            }
            try {
                document.write(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeToFile(outputStream);
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        StompSessionHandler sessionHandler = new StompSessionHandler() {

            @Override
            public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message casted = (Message) payload;
                messageArea.append(casted.getSender() + ": " + casted.getContent() + System.lineSeparator());
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
                    messageArea.append(BackendClient.instance.getWorkerInformation().getConnectedWorkerName() + ": " + message.getContent() + System.lineSeparator());
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
        ListenableFuture<StompSession> connect = stompClient.connect("ws://localhost:8080/chat", webSocketHttpHeaders, sessionHandler);
        connect.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {}

            @Override
            public void onSuccess(StompSession result) {
                session = result;
            }
        });
    }

    @SneakyThrows
    public void writeToFile(ByteArrayOutputStream outputStream) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Word File", "docx");
        fileChooser.setFileFilter(filter);
        fileChooser.setVisible(true);
        fileChooser.showSaveDialog(chatPanel);
        File currFile = fileChooser.getSelectedFile();
        if (!FilenameUtils.getExtension(currFile.getName()).equalsIgnoreCase("docx")) {
            currFile = new File(currFile.toString() + ".docx");
        }
        FileUtils.writeByteArrayToFile(currFile, outputStream.toByteArray());
    }

    public static JFrame instance() {
        ChatPage jFrame = new ChatPage();
        jFrame.setContentPane(jFrame.chatPanel);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(300, 400);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                jFrame.dispose();
                jFrame.session.disconnect();
            }
        });

        return jFrame;

    }


}

