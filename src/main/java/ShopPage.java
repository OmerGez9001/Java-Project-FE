import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

@Log4j2
public class ShopPage extends JFrame {

    private final BackendClient backendClient = BackendClient.instance;
    private JPanel shopPanel;
    private JLabel shopNameLabel;
    private JLabel ShopIdLabel;
    private JPanel itemsPanel;
    private JLabel customerNumber;
    private JTextField customerNumberField;
    private JLabel customerType;
    private JButton submitButton;
    private JComboBox comboBox1;

    private Transformer transformer = new Transformer();

    public ShopPage() {
        super();
        shopPanel = new JPanel();
        JPanel shopDetails = new JPanel();
        itemsPanel = new JPanel();

        ShopIdLabel.setText("Shop id: " + this.backendClient.getWorkerInformation().getShopId());
        ShopIdLabel.setVerticalAlignment(JLabel.TOP);
        comboBox1.addItem("Buy");
        comboBox1.addItem("Sell");

        customerNumberField.addActionListener(l -> {
            try {
                Customer customer = this.backendClient.getCustomer(customerNumberField.getText());
                this.customerType.setText("Customer Type: " + customer.getType());
                this.customerType.setVisible(true);
            } catch (Exception exception) {
                this.customerType.setVisible(false);
                log.error(exception);
            }
        });


        shopNameLabel.setText("Shop name: " + this.backendClient.getShop(backendClient.getWorkerInformation().getShopId()).getShopName());
        shopNameLabel.setVerticalAlignment(JLabel.TOP);


        String[] columns = {"ItemId", "Name", "Description", "Price", "Quantity"};
        String[][] data = this.backendClient.getShopItems(backendClient.getWorkerInformation().getShopId()).stream().map(x -> new String[]{String.valueOf(x.getItem().getId()), x.getItem().getName(), x.getItem().getDescription(), String.valueOf(x.getItem().getPrice()), String.valueOf(x.getQuantity())}).toArray(String[][]::new);
        shopDetails.setLayout(new GridLayout(0, 1));

        JTable itemsTable = new JTable(new DefaultTableModel(data, columns)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        String[] itemsToBuyColumns = {"ItemId", "Quantity"};


        JTable itemsToBuyTable = new JTable(new DefaultTableModel(new String[][]{}, itemsToBuyColumns));
        itemsTable.setVisible(true);

        JScrollPane existingItemsPanel = new JScrollPane(itemsTable);
        JScrollPane itemsToBuyPanel = new JScrollPane(itemsToBuyTable);

        shopDetails.add(shopNameLabel);
        shopDetails.add(ShopIdLabel);
        shopDetails.add(customerNumber);
        shopDetails.add(customerNumberField);
        shopDetails.add(customerType);
        shopDetails.add(comboBox1);
        shopDetails.add(submitButton);
        customerType.setVisible(false);

        shopPanel.add(shopDetails);
        shopPanel.add(itemsToBuyPanel);
        shopPanel.add(existingItemsPanel);
        this.getContentPane().add(shopPanel);

        Object deleteKey = new Object();
        Object createKey = new Object();

        InputMap im = itemsToBuyTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteKey);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), createKey);
        itemsToBuyTable.getActionMap().put(deleteKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                DefaultTableModel model1 = (DefaultTableModel) itemsToBuyTable.getModel();
                if (model1.getRowCount() > 1) model1.removeRow(itemsToBuyTable.getSelectedRow());
            }
        });
        itemsToBuyTable.getActionMap().put(createKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model1 = (DefaultTableModel) itemsToBuyTable.getModel();
                model1.addRow(new String[]{"", "1"});
            }
        });

        submitButton.addActionListener((l) -> {
            List<ItemTransactionRequest> request = this.transformer.fromShopPage(itemsToBuyTable);

            if (comboBox1.getSelectedItem().equals("Buy")) {
                TransactionResult result = this.backendClient.buy(new TransactionDetails(this.customerNumberField.getText(), this.backendClient.getWorkerInformation().getShopId(), request));
                JOptionPane.showMessageDialog(this, "thank you for buying, " + result);
            } else {
                TransactionResult result = this.backendClient.sell(new TransactionDetails(this.customerNumberField.getText(), this.backendClient.getWorkerInformation().getShopId(), request));
                JOptionPane.showMessageDialog(this, "thank you for selling, " + result);
            }
            ((DefaultTableModel) itemsTable.getModel()).setDataVector(getShopItems(), columns);

        });
    }

    private String[][] getShopItems() {
        return this.backendClient.getShopItems(backendClient.getWorkerInformation().getShopId()).stream().map(x -> new String[]{String.valueOf(x.getItem().getId()), x.getItem().getName(), x.getItem().getDescription(), String.valueOf(x.getItem().getPrice()), String.valueOf(x.getQuantity())}).toArray(String[][]::new);

    }

    public static JFrame instance() {
        ShopPage jFrame = new ShopPage();
        jFrame.pack();
        jFrame.setVisible(true);
        return jFrame;
    }
}
