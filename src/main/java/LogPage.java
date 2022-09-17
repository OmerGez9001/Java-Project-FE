import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
public class LogPage extends JFrame {
    private JButton itemsSalesButton;
    private JButton shopSalesButton;
    private JButton categorySalesButton;
    private JPanel panel1;
    private JButton export;
    private JButton customerRegisteryButton;
    private JButton workerRegisterButton;
    private JTable jTable;

    private Runnable logReport = null;
    private BackendClient backendClient = BackendClient.instance;

    public LogPage() {
        super();
        panel1 = new JPanel();
        String[] columns = {"Name", "Quantity"};
        String[] registeryColumns = {"Worker ID", "Register ID", "Register Action"};

        createTable(columns);

        panel1.add(itemsSalesButton);
        panel1.add(shopSalesButton);
        panel1.add(categorySalesButton);
        panel1.add(export);
        panel1.add(customerRegisteryButton);
        panel1.add(workerRegisterButton);


        itemsSalesButton.addActionListener(l -> {
            List<SellsPerItemReport> itemLog = backendClient.getItemLog();
            String[][] data = itemLog.stream().map(x -> new String[]{x.getItem().getName(), String.valueOf(x.getCount())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);
            logReport = ()->{
                try {
                    createWordDocForItems(itemLog);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

        });
        shopSalesButton.addActionListener(l -> {
            List<SellsPerShopReport> shopLog = backendClient.getShopLog();
            String[][] data = shopLog.stream().map(x -> new String[]{x.getShop().getShopName(), String.valueOf(x.getSells())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);
            logReport = ()->{
                try {
                    createWordDocForShops(shopLog);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

        });
        categorySalesButton.addActionListener(l -> {
            List<SellsPerCategoryReport> categoryLog = backendClient.getCategoryLog();
            String[][] data = categoryLog.stream().map(x -> new String[]{x.getCategory(), String.valueOf(x.getCount())}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, columns);
            logReport = ()->{
                try {
                    createWordDocForCategory(categoryLog);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

        });

        customerRegisteryButton.addActionListener(l->{
            List<RegisterLog> customerLog = backendClient.getCustomerLog();
            String[][] data= customerLog.stream().map(x-> new String[]{x.getWorkerId(), x.getRegisterId(), x.getRegisterAction().toString()}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, registeryColumns);
            logReport = ()->{
                try {
                    createWordDocForCustomerRegistration(customerLog);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        });

        workerRegisterButton.addActionListener(l->{
            List<RegisterLog> workerLog = backendClient.getWorkerLog();
            String[][] data= workerLog.stream().map(x-> new String[]{}).toArray(String[][]::new);
            ((DefaultTableModel) jTable.getModel()).setDataVector(data, registeryColumns);
            logReport = ()->{
                try {
                    createWordDocForWorkerRegistration(workerLog);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        });

        export.addActionListener(l -> {
            if (logReport != null){
                logReport.run();
            }
        });
    }

    public void createWordDocForWorkerRegistration(List<RegisterLog> workersLog) throws IOException {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XWPFParagraph paragraph = document.createParagraph();

        for (RegisterLog workerLog : workersLog) {
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText(workerLog.getWorkerId());
            header.addBreak();

            XWPFRun body = paragraph.createRun();
            body.setText(workerLog.getRegisterAction().toString());
            body.addBreak();
            body.addBreak();
        }
        document.write(outputStream);
        writeToFile(outputStream);
        outputStream.close();
    }

    public void createWordDocForCustomerRegistration(List<RegisterLog> customersLog) throws IOException {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XWPFParagraph paragraph = document.createParagraph();

        for (RegisterLog customerLog : customersLog) {
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText(customerLog.getRegisterId());
            header.addBreak();

            XWPFRun body = paragraph.createRun();
            body.setText(customerLog.getRegisterAction().toString());
            body.addBreak();
            body.addBreak();
        }
        document.write(outputStream);
        writeToFile(outputStream);
        outputStream.close();
    }

    public void createWordDocForShops(List<SellsPerShopReport> sellsPerShopReports) throws IOException {
        XWPFDocument document = new XWPFDocument();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XWPFParagraph paragraph = document.createParagraph();

        for (SellsPerShopReport sellPerShopReports : sellsPerShopReports) {
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText(sellPerShopReports.getShop().getShopName());
            header.addBreak();

            XWPFRun body = paragraph.createRun();
            body.setText("sells: " + sellPerShopReports.getSells());
            body.addBreak();
            body.addBreak();
        }
        document.write(outputStream);
        writeToFile(outputStream);
        outputStream.close();
    }

    @SneakyThrows
    public void writeToFile(ByteArrayOutputStream outputStream){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Word File", "docx");
        fileChooser.setFileFilter(filter);
        fileChooser.setVisible(true);
        fileChooser.showSaveDialog(panel1);
        File currFile = fileChooser.getSelectedFile();
        if (!FilenameUtils.getExtension(currFile.getName()).equalsIgnoreCase("docx")) {
            currFile = new File(currFile.toString() + ".docx");
        }
        FileUtils.writeByteArrayToFile(currFile, outputStream.toByteArray());
    }

    public void createWordDocForItems(List<SellsPerItemReport> sellsPerItemReport) throws IOException {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XWPFParagraph paragraph = document.createParagraph();

        for (SellsPerItemReport sellPerShopReports : sellsPerItemReport) {
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText(sellPerShopReports.getItem().getName());
            header.addBreak();

            XWPFRun body = paragraph.createRun();
            body.setText("sells: " + sellPerShopReports.getCount());
            body.addBreak();
            body.addBreak();
        }
        document.write(outputStream);
        writeToFile(outputStream);
        outputStream.close();
    }

    public void createWordDocForCategory(List<SellsPerCategoryReport> sellsPerItemReport) throws IOException {
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XWPFParagraph paragraph = document.createParagraph();

        for (SellsPerCategoryReport sellPerShopReports : sellsPerItemReport) {
            XWPFRun header = paragraph.createRun();
            header.setBold(true);
            header.setFontSize(16);
            header.setText(sellPerShopReports.getCategory());
            header.addBreak();

            XWPFRun body = paragraph.createRun();
            body.setText("sells: " + sellPerShopReports.getCount());
            body.addBreak();
            body.addBreak();
        }
        document.write(outputStream);
        writeToFile(outputStream);
        outputStream.close();
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
