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

    public ActionsPage() throws HeadlessException {
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
        jFrame.setVisible(true);
        return jFrame;
    }
}
