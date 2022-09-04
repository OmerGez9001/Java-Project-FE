import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Transformer {
    public Worker fromWorkersPage(JTable table, int row) {
        List<String> rowValues = getRowValues(table, row);
        Worker worker = new Worker();
        Shop shop = new Shop();
        worker.setWorkerId(rowValues.get(0));
        worker.setId(rowValues.get(1));
        worker.setPassword(rowValues.get(2));
        worker.setPhoneNumber(rowValues.get(3));
        worker.setAccountNumber(rowValues.get(4));
        worker.setShop(shop);
        worker.setJob(Job.valueOf(rowValues.get(5)));
        shop.setId(Long.parseLong(rowValues.get(6)));
        return worker;
    }

    public Customer fromCustomerPage(JTable table, int row) {
        List<String> rowValues = getRowValues(table, row);
        Customer customer = new Customer();
        customer.setType(rowValues.get(0));
        customer.setId(rowValues.get(1));
        customer.setFullName(rowValues.get(2));
        customer.setPhoneNumber(rowValues.get(3));
        return customer;
    }

    public List<ItemTransactionRequest> fromShopPage(JTable table) {
        List<ItemTransactionRequest> itemTransactionRequests = new ArrayList<>();
        for (int row = 0; row < table.getRowCount(); row++) {
            List<String> singleItemQuantity = getRowValues(table, row);
            itemTransactionRequests.add(new ItemTransactionRequest(Long.parseLong(singleItemQuantity.get(0)), Long.parseLong(singleItemQuantity.get(1))));
        }
        return itemTransactionRequests;
    }

    private List<String> getRowValues(JTable table, int row) {
        List<String> rowData = new ArrayList<>();
        for (int i = 0; i < table.getColumnCount(); i++) {
            rowData.add(String.valueOf(table.getValueAt(row, i)));
        }
        return rowData;

    }
}
