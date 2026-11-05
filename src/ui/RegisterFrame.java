package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

/**
 * RegisterFrame
 * GUI screen for registering new Employee accounts.
 * Uses a centered card layout that remains centered upon window resize/maximize.
 */
public class RegisterFrame extends JFrame {

    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnBackToLogin;
    private UserDAO userDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public RegisterFrame() {
        userDAO = new UserDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Helpdesk Ticketing System - User Registration");
        setSize(560, 480);
        setMinimumSize(new Dimension(480, 420));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblHeader = new JLabel("Register New Account");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);

        // Card Panel (Contains actual form)
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220), 1, true),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Card Title
        JLabel lblCardTitle = new JLabel("Employee Registration Form", SwingConstants.CENTER);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCardTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(lblCardTitle, gbc);

        gbc.gridwidth = 1;

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        cardPanel.add(new JLabel("Full Name:"), gbc);
        txtName = new JTextField(18);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setPreferredSize(new Dimension(220, 28));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        cardPanel.add(txtName, gbc);

        // Email Address
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        cardPanel.add(new JLabel("Email Address:"), gbc);
        txtEmail = new JTextField(18);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtEmail.setPreferredSize(new Dimension(220, 28));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        cardPanel.add(txtEmail, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.35;
        cardPanel.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setPreferredSize(new Dimension(220, 28));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.65;
        cardPanel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.35;
        cardPanel.add(new JLabel("Confirm Password:"), gbc);
        txtConfirmPassword = new JPasswordField(18);
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtConfirmPassword.setPreferredSize(new Dimension(220, 28));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.65;
        cardPanel.add(txtConfirmPassword, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        btnRegister = new JButton("Register Account");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRegister.setBackground(new Color(39, 174, 96));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new Dimension(150, 35));

        btnBackToLogin = new JButton("Back to Login");
        btnBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setPreferredSize(new Dimension(130, 35));

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBackToLogin);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(buttonPanel, gbc);

        // Outer Panel using GridBagLayout to keep Card Panel centered
        JPanel outerCenteringPanel = new JPanel(new GridBagLayout());
        outerCenteringPanel.setBackground(new Color(245, 247, 250));
        outerCenteringPanel.add(cardPanel, new GridBagConstraints());

        // Content Pane Assembly
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(outerCenteringPanel, BorderLayout.CENTER);

        // Action Listeners
        btnRegister.addActionListener(this::performRegistration);
        btnBackToLogin.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void performRegistration(ActionEvent e) {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required. Please complete the form.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address format (e.g. user@domain.com).", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match! Please re-enter passwords.", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "An account with email '" + email + "' is already registered.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(name, email, password, "EMPLOYEE");
        boolean success = userDAO.register(newUser);

        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in with your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "An error occurred while creating your account.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
