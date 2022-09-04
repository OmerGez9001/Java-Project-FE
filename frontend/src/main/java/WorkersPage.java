import javax.swing.*;
import java.util.List;

public class WorkersPage extends JFrame {

    private JTable table1;
    private JPanel panel1;

    public WorkersPage() {
        super();

        List<Worker> workers = BackendClient.instance.allWorkers();
        System.out.println(workers);

        String[] columns = {"FullName", "Id", "PhoneNumber", "WorkerId", "Shop", "Job"};

        String[][] data = workers.stream().map(x -> new String[]{x.getFullName(), x.getId(), x.getPhoneNumber(), x.getWorkerId(), x.getShop().getShopName(), String.valueOf(x.getJob())}).toArray(String[][]::new);
        table1 = new JTable(data, columns);
        table1.setVisible(true);
        this.setBounds(30, 40, 200, 300);
        JScrollPane sp = new JScrollPane(table1);
        this.add(sp);
        this.setSize(500, 200);
    }

    public static JFrame instance() {
        WorkersPage jFrame = new WorkersPage();
        jFrame.setContentPane(jFrame.panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }
}
