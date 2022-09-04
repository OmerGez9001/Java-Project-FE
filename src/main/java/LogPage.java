import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class LogPage extends JFrame {
    private JButton itemsSalesButton;
    private JButton shopSalesButton;
    private JButton categorySalesButton;
    private JPanel panel1;
    private JTable jTable;
    private BackendClient backendClient = BackendClient.instance;

    public LogPage() {
        super();
        panel1 = new JPanel();
        String[] columns = {"Name", "Quantity"};

        createTable(columns);

        panel1.add(itemsSalesButton);
        panel1.add(shopSalesButton);
        panel1.add(categorySalesButton);


        itemsSalesButton.addActionListener(l -> {
            List<SellsPerItemReport> itemLog = backendClient.getItemLog();
            String[][] data = itemLog.stream().map(x -> new String[]{x.getItem().getName(), String.valueOf(x.getCount())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);


        });
        shopSalesButton.addActionListener(l -> {
            List<SellsPerShopReport> shopLog = backendClient.getShopLog();
            String[][] data = shopLog.stream().map(x -> new String[]{x.getShop().getShopName(), String.valueOf(x.getSells())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);


        });
        categorySalesButton.addActionListener(l -> {
            List<SellsPerCategoryReport> categoryLog = backendClient.getCategoryLog();
            String[][] data = categoryLog.stream().map(x -> new String[]{x.getCategory(), String.valueOf(x.getCount())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);

        });

    }

    private void createTable(String[] columns) {
        JPanel panel = new JPanel();
        jTable = new JTable(new DefaultTableModel(new String[][]{}, columns));
        JScrollPane scrollPane = new JScrollPane(jTable);
        jTable.setVisible(true);
        panel.add(scrollPane);
        panel1.add(panel);
    }


    public static JFrame instance() {
        LogPage jFrame = new LogPage();
        jFrame.setContentPane(new LogPage().panel1);
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }
}
