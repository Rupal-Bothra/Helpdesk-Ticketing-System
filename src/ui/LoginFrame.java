package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * LoginFrame
 * Main entry GUI screen for user & admin authentication.
 * Uses a centered card layout that remains centered upon window resize/maximize.
 */
public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private UserDAO userDAO;

    public LoginFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Helpdesk Ticketing System - Login");
        setSize(520, 440);
        setMinimumSize(new Dimension(460, 380));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblHeader = new JLabel("Helpdesk Ticketing System");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);

        // Card Panel (Contains actual form)
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220), 1, true),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card Title
        JLabel lblCardTitle = new JLabel("User / Admin Login", SwingConstants.CENTER);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCardTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(lblCardTitle, gbc);

        gbc.gridwidth = 1;

        // Email Field
        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        cardPanel.add(lblEmail, gbc);

        txtEmail = new JTextField(18);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        cardPanel.add(txtEmail, gbc);

        // Password Field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        cardPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(220, 30));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        cardPanel.add(txtPassword, gbc);

        // Buttons Panel inside Card
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(110, 35));

        btnRegister = new JButton("Register New User");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(150, 35));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(buttonPanel, gbc);

        // Outer Panel using GridBagLayout to keep Card Panel perfectly centered
        JPanel outerCenteringPanel = new JPanel(new GridBagLayout());
        outerCenteringPanel.setBackground(new Color(245, 247, 250));
        outerCenteringPanel.add(cardPanel, new GridBagConstraints());

        // Content Pane Assembly
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(outerCenteringPanel, BorderLayout.CENTER);

        // Action Listeners
        btnLogin.addActionListener(this::performLogin);
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void performLogin(ActionEvent e) {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both Email and Password.", 
                "Input Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(email, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Welcome back, " + user.getName() + "!\nLogged in as: " + user.getRole(), 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);

            if (user.isAdmin()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid Email or Password!\nPlease check your credentials and try again.", 
                "Authentication Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
