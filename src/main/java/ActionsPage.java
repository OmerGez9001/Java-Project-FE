import javax.swing.*;
import java.awt.*;

public class ActionsPage extends JFrame {
    public JPanel panel1;
    private JButton workersButton;
    private JButton currentShopInformationButton;
    private JButton customerInformationButton;
    private JButton logsButton;
    private JButton logoutButton;
    private JButton chatButton;
    private JLabel helloText;

    public ActionsPage() throws HeadlessException {
        helloText.setText("Hello, " + BackendClient.instance.getWorkerInformation().getConnectedWorkerName());
        workersButton.addActionListener(e -> WorkersPage.instance());
        currentShopInformationButton.addActionListener(e -> ShopPage.instance());
        customerInformationButton.addActionListener(e -> CustomerPage.instance());
        logsButton.addActionListener(e -> LogPage.instance());
        chatButton.addActionListener(e -> ChatPage.instance());

        logoutButton.addActionListener(e -> {
            LoginPage.instance();
            BackendClient.instance.disconnect();
            this.dispose();
        });

        if (!BackendClient.instance.getWorkerInformation().getJobs().contains(Job.ADMIN))
            workersButton.setVisible(false);
        if (BackendClient.instance.getWorkerInformation().getShopId() == null) {
            currentShopInformationButton.setVisible(false);
        }

    }

    public static JFrame instance() {
        ActionsPage jFrame = new ActionsPage();
        jFrame.setContentPane(jFrame.panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(250,250);
        jFrame.setVisible(true);
        return jFrame;
    }
}
