package Inventory;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dashboard extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Font garetFont;
    
    private JLabel lblNewLabel_2;  
    private JLabel lblNewLabel_2_1;  
    private JLabel lblNewLabel_2_2;  

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Dashboard frame = new Dashboard();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Dashboard() {

        garetFont = loadCustomFont("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf", 32);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBounds(249, 650, 759, 79);
        bottomPanel.setBackground(new Color(18, 25, 38));
        contentPane.add(bottomPanel);

        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(249, 0, 759, 79);
        headerPanel.setBackground(new Color(18, 25, 38));
        contentPane.add(headerPanel);
        headerPanel.setLayout(null);

        JLabel titleLabel = new JLabel("DASHBOARD");
        titleLabel.setBounds(300, 21, 220, 40);
        titleLabel.setFont(garetFont);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        createSidePanel();

        createPieChart();

        createBarChart();
    }

    private void createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        fetchDataForPieChart(dataset);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Stored Items", 
                dataset,        
                true,           
                true,           
                false           
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}")); 

        pieChart.getTitle().setFont(loadCustomFont("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf", 24)); 

        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setBounds(260, 201, 365, 438);
        contentPane.add(pieChartPanel);
    }

    private void fetchDataForPieChart(DefaultPieDataset dataset) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "1234");
            String query = "SELECT Category, COUNT(Category) as count FROM data WHERE status = 'active' AND (Activity = 'Stored' OR Activity = 'Restocked') GROUP BY Category"; 
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String itemName = rs.getString("Category");
                int count = rs.getInt("count");
                dataset.setValue(itemName, count); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    private void createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        fetchPriceDataForBarChart(dataset);

        JFreeChart barChart = ChartFactory.createBarChart(
            "Stored Items Overall Price",
            "Category",        
            "Price",           
            dataset            
        );

        barChart.removeLegend();

        barChart.getTitle().setFont(loadCustomFont(
            "C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf",
            24
        )); 

        CategoryPlot plot = barChart.getCategoryPlot();

        CategoryAxis categoryAxis = plot.getDomainAxis();
        categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        categoryAxis.setMaximumCategoryLabelWidthRatio(0.8f); 

        plot.getRangeAxis().setAutoRange(true);

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        ChartPanel barChartPanel = new ChartPanel(barChart);
        barChartPanel.setBounds(630, 201, 368, 438); 
        barChartPanel.setMouseWheelEnabled(false); 
        contentPane.add(barChartPanel);
        
        JPanel StatusPanel = new JPanel();
        StatusPanel.setBackground(new Color(18, 25, 38));
        StatusPanel.setBounds(260, 90, 738, 100);
        contentPane.add(StatusPanel);
        StatusPanel.setLayout(null);
    }


    private void fetchPriceDataForBarChart(DefaultCategoryDataset dataset) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "1234");
            String query = "SELECT Category, SUM(price) as total_price FROM data WHERE status = 'active' AND (Activity = 'Stored' OR Activity = 'Restocked') GROUP BY Category"; 
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String category = rs.getString("Category");
                double totalPrice = rs.getDouble("total_price");
                dataset.addValue(totalPrice, "Total Price", category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(18, 25, 38));
        panel.setBounds(0, 0, 250, 729);
        contentPane.add(panel);

        lblNewLabel_2 = new JLabel("0");
        lblNewLabel_2.setForeground(Color.WHITE);
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_2.setBounds(105, 53, 50, 14);

        lblNewLabel_2_1 = new JLabel("0");
        lblNewLabel_2_1.setForeground(Color.WHITE);
        lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_2_1.setBounds(105, 53, 50, 14);

        lblNewLabel_2_2 = new JLabel("0");
        lblNewLabel_2_2.setForeground(Color.WHITE);
        lblNewLabel_2_2.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_2_2.setBounds(105, 53, 50, 14);

        JPanel StatusPanel = new JPanel();
        StatusPanel.setBackground(new Color(18, 25, 38));
        StatusPanel.setBounds(260, 90, 738, 100); 
        contentPane.add(StatusPanel);
        StatusPanel.setLayout(null);

        JPanel panel_1 = new JPanel();
        panel_1.setBounds(260, 11, 215, 78); 
        panel_1.setBackground(new Color(0, 128, 192));
        panel_1.setLayout(null);
        JLabel lblNewLabel_1 = new JLabel("Shipped");
        lblNewLabel_1.setForeground(new Color(255, 255, 255));
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_1.setBounds(80, 11, 80, 19);
        panel_1.add(lblNewLabel_1);
        panel_1.add(lblNewLabel_2);  
        StatusPanel.add(panel_1);

        JPanel panel_1_1 = new JPanel();
        panel_1_1.setLayout(null);
        panel_1_1.setBackground(new Color(0, 128, 255));
        panel_1_1.setBounds(10, 11, 215, 78);  
        JLabel lblNewLabel_1_1 = new JLabel("Stored");
        lblNewLabel_1_1.setForeground(Color.WHITE);
        lblNewLabel_1_1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_1_1.setBounds(85, 11, 80, 19);
        panel_1_1.add(lblNewLabel_1_1);
        panel_1_1.add(lblNewLabel_2_1); 
        StatusPanel.add(panel_1_1);

        JPanel panel_1_2 = new JPanel();
        panel_1_2.setLayout(null);
        panel_1_2.setBackground(new Color(0, 64, 128));
        panel_1_2.setBounds(513, 11, 215, 78);  
        JLabel lblNewLabel_1_2 = new JLabel("Restocked");
        lblNewLabel_1_2.setForeground(Color.WHITE);
        lblNewLabel_1_2.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNewLabel_1_2.setBounds(70, 11, 100, 19);
        panel_1_2.add(lblNewLabel_1_2);
        panel_1_2.add(lblNewLabel_2_2); 
        StatusPanel.add(panel_1_2);

        Font garetFont = loadCustomFont("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf", 16);

        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Dashboard.png"));
        btnDashboard.setBounds(29, 217, 191, 60);
        btnDashboard.setFont(garetFont);
        panel.add(btnDashboard);

        JButton btnOrder = new JButton("Order");
        btnOrder.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Order.png"));
        btnOrder.setBounds(29, 315, 191, 60);
        btnOrder.setFont(garetFont);
        panel.add(btnOrder);

        JButton btnInventory = new JButton("Inventory");
        btnInventory.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Inventory.png"));
        btnInventory.setBounds(29, 415, 191, 60);
        btnInventory.setFont(garetFont);
        panel.add(btnInventory);

        JButton btnTracking = new JButton("Tracking");
        btnTracking.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Tracking.png"));
        btnTracking.setBounds(29, 515, 191, 60);
        btnTracking.setFont(garetFont);
        panel.add(btnTracking);

        JButton btnReport = new JButton("Report");
        btnReport.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Reports.png"));
        btnReport.setBounds(29, 615, 191, 60);
        btnReport.setFont(garetFont);
        panel.add(btnReport);

        btnInventory.addActionListener(e -> {
            dispose();
            new Inventory().setVisible(true);
        });

        btnOrder.addActionListener(e -> {
            dispose();
            new Order().setVisible(true);
        });

        btnTracking.addActionListener(e -> {
            dispose();
            new Tracking().setVisible(true);
        });

        btnReport.addActionListener(e -> {
            dispose();
            new Report().setVisible(true);
        });
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Icon.png"));
        lblNewLabel.setBounds(0, 11, 250, 180);
        panel.add(lblNewLabel);

        fetchCategoryCounts();
    }

    private void fetchCategoryCounts() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory", "root", "1234");
            String toShipQuery = "SELECT COUNT(*) FROM data WHERE Activity = 'Shipped' OR Status = 'removed'";
            String toStoreQuery = "SELECT COUNT(*) FROM data WHERE Activity = 'Stored' AND Status != 'removed'";
            String toRestockQuery = "SELECT COUNT(*) FROM data WHERE Activity = 'Restocked' AND Status != 'removed'";

            Statement stmt = connection.createStatement();

            ResultSet rsToShip = stmt.executeQuery(toShipQuery);
            if (rsToShip.next()) {
                int toShipCount = rsToShip.getInt(1);
                lblNewLabel_2.setText(String.valueOf(toShipCount)); 
            }

            ResultSet rsToStore = stmt.executeQuery(toStoreQuery);
            if (rsToStore.next()) {
                int toStoreCount = rsToStore.getInt(1);
                lblNewLabel_2_1.setText(String.valueOf(toStoreCount));
            }

            ResultSet rsToRestock = stmt.executeQuery(toRestockQuery);
            if (rsToRestock.next()) {
                int toRestockCount = rsToRestock.getInt(1);
                lblNewLabel_2_2.setText(String.valueOf(toRestockCount)); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Font loadCustomFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }
}
