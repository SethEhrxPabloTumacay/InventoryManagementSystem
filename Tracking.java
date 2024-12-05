package Inventory;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Tracking extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table1; 
    private JTable table2; 
    private JTable table3; 
    private JTable table;

    private String currentView = "tracking";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory"; 
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "1234"; 

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Tracking frame = new Tracking();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Tracking() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(null);
        sidePanel.setBackground(new Color(18, 25, 38));
        sidePanel.setBounds(0, 0, 250, 729);
        contentPane.add(sidePanel);
        
        JLabel iconLabel = new JLabel("");
        iconLabel.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Icon.png"));
        iconLabel.setBounds(0, 11, 250, 180);
        sidePanel.add(iconLabel);
        
        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Dashboard.png"));
        dashboardButton.setBounds(29, 217, 191, 60);
        sidePanel.add(dashboardButton);
        
        JButton orderButton = new JButton("Order");
        orderButton.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Order.png"));
        orderButton.setBounds(29, 315, 191, 60);
        sidePanel.add(orderButton);
        
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Inventory.png"));
        inventoryButton.setBounds(29, 415, 191, 60);
        sidePanel.add(inventoryButton);
        
        JButton trackingButton = new JButton("Tracking");
        trackingButton.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Tracking.png"));
        trackingButton.setBounds(29, 515, 191, 60);
        sidePanel.add(trackingButton);
        
        JButton reportButton = new JButton("Report");
        reportButton.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Reports.png"));
        reportButton.setBounds(29, 615, 191, 60);
        sidePanel.add(reportButton);
        
        Font customFont = loadCustomFont("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf");

        Font buttonFont = customFont.deriveFont(Font.PLAIN, 16);

        dashboardButton.setFont(buttonFont);
        orderButton.setFont(buttonFont);
        inventoryButton.setFont(buttonFont);
        trackingButton.setFont(buttonFont);
        reportButton.setFont(buttonFont);
        
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(18, 25, 38));
        headerPanel.setBounds(249, 0, 759, 79);
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);
        
        JLabel trackingLabel = new JLabel("TRACKING");
        trackingLabel.setForeground(Color.WHITE);
        trackingLabel.setBounds(300, 21, 181, 36);
        trackingLabel.setFont(loadCustomFont("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf"));
        headerPanel.add(trackingLabel);

        trackingLabel.setFont(customFont.deriveFont(Font.PLAIN, 32));
        headerPanel.add(trackingLabel);
        
        JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setBounds(260, 227, 728, 130);
        contentPane.add(scrollPane1);
        
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        table1.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "Processor", "CPU Cooler", "Motherboard" }
        ));
        configureTable(table1);

        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBounds(260, 368, 728, 130);
        contentPane.add(scrollPane2);
        
        table2 = new JTable();
        scrollPane2.setViewportView(table2); 
        table2.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "RAM", "Video Card", "Storage" }
        ));
        configureTable(table2);

        JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setBounds(260, 509, 728, 130);
        contentPane.add(scrollPane3);
        
        table3 = new JTable();
        scrollPane3.setViewportView(table3); 
        table3.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "PC Casing", "Power Supply Unit", "Cooling" }
        ));
        configureTable(table3);
        
        JScrollPane scrollPane4 = new JScrollPane();
        scrollPane4.setBounds(260, 90, 728, 130);
        contentPane.add(scrollPane4);
        
        table = new JTable();
        scrollPane4.setViewportView(table); 
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "OrderID", "Item Name", "Date", "Stock", "Price", "Category", "Activity" }
        ));
        configureTable(table);

        JPanel panel_1_1 = new JPanel();
        panel_1_1.setBackground(new Color(18, 25, 38));
        panel_1_1.setBounds(249, 650, 759, 79);
        contentPane.add(panel_1_1);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(new Color(18, 25, 38));
        panel_2.setBounds(998, 74, 10, 576);
        contentPane.add(panel_2);
        
        dashboardButton.addActionListener(e -> {
            if (!currentView.equals("dashboard")) {
                dispose();
                new Dashboard().setVisible(true);
            }
        });

        orderButton.addActionListener(e -> {
            if (!currentView.equals("order")) {
                dispose();
                new Order().setVisible(true);
            }
        });

        inventoryButton.addActionListener(e -> {
            if (!currentView.equals("inventory")) {
                dispose();
                new Inventory().setVisible(true);
            }
        });
        
        reportButton.addActionListener(e -> {
            if (!currentView.equals("report")) {
                dispose();
                new Report().setVisible(true);
            }
        });
        
        populateTables();
    }

    private void populateTables() {
        populateProcessorTable();
        populateRAMTable();
        populatePCCasingTable();
        populateOrderTable();
    }

    private void populateProcessorTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ItemName, Category FROM data WHERE Category IN ('Processor', 'CPU Cooler', 'Motherboard') AND Status = 'active' AND Activity IN ('Stored', 'Restocked', 'Shipped')")) {

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            model.setRowCount(0);  

            HashMap<String, java.util.List<String>> categoryMap = new HashMap<>();
            categoryMap.put("Processor", new java.util.ArrayList<>());
            categoryMap.put("CPU Cooler", new java.util.ArrayList<>());
            categoryMap.put("Motherboard", new java.util.ArrayList<>());

            while (rs.next()) {
                String itemName = rs.getString("ItemName");
                String category = rs.getString("Category");

                if (categoryMap.containsKey(category)) {
                    categoryMap.get(category).add(itemName);
                }
            }

            java.util.List<String> processors = categoryMap.get("Processor");
            java.util.List<String> cpuCoolers = categoryMap.get("CPU Cooler");
            java.util.List<String> motherboards = categoryMap.get("Motherboard");

            int maxRows = Math.max(processors.size(), Math.max(cpuCoolers.size(), motherboards.size()));

            for (int i = 0; i < maxRows; i++) {
                String processor = (i < processors.size()) ? processors.get(i) : null;
                String cpuCooler = (i < cpuCoolers.size()) ? cpuCoolers.get(i) : null;
                String motherboard = (i < motherboards.size()) ? motherboards.get(i) : null;

                model.addRow(new Object[] { processor, cpuCooler, motherboard });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void populateRAMTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ItemName, Category FROM data WHERE Category IN ('RAM', 'Video Card', 'Storage') AND Status = 'active' AND Activity IN ('Stored', 'Restocked', 'Shipped')")) {

            DefaultTableModel model = (DefaultTableModel) table2.getModel();
            model.setRowCount(0); 

            HashMap<String, java.util.List<String>> categoryMap = new HashMap<>();
            categoryMap.put("RAM", new java.util.ArrayList<>());
            categoryMap.put("Video Card", new java.util.ArrayList<>());
            categoryMap.put("Storage", new java.util.ArrayList<>());

            while (rs.next()) {
                String itemName = rs.getString("ItemName");
                String category = rs.getString("Category");
                categoryMap.get(category).add(itemName);
            }

            int maxRows = categoryMap.values().stream().mapToInt(java.util.List::size).max().orElse(0);

            for (int i = 0; i < maxRows; i++) {
                String ram = i < categoryMap.get("RAM").size() ? categoryMap.get("RAM").get(i) : null;
                String videoCard = i < categoryMap.get("Video Card").size() ? categoryMap.get("Video Card").get(i) : null;
                String storage = i < categoryMap.get("Storage").size() ? categoryMap.get("Storage").get(i) : null;

                model.addRow(new Object[]{ram, videoCard, storage});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void populatePCCasingTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ItemName, Category FROM data WHERE Category IN ('PC Casing', 'Power Supply Unit', 'Cooling') AND Status = 'active' AND Activity IN ('Stored', 'Restocked', 'Shipped')")) {

            DefaultTableModel model = (DefaultTableModel) table3.getModel();
            model.setRowCount(0); 

            HashMap<String, java.util.List<String>> categoryMap = new HashMap<>();
            categoryMap.put("PC Casing", new java.util.ArrayList<>());
            categoryMap.put("Power Supply Unit", new java.util.ArrayList<>());
            categoryMap.put("Cooling", new java.util.ArrayList<>());

            while (rs.next()) {
                String itemName = rs.getString("ItemName");
                String category = rs.getString("Category");

                if (categoryMap.containsKey(category)) {
                    categoryMap.get(category).add(itemName);
                }
            }

            int maxRows = categoryMap.values().stream()
                    .mapToInt(java.util.List::size)
                    .max()
                    .orElse(0);

            for (int i = 0; i < maxRows; i++) {
                String pcCasing = i < categoryMap.get("PC Casing").size() ? categoryMap.get("PC Casing").get(i) : null;
                String psu = i < categoryMap.get("Power Supply Unit").size() ? categoryMap.get("Power Supply Unit").get(i) : null;
                String cooling = i < categoryMap.get("Cooling").size() ? categoryMap.get("Cooling").get(i) : null;

                model.addRow(new Object[]{pcCasing, psu, cooling});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void populateOrderTable() {
        HashMap<String, Object[]> orderDataMap = new HashMap<>(); 

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT OrderID, ItemName, Date, Quantity, Price, Category, Activity FROM data WHERE Status IN ('active', 'shipped')")) {

        	while (rs.next()) {
                String orderID = rs.getString("OrderID");
                String itemName = rs.getString("ItemName");
                String date = rs.getString("Date");
                String quantity = rs.getString("Quantity");
                double price = rs.getDouble("Price");
                String category = rs.getString("Category");
                String activity = rs.getString("Activity");

                orderDataMap.put(orderID, new Object[]{orderID, itemName, date, quantity, price, category, activity});
            }

            DefaultTableModel model = (DefaultTableModel) table.getModel(); 
            model.setRowCount(0); 

            for (Map.Entry<String, Object[]> entry : orderDataMap.entrySet()) {
             
                model.addRow(entry.getValue());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching order data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultEditor(Object.class, null);  
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            
            table.getColumnModel().getColumn(i).setResizable(false);
        }
        
        table.getTableHeader().setReorderingAllowed(false);

        setColumnWidthByHeader(table, "OrderID", 50);      
        setColumnWidthByHeader(table, "Item Name", 150);   
        setColumnWidthByHeader(table, "Date", 80);        
        setColumnWidthByHeader(table, "Quantity", 50);     
        setColumnWidthByHeader(table, "Price", 90);       
        setColumnWidthByHeader(table, "Category", 120);   
        setColumnWidthByHeader(table, "Activity", 80);    

        table.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 12)); 
    }

    private void setColumnWidthByHeader(JTable table, String columnName, int width) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(columnName)) {
                columnModel.getColumn(i).setPreferredWidth(width);
            }
        }
    }

    private Font loadCustomFont(String path) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, 16); 
        }
    }
}
