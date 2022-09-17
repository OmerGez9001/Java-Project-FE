import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

@Log4j2
public class CustomerPage extends JFrame {

    private JPanel panel1;
    private JTable customerTable = null;
    private Transformer transformer = new Transformer();
    private BackendClient backendClient = BackendClient.instance;

    public CustomerPage() {
        super();

        List<Customer> customers = BackendClient.instance.allCustomers();

        String[] columns = {"CustomerType", "Id", "FullName", "PhoneNumber"};

        String[][] data = customers.stream().map(x -> new String[]{x.getType(), x.getId(), x.getFullName(), x.getPhoneNumber()}).toArray(String[][]::new);
        DefaultTableModel model = new DefaultTableModel(data, columns);

        JTable workersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(workersTable);
        this.getContentPane().add(scrollPane);
        this.pack();
        this.setVisible(true);
        workersTable.getModel().addTableModelListener(e ->
        {
            if (e.getType() == TableModelEvent.UPDATE)
                try {
                    backendClient.upsertCustomer(transformer.fromCustomerPage(workersTable, e.getFirstRow()));
                } catch (Exception exception) {
                    log.error(exception);
                }

        });
        InputMap im = workersTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object deleteKey = new Object();
        Object createKey = new Object();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteKey);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), createKey);
        workersTable.getActionMap().put(deleteKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int selectedRow = workersTable.getSelectedRow();
                backendClient.deleteCustomer((String) workersTable.getValueAt(selectedRow, 1));
                DefaultTableModel model1 = (DefaultTableModel) workersTable.getModel();
                model1.removeRow(selectedRow);
            }
        });
        workersTable.getActionMap().put(createKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model1 = (DefaultTableModel) workersTable.getModel();
                model1.addRow(new String[]{"", "", "", ""});
            }
        });

    }

    public static JFrame instance() {
        CustomerPage jFrame = new CustomerPage();
        return jFrame;
    }
}
