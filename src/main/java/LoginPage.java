import javax.swing.*;

public class LoginPage extends JFrame {
    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton loginButton;

    public LoginPage() {
        super();
        loginButton.addActionListener(e -> {
            BackendClient.instance.authenticate(textField1.getText(), String.valueOf(passwordField1.getPassword()));
            ActionsPage.instance();
            this.dispose();
        });
    }

    public static JFrame instance() {
        LoginPage jFrame = new LoginPage();
        jFrame.setContentPane(jFrame.panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setSize(250,150);
        jFrame.setVisible(true);
        return jFrame;
    }
}
