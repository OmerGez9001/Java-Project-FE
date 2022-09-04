import javax.swing.*;
import java.awt.*;

public class ActionsPage extends JFrame {
    public JPanel panel1;
    private JButton workersButton;

    public ActionsPage() throws HeadlessException {
        workersButton.addActionListener(e -> WorkersPage.instance());
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
