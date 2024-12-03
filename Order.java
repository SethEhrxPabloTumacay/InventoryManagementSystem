package Inventory;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import com.toedter.calendar.JDateChooser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class Order extends JFrame {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private JTextField textField_1; // For Item Name
    private JTextField textField_3; // For Quantity
    private JTextField textField_4; // For Price

    // Track current view
    private String currentView = "order";

    // Declare comboBox and dateChooser as instance variables
    private JComboBox<String> comboBox;
    private JDateChooser dateChooser;

    // Activity linked list
    private ActivityLinkedList activityList;

    // Stock queue for handling stock operations
    private StockQueue stockQueue;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Order frame = new Order();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Connection connectToDatabase() {
        Connection connection = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void loadDataIntoTable() {
        Connection connection = connectToDatabase();
        if (connection != null) {
            String query = "SELECT * FROM data WHERE status IN ('active', 'shipped')";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0); // Clear existing rows
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("OrderID"),
                        rs.getString("ItemName"),
                        rs.getDate("Date"),
                        rs.getInt("Quantity"),
                        formatPrice(rs.getDouble("Price")), // Format price with currency
                        rs.getString("Category"),
                        rs.getString("Activity")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                closeConnection(connection);
            }
        }
    }

    public void insertOrder() {
        // Validate input fields
        if (textField_3.getText().isEmpty() || textField_4.getText().isEmpty() || dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int orderId = getNextOrderID(); // Get the next OrderID
            String itemName = textField_1.getText();
            int quantity = Integer.parseInt(textField_3.getText());
            double price = Double.parseDouble(textField_4.getText());

            // Check if Quantity and Price are greater than 0
            if (quantity <= 0 || price <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity and Price must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String category = (String) comboBox.getSelectedItem();
            String activity = "Stored"; // Set activity to "Stored"

            Connection connection = connectToDatabase();
            if (connection != null) {
                String query = "INSERT INTO data (OrderID, ItemName, Date, Quantity, Price, Category, Activity) VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, orderId); // OrderID
                    pstmt.setString(2, itemName); // Item Name
                    pstmt.setDate(3, new java.sql.Date(dateChooser.getDate().getTime())); // Order Date
                    pstmt.setInt(4, quantity); // Quantity
                    pstmt.setDouble(5, price); // Price
                    pstmt.setString(6, category); // Category
                    pstmt.setString(7, activity); // Activity - set to "Stored"

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Order inserted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    activityList.addActivity("Order Created", orderId);
                    activityList.addActivity("Order Stored", orderId); // Log activity

                    // Enqueue the stock operation
                    stockQueue.enqueue(new StockOperation("add", quantity, orderId));

                    loadDataIntoTable(); // Refresh the table data

                    // Display activities only in console
                    String activities = activityList.displayActivities(orderId);
                    System.out.println(activities); // Log in console

                    // Clear fields after successful order
                    clearFields();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error inserting order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid number format: " + e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                } finally {
                    closeConnection(connection);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearFields() {
        textField_1.setText(""); // Clear Item Name field
        textField_3.setText(""); // Clear Quantity field
        textField_4.setText(""); // Clear Price field
        comboBox.setSelectedIndex(0); // Reset Category combo box
        dateChooser.setDate(null); // Reset date chooser
    }

    private int getNextOrderID() {
        Connection connection = connectToDatabase();
        int nextOrderID = 1; // Default value if no orders exist
        if (connection != null) {
            String query = "SELECT MAX(OrderID) AS maxId FROM data"; // Change 'data' to your actual table name
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    nextOrderID = rs.getInt("maxId") + 1; // Increment max ID by 1
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection(connection);
            }
        }
        return nextOrderID;
    }

    private String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("₱#,##0.00"); // Format to Peso currency
        return df.format(price);
    }

    /**
     * Create the frame.
     */
    public Order() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1024, 768);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Load custom font
        Font garetFont = null;
        try {
            garetFont = Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf"));
            garetFont = garetFont.deriveFont(16f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 25, 38));
        panel.setBounds(0, 0, 250, 729);
        contentPane.add(panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Icon.png"));
        lblNewLabel.setBounds(0, 11, 250, 180);
        panel.add(lblNewLabel);

        // Add buttons with icons
        JButton btnDashboard = new JButton("Dashboard");
        btnDashboard.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Dashboard.png"));
        btnDashboard.setBounds(29, 217, 191, 60);
        if (garetFont != null) btnDashboard.setFont(garetFont);
        panel.add(btnDashboard);

        JButton btnOrder = new JButton("Order");
        btnOrder.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Order.png"));
        btnOrder.setBounds(29, 315, 191, 60);
        if (garetFont != null) btnOrder.setFont(garetFont);
        panel.add(btnOrder);

        JButton btnInventory = new JButton("Inventory");
        btnInventory.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Inventory.png"));
        btnInventory.setBounds(29, 415, 191, 60);
        if (garetFont != null) btnInventory.setFont(garetFont);
        panel.add(btnInventory);

        JButton btnTracking = new JButton("Tracking");
        btnTracking.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Tracking.png"));
        btnTracking.setBounds(29, 515, 191, 60);
        if (garetFont != null) btnTracking.setFont(garetFont);
        panel.add(btnTracking);

        JButton btnReport = new JButton("Report");
        btnReport.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\Reports.png"));
        btnReport.setBounds(29, 615, 191, 60);
        if (garetFont != null) btnReport.setFont(garetFont);
        panel.add(btnReport);

        JLabel lblNewLabel_1 = new JLabel("ORDER LIST");
        lblNewLabel_1.setBounds(525, 21, 200, 36);
        lblNewLabel_1.setForeground(new Color(255, 255, 255)); // White text color
        if (garetFont != null) lblNewLabel_1.setFont(garetFont.deriveFont(32f));
        contentPane.add(lblNewLabel_1);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(249, 79, 759, 300);
        contentPane.add(scrollPane);

        // Initialize JTable
        table = new JTable() {
            private static final long serialVersionUID = 1L;

            // Override to make the table non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };

        table.getTableHeader().setReorderingAllowed(false);  // Disable column reordering
        table.getTableHeader().setResizingAllowed(false);    // Disable column resizing

        // Disable column resizing
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }

        scrollPane.setViewportView(table);

        // Set the table model
        table.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[] { "OrderID", "Item Name", "Date", "Stock", "Price", "Category", "Activity" }
        ));

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(0).setMinWidth(70);
        table.getColumnModel().getColumn(0).setMaxWidth(70);

        table.getColumnModel().getColumn(1).setPreferredWidth(170);
        table.getColumnModel().getColumn(1).setMinWidth(170);
        table.getColumnModel().getColumn(1).setMaxWidth(170);

        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setMinWidth(100);
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setMinWidth(70);
        table.getColumnModel().getColumn(3).setMaxWidth(70);

        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setMinWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(100);

        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setMinWidth(130);
        table.getColumnModel().getColumn(5).setMaxWidth(130);

        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setMinWidth(120);
        table.getColumnModel().getColumn(6).setMaxWidth(120);

        // Center-align the table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply the renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JLabel lblName = new JLabel("Item Name");
        lblName.setBounds(260, 437, 95, 14);
        if (garetFont != null) lblName.setFont(garetFont);
        contentPane.add(lblName);

        JLabel lblDate = new JLabel("Date");
        lblDate.setBounds(511, 437, 46, 14);
        if (garetFont != null) lblDate.setFont(garetFont);
        contentPane.add(lblDate);

        JLabel lblQuantity = new JLabel("Quantity");
        lblQuantity.setBounds(752, 434, 86, 20);
        if (garetFont != null) lblQuantity.setFont(garetFont);
        contentPane.add(lblQuantity);

        JLabel lblPrice = new JLabel("Price");
        lblPrice.setBounds(260, 475, 46, 14);
        if (garetFont != null) lblPrice.setFont(garetFont);
        contentPane.add(lblPrice);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setBounds(511, 474, 82, 17);
        if (garetFont != null) lblCategory.setFont(garetFont);
        contentPane.add(lblCategory);

        textField_1 = new JTextField();
        textField_1.setBounds(361, 431, 140, 27);
        contentPane.add(textField_1); // For Item Name

        textField_3 = new JTextField();
        textField_3.setBounds(848, 429, 150, 30);
        contentPane.add(textField_3); // For Quantity

        textField_4 = new JTextField();
        textField_4.setBounds(361, 469, 140, 27);
        contentPane.add(textField_4); // For Price

        // Initialize combo boxes
        comboBox = new JComboBox<>(new String[]{
            "Processor", "CPU Cooler", "Motherboard", "RAM",
            "Video Card", "Storage", "PC Casing", "Power Supply Unit", "Cooling"
        });
        comboBox.setBounds(608, 468, 130, 28);
        contentPane.add(comboBox);

        // Initialize date chooser
        dateChooser = new JDateChooser();
        dateChooser.setBounds(608, 434, 130, 27);
        contentPane.add(dateChooser);

        // Buttons
        JButton btnOrderNow = new JButton("Order");
        btnOrderNow.setBounds(400, 548, 115, 36);
        contentPane.add(btnOrderNow);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(400, 603, 115, 36);
        contentPane.add(btnUpdate);

        JButton btnCancel = new JButton("Clear");
        btnCancel.setBounds(720, 603, 115, 36);
        contentPane.add(btnCancel);

        JButton btnRemove = new JButton("Remove");
        btnRemove.setBounds(560, 603, 115, 36);
        contentPane.add(btnRemove);

        JButton btnShip = new JButton("Ship");
        btnShip.setBounds(560, 548, 115, 36);
        contentPane.add(btnShip);

        JButton btnRestock = new JButton("Restock");
        btnRestock.setBounds(720, 548, 115, 36);
        contentPane.add(btnRestock);

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(new Color(18, 25, 38));
        panel_1.setBounds(249, 0, 759, 79);
        contentPane.add(panel_1);

        JPanel panel_1_1 = new JPanel();
        panel_1_1.setBackground(new Color(18, 25, 38));
        panel_1_1.setBounds(249, 650, 759, 79);
        contentPane.add(panel_1_1);

        // Initialize the activity linked list
        activityList = new ActivityLinkedList();

        // Initialize the stock queue
        stockQueue = new StockQueue(); // Initialize the stock queue

        // Action Listeners for Navigation Buttons
        btnDashboard.addActionListener(e -> navigateTo("dashboard"));
        btnOrder.addActionListener(e -> navigateTo("order"));
        btnInventory.addActionListener(e -> navigateTo("inventory"));
        btnTracking.addActionListener(e -> navigateTo("tracking"));
        btnReport.addActionListener(e -> navigateTo("report")); 
        
        // Order Now button functionality
        btnOrderNow.addActionListener(e -> insertOrder());

        // Update button functionality
        btnUpdate.addActionListener(e -> loadDataIntoTable());

        // Clear button functionality
        btnCancel.addActionListener(e -> clearFields());

        // Remove button functionality
        btnRemove.addActionListener(e -> {
            int selectedRow = table.getSelectedRow(); // Get selected row index
            if (selectedRow >= 0) { // Check if a row is selected
                int orderId = (int) table.getValueAt(selectedRow, 0); // Assuming OrderID is in the first column
                removeOrder(orderId); // Call removeOrder with selected OrderID
            } else {
                JOptionPane.showMessageDialog(Order.this, "Please select an order to remove.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Ship button functionality
        btnShip.addActionListener(e -> {
            int selectedRow = table.getSelectedRow(); // Get selected row index
            if (selectedRow >= 0) { // Check if a row is selected
                int orderId = (int) table.getValueAt(selectedRow, 0); // Assuming OrderID is in the first column
                int currentQuantity = (int) table.getValueAt(selectedRow, 3); // Get current quantity
                double currentPrice = Double.parseDouble(((String) table.getValueAt(selectedRow, 4)).replace("₱", "").replace(",", "").trim()); // Get the price

                // Check if the item is already shipped
                String activity = (String) table.getValueAt(selectedRow, 6);
                if ("Shipped".equals(activity)) {
                    JOptionPane.showMessageDialog(Order.this, "Cannot ship an order that is already shipped.", "Shipping Error", JOptionPane.WARNING_MESSAGE);
                    return; // Exit the method without proceeding with shipping
                }

                // Prompt for the amount to ship
                String inputQuantity = JOptionPane.showInputDialog(Order.this, "Enter the quantity to ship:");
                if (inputQuantity != null) {
                    try {
                        int amountToShip = Integer.parseInt(inputQuantity);
                        // Check if the amount to ship is valid
                        if (amountToShip <= 0) {
                            JOptionPane.showMessageDialog(Order.this, "Please enter a valid quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        } else if (amountToShip > currentQuantity) {
                            JOptionPane.showMessageDialog(Order.this, "Cannot ship more than the current quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        } else {
                            // Prompt for the new price for the shipped order
                            String inputPrice = JOptionPane.showInputDialog(Order.this, "Enter the new price for the shipped order:");
                            if (inputPrice != null) {
                                try {
                                    double newPrice = Double.parseDouble(inputPrice);

                                    // Call the method to create a shipped order with the new price and current price
                                    createShippedOrder(orderId, amountToShip, currentQuantity, newPrice, currentPrice);
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(Order.this, "Invalid input. Please enter a valid number for price.", "Input Error", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(Order.this, "Invalid input. Please enter a number for quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Order.this, "Please select an order to ship.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Restock button functionality
        btnRestock.addActionListener(e -> {
            int selectedRow = table.getSelectedRow(); // Get selected row index
            if (selectedRow >= 0) { // Check if a row is selected
                int orderId = (int) table.getValueAt(selectedRow, 0); // Assuming OrderID is in the first column
                String activity = (String) table.getValueAt(selectedRow, 6); // Assuming Activity is in the seventh column (index 6)

                if ("Shipped".equals(activity)) {
                    // Display an error message if the item is already shipped
                    JOptionPane.showMessageDialog(Order.this, "Cannot restock an order that is already shipped.", "Restock Error", JOptionPane.WARNING_MESSAGE);
                    return; // Exit the method without proceeding with restock
                }

                // Ask for the quantity to restock
                String quantityInput = JOptionPane.showInputDialog(Order.this, "Enter the quantity to restock:");
                if (quantityInput != null) {
                    try {
                        int amountToRestock = Integer.parseInt(quantityInput);
                        // Check if the amount to restock is valid (greater than 0)
                        if (amountToRestock <= 0) {
                            JOptionPane.showMessageDialog(Order.this, "Please enter a valid quantity greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        } else {
                            // Ask for the price increase
                            String priceInput = JOptionPane.showInputDialog(Order.this, "Enter the price increase:");
                            if (priceInput != null) {
                                try {
                                    double priceIncrease = Double.parseDouble(priceInput);
                                    if (priceIncrease < 0) {
                                        JOptionPane.showMessageDialog(Order.this, "Please enter a valid price increase (greater than or equal to 0).", "Input Error", JOptionPane.WARNING_MESSAGE);
                                    } else {
                                        // Restock the item in the database with the price increase
                                        restockOrder(orderId, amountToRestock, priceIncrease);
                                    }
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(Order.this, "Invalid input. Please enter a valid price.", "Input Error", JOptionPane.WARNING_MESSAGE);
                                }
                            }
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(Order.this, "Invalid input. Please enter a valid number for quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Order.this, "Please select an order to restock.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Load data into the table upon launch
        loadDataIntoTable();
    }
    
    private void navigateTo(String view) {
        if (!currentView.equals(view)) {
            dispose(); // Close the current frame
            switch (view) {
                case "dashboard":
                    new Dashboard().setVisible(true); // Open Dashboard
                    break;
                case "order":
                    new Order().setVisible(true); // Open Order
                    break;
                case "inventory":
                    new Inventory().setVisible(true); // Open Inventory
                    break;
                case "tracking":
                    new Tracking().setVisible(true); // Open Tracking
                    break;
                case "report":
                    new Report().setVisible(true); // Open Report
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown view: " + view, "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }

    private void createShippedOrder(int orderId, int amountShipped, int currentQuantity, double newPrice, double currentPrice) {
        Connection connection = connectToDatabase();
        if (connection != null) {
            String insertQuery = "INSERT INTO data (OrderID, ItemName, Date, Quantity, Price, Category, Activity, Status) " +
                    "SELECT ?, ItemName, ?, ?, ?, Category, 'Shipped', 'Shipped' FROM data WHERE OrderID = ?";
            String updateQuantityAndPriceQuery = "UPDATE data SET Quantity = Quantity - ?, Price = ? WHERE OrderID = ?";
            String deleteQuery = "UPDATE data SET status = 'removed' WHERE OrderID = ?";

            try (PreparedStatement insertPstmt = connection.prepareStatement(insertQuery);
                 PreparedStatement updatePstmt = connection.prepareStatement(updateQuantityAndPriceQuery);
                 PreparedStatement deletePstmt = connection.prepareStatement(deleteQuery)) {

                boolean isFullShipment = (amountShipped == currentQuantity);

                if (!isFullShipment && newPrice <= 0) {
                    JOptionPane.showMessageDialog(this, "Price must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return; // Exit the method if price is invalid
                }

                if (!isFullShipment && newPrice == currentPrice) {
                    JOptionPane.showMessageDialog(this, "New price cannot be the same as the current price unless shipping all items.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return; // Exit the method if the new price is invalid
                }

                insertPstmt.setInt(1, getNextOrderID()); // New OrderID
                insertPstmt.setDate(2, new java.sql.Date(new java.util.Date().getTime())); // Today's date
                insertPstmt.setInt(3, amountShipped); // Quantity to ship
                insertPstmt.setDouble(4, isFullShipment ? currentPrice : newPrice); // Set price based on full shipment
                insertPstmt.setInt(5, orderId); // Original OrderID
                insertPstmt.executeUpdate();

                if (isFullShipment) {
                    deletePstmt.setInt(1, orderId); // Original OrderID
                    deletePstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Order shipped and original order removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    activityList.addActivity("Order Created", orderId);
                    activityList.addActivity("Order Updated", orderId);
                    activityList.addActivity("Order Shipped", orderId); // Log activity

                } else {
                    updatePstmt.setInt(1, amountShipped); // Subtract the quantity for the original order
                    updatePstmt.setDouble(2, currentPrice - newPrice); // Adjust price for the remaining order
                    updatePstmt.setInt(3, orderId); // Original OrderID
                    updatePstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Order created for shipping!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    activityList.addActivity("Order Created", orderId);
                    activityList.addActivity("Order Updated", orderId);
                    activityList.addActivity("Partial Shipment Created for Order: " + orderId, orderId); // Log activity
                }

                loadDataIntoTable(); // Refresh the table data

                // Display activities only in console
                String activities = activityList.displayActivities(orderId);
                System.out.println(activities); // Log in console

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error creating shipping order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                closeConnection(connection);
            }
        }
    }

    // Method to restock an order
    private void restockOrder(int orderId, int amountToRestock, double priceIncrease) {
        Connection connection = connectToDatabase();
        if (connection != null) {
            String selectQuery = "SELECT Quantity, Price FROM data WHERE OrderID = ?";
            String updateQuery = "UPDATE data SET Quantity = ?, Price = ?, Activity = 'Restocked' WHERE OrderID = ?";

            try (PreparedStatement selectPstmt = connection.prepareStatement(selectQuery);
                 PreparedStatement updatePstmt = connection.prepareStatement(updateQuery)) {

                selectPstmt.setInt(1, orderId);
                ResultSet rs = selectPstmt.executeQuery();

                if (rs.next()) {
                    int originalQuantity = rs.getInt("Quantity");
                    double originalPrice = rs.getDouble("Price");

                    int newQuantity = originalQuantity + amountToRestock;
                    double newPrice = originalPrice + priceIncrease; // Add the price increase

                    if (newPrice < originalPrice) {
                        JOptionPane.showMessageDialog(this, "New price cannot exceed the current price.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        return; // Exit the method if price is invalid
                    }

                    updatePstmt.setInt(1, newQuantity); // New quantity
                    updatePstmt.setDouble(2, newPrice); // Updated price
                    updatePstmt.setInt(3, orderId); // The same OrderID

                    int rowsAffected = updatePstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Order restocked and price updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        activityList.addActivity("Order Created", orderId);
                        activityList.addActivity("Order Restocked", orderId); // Log activity
                        loadDataIntoTable(); // Refresh the table data

                        // Display activities only in console
                        String activities = activityList.displayActivities(orderId);
                        System.out.println(activities); // Log in console
                    } else {
                        JOptionPane.showMessageDialog(this, "Order update failed.", "Update Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Order not found.", "Order Error", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error restocking order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                closeConnection(connection);
            }
        }
    }

    // Confirmation dialog for removing an order
    public void removeOrder(int orderId) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this order?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            Connection connection = connectToDatabase();
            if (connection != null) {
                String query = "UPDATE data SET status = 'removed' WHERE OrderID = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    pstmt.setInt(1, orderId);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Order has been removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        activityList.addActivity("Order Removed: " + orderId, orderId); // Log activity
                        loadDataIntoTable(); // Refresh the table data

                        // Display activities only in console
                        String activities = activityList.displayActivities(orderId);
                        System.out.println(activities); // Log in console
                    } else {
                        JOptionPane.showMessageDialog(this, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error removing order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    closeConnection(connection);
                }
            }
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}