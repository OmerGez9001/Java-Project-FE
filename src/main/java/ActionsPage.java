import javax.swing.*;
import java.awt.*;

public class ActionsPage extends JFrame {
    public JPanel panel1;
    private JButton workersButton;
    private JButton currentShopInformationButton;
    private JButton customerInformationButton;
    private JButton logsButton;

    public ActionsPage() throws HeadlessException {
        workersButton.addActionListener(e -> WorkersPage.instance());
        currentShopInformationButton.addActionListener(e -> ShopPage.instance());
        customerInformationButton.addActionListener(e -> CustomerPage.instance());
        logsButton.addActionListener(e -> LogPage.instance());

        if (!BackendClient.instance.getWorkerInformation().getJobs().contains(Job.ADMIN))
            workersButton.setVisible(false);
        if (BackendClient.instance.getWorkerInformation().getShopId() == null) {
            currentShopInformationButton.setVisible(false);
        }

    }

    public static JFrame instance() {
        JFrame jFrame = new ActionsPage();
        jFrame.setContentPane(new ActionsPage().panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }
}
