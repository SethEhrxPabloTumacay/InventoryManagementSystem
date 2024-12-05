package Inventory;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private RoundedTextField textField;
    private RoundedPasswordField passwordField;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 720, 480);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JPanel panel = new JPanel();
        panel.setBackground(new Color(18, 25, 38));
        panel.setBounds(0, 0, 704, 441);
        contentPane.add(panel);
        panel.setLayout(null);
        
        RoundedPanel panel_1 = new RoundedPanel(30);
        panel_1.setBackground(new Color(32, 40, 54));
        panel_1.setBounds(190, 100, 310, 270);
        panel.add(panel_1);
        panel_1.setLayout(null);
        
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, 
                new File("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\fonts\\Garet\\Garet-Book.ttf")).deriveFont(18f);
            
            JLabel lblNewLabel = new JLabel("Admin Login");
            lblNewLabel.setForeground(new Color(255, 255, 255));
            lblNewLabel.setFont(customFont);
            lblNewLabel.setBounds(100, 20, 120, 30);
            panel_1.add(lblNewLabel);
            
            JLabel lblNewLabel_1 = new JLabel("Username");
            lblNewLabel_1.setFont(customFont.deriveFont(14f));
            lblNewLabel_1.setForeground(Color.WHITE);
            lblNewLabel_1.setBounds(30, 71, 120, 14);
            panel_1.add(lblNewLabel_1);
            
            JLabel lblNewLabel_2 = new JLabel("Password");
            lblNewLabel_2.setFont(customFont.deriveFont(14f));
            lblNewLabel_2.setForeground(Color.WHITE);
            lblNewLabel_2.setBounds(30, 137, 120, 14);
            panel_1.add(lblNewLabel_2);
            
            textField = new RoundedTextField(15);
            textField.setBounds(30, 96, 250, 30);
            panel_1.add(textField);
            
            passwordField = new RoundedPasswordField(15);
            passwordField.setBounds(30, 162, 250, 30);
            panel_1.add(passwordField);
            
            JButton btnNewButton = new JButton("Login");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String enteredUsername = textField.getText();
                    String enteredPassword = new String(passwordField.getPassword());

                    if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter both Username and Password", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        if (enteredUsername.equals("admin") && enteredPassword.equals("1234")) {
                            JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            Dashboard dashboard = new Dashboard();
                            dashboard.setVisible(true);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "You have entered the wrong Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            
            btnNewButton.setBackground(new Color(0, 128, 255));
            btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
            btnNewButton.setBounds(30, 216, 250, 33);
            panel_1.add(btnNewButton);
            
            JLabel lblNewLabel_3 = new JLabel("New label");
            lblNewLabel_3.setIcon(new ImageIcon("C:\\Users\\setht\\Documents\\Data Structure\\InventoryManagementSystem\\src\\inventorymanagementsystem\\resources\\images\\White Icon.png"));
            lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
            lblNewLabel_3.setBounds(280, 11, 127, 78);
            panel.add(lblNewLabel_3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RoundedPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private int cornerRadius;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
    }
}

class RoundedTextField extends JTextField {

    private static final long serialVersionUID = 1L;
    private int cornerRadius;

    public RoundedTextField(int radius) {
        this.cornerRadius = radius;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(getForeground());
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void setBorder(javax.swing.border.Border border) {
    }

    @Override
    public Insets getInsets() {
        return new Insets(10, 10, 10, 10);
    }
}

class RoundedPasswordField extends JPasswordField {

    private static final long serialVersionUID = 1L;
    private int cornerRadius;

    public RoundedPasswordField(int radius) {
        this.cornerRadius = radius;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(getForeground());
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void setBorder(javax.swing.border.Border border) {
    }

    @Override
    public Insets getInsets() {
        return new Insets(10, 10, 10, 10);
    }
}
