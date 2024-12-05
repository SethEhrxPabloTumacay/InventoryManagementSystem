package Inventory;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Report extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private JTable table_1;

    private String currentView = "report";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    private JTable table_2;
    private JTable table_3;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Report frame = new Report();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Report() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        Font garetFont = null;
        try {
            File fontFile = new File("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf");
            garetFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Font.PLAIN, 16);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(garetFont);
        } catch (Exception e) {
            e.printStackTrace();
            garetFont = new Font("Arial", Font.PLAIN, 18); 
        }

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(18, 25, 38));
        panel.setBounds(0, 0, 250, 729);
        contentPane.add(panel);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Icon.png"));
        lblNewLabel.setBounds(0, 11, 250, 180);
        panel.add(lblNewLabel);

        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setFont(garetFont);
        btnDashboard.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Dashboard.png"));
        btnDashboard.setBounds(29, 217, 191, 60);
        panel.add(btnDashboard);

        JButton btnOrder = new JButton("Order");
        btnOrder.setFont(garetFont);
        btnOrder.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Order.png"));
        btnOrder.setBounds(29, 315, 191, 60);
        panel.add(btnOrder);

        JButton btnInventory = new JButton("Inventory");
        btnInventory.setFont(garetFont);
        btnInventory.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Inventory.png"));
        btnInventory.setBounds(29, 415, 191, 60);
        panel.add(btnInventory);

        JButton btnTracking = new JButton("Tracking");
        btnTracking.setFont(garetFont);
        btnTracking.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Tracking.png"));
        btnTracking.setBounds(29, 515, 191, 60);
        panel.add(btnTracking);

        JButton btnReport = new JButton("Report");
        btnReport.setFont(garetFont);
        btnReport.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Reports.png"));
        btnReport.setBounds(29, 615, 191, 60);
        panel.add(btnReport);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(new Color(18, 25, 38));
        panel_1.setBounds(249, 0, 759, 79);
        contentPane.add(panel_1);

        JLabel lblNewLabel_1 = new JLabel("REPORT");
        lblNewLabel_1.setBounds(315, 21, 150, 36);
        lblNewLabel_1.setForeground(new Color(255, 255, 255));
        lblNewLabel_1.setFont(garetFont.deriveFont(32f));
        panel_1.setLayout(null);
        panel_1.add(lblNewLabel_1);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(260, 90, 360, 278);
        contentPane.add(scrollPane);

        DefaultTableModel model1 = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Recently Added Item", "Stock", "Category"
            }
        );

        table = new JTable(model1);
        scrollPane.setViewportView(table);
        configureTable(table);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(630, 90, 360, 278);
        contentPane.add(scrollPane_1);

        DefaultTableModel model2 = new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Recently Removed Item", "Category"
            }
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table_1 = new JTable(model2);
        scrollPane_1.setViewportView(table_1);
        configureTable(table_1);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(18, 25, 38));
        bottomPanel.setBounds(249, 650, 759, 79);
        contentPane.add(bottomPanel);
        
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(630, 379, 360, 260);
        contentPane.add(scrollPane_2);
        
        table_2 = new JTable();
        scrollPane_2.setViewportView(table_2);
        table_2.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Category", "Stock Levels"
            }
        ));
        
        configureTable(table_2);
        table_2.setRowHeight(30); 

        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(260, 379, 360, 260);
        contentPane.add(scrollPane_3);

        table_3 = new JTable();
        scrollPane_3.setViewportView(table_3);
        table_3.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] {
                "Recently Restocked Item", "Stock", "Category"
            }
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false; 
            }
        });

        configureTable(table_3);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(new Color(18, 25, 38));
        panel_2.setBounds(1000, 77, 10, 575);
        contentPane.add(panel_2);

        btnDashboard.addActionListener(e -> navigateTo("dashboard"));
        btnOrder.addActionListener(e -> navigateTo("order"));
        btnInventory.addActionListener(e -> navigateTo("inventory"));
        btnTracking.addActionListener(e -> navigateTo("tracking"));

        updateTables();  
    }

    private void configureTable(JTable table) {
        table.setDefaultEditor(Object.class, null); 
        table.getTableHeader().setReorderingAllowed(false); 
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        TableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer) centerRenderer).setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        if (table == this.table) { 
            table.getColumnModel().getColumn(0).setPreferredWidth(190);
            table.getColumnModel().getColumn(0).setMinWidth(190);
            table.getColumnModel().getColumn(0).setMaxWidth(190);

            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setMinWidth(50);
            table.getColumnModel().getColumn(1).setMaxWidth(50);

            table.getColumnModel().getColumn(2).setPreferredWidth(120);
            table.getColumnModel().getColumn(2).setMinWidth(120);
            table.getColumnModel().getColumn(2).setMaxWidth(120);
        } else if (table == this.table_3) {
            table.getColumnModel().getColumn(0).setPreferredWidth(190);
            table.getColumnModel().getColumn(0).setMinWidth(190);
            table.getColumnModel().getColumn(0).setMaxWidth(190);

            table.getColumnModel().getColumn(1).setPreferredWidth(50);
            table.getColumnModel().getColumn(1).setMinWidth(50);
            table.getColumnModel().getColumn(1).setMaxWidth(50);

            table.getColumnModel().getColumn(2).setPreferredWidth(120);
            table.getColumnModel().getColumn(2).setMinWidth(120);
            table.getColumnModel().getColumn(2).setMaxWidth(120);
        }
    }

    private void updateTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            HashMap<String, Integer> stockLevels = new HashMap<>();
            String sql1 = "SELECT ItemName, Quantity, Category FROM data WHERE status = 'active' AND Activity IN ('Stored') ORDER BY timestamp DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs1 = stmt.executeQuery(sql1)) {
                DefaultTableModel model1 = (DefaultTableModel) table.getModel();
                model1.setRowCount(0);
                while (rs1.next()) {
                    String itemName = rs1.getString("ItemName");
                    int quantity = rs1.getInt("Quantity");
                    String category = rs1.getString("Category");
                    model1.addRow(new Object[] { itemName, quantity, category });
                    stockLevels.put(category, stockLevels.getOrDefault(category, 0) + quantity);
                }
            }

            String sql2 = "SELECT ItemName, Category FROM data WHERE status IN ('removed', 'shipped') ORDER BY timestamp DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs2 = stmt.executeQuery(sql2)) {
                DefaultTableModel model2 = (DefaultTableModel) table_1.getModel();
                model2.setRowCount(0);
                while (rs2.next()) {
                    String itemName = rs2.getString("ItemName");
                    String category = rs2.getString("Category");
                    model2.addRow(new Object[] { itemName, category });
                }
            }

            DefaultTableModel model3 = (DefaultTableModel) table_2.getModel();
            model3.setRowCount(0); 
            for (Map.Entry<String, Integer> entry : stockLevels.entrySet()) {
                model3.addRow(new Object[] { entry.getKey(), entry.getValue() });
            }

            String sql4 = "SELECT ItemName, Quantity, Category FROM data WHERE status = 'active' AND Activity IN ('Restocked') ORDER BY timestamp DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs4 = stmt.executeQuery(sql4)) {
                DefaultTableModel model4 = (DefaultTableModel) table_3.getModel();
                model4.setRowCount(0);
                while (rs4.next()) {
                    String itemName = rs4.getString("ItemName");
                    int quantity = rs4.getInt("Quantity");
                    String category = rs4.getString("Category");
                    model4.addRow(new Object[] { itemName, quantity, category });
                  
                    stockLevels.put(category, stockLevels.getOrDefault(category, 0) + quantity);
                }
            }

            model3.setRowCount(0);
            for (Map.Entry<String, Integer> entry : stockLevels.entrySet()) {
                model3.addRow(new Object[] { entry.getKey(), entry.getValue() });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void navigateTo(String view) {
        if (!currentView.equals(view)) {
            dispose();
            switch (view) {
                case "dashboard":
                    new Dashboard().setVisible(true);
                    break;
                case "order":
                    new Order().setVisible(true);
                    break;
                case "inventory":
                    new Inventory().setVisible(true);
                    break;
                case "tracking":
                    new Tracking().setVisible(true);
                    break;
            }
        }
    }
}
