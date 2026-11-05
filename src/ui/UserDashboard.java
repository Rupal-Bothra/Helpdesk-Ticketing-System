package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * UserDashboard
 * Dashboard UI for Employee users to raise complaints, view tickets, and manage session.
 * Uses a centered responsive container layout so cards remain centered upon window resize/maximize.
 */
public class UserDashboard extends JFrame {

    private final User currentUser;

    public UserDashboard(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Employee Dashboard - Helpdesk Ticketing System");
        setSize(900, 580);
        setMinimumSize(new Dimension(720, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel lblTitle = new JLabel("Employee Portal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Logged in as: " + currentUser.getName() + " (" + currentUser.getEmail() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(new Color(236, 240, 241));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Cards Panel (Holds the 2 Action Cards)
        JPanel cardsContainer = new JPanel(new GridLayout(1, 2, 30, 30));
        cardsContainer.setOpaque(false);
        cardsContainer.setPreferredSize(new Dimension(760, 340));

        // Card 1: Raise Complaint
        JPanel cardRaise = createCardPanel(
            "Raise Complaint",
            "Submit a new issue or inquiry. Automatic priority will be detected using weighted keyword rules.",
            new Color(39, 174, 96),
            e -> new ComplaintFrame(currentUser, this).setVisible(true)
        );

        // Card 2: My Tickets
        JPanel cardTickets = createCardPanel(
            "My Tickets",
            "View real-time status, priority assignments, and administrative resolution remarks.",
            new Color(142, 68, 173),
            e -> new MyTicketsFrame(currentUser).setVisible(true)
        );

        cardsContainer.add(cardRaise);
        cardsContainer.add(cardTickets);

        // Outer Panel using GridBagLayout to keep Cards Panel centered
        JPanel outerCenteringPanel = new JPanel(new GridBagLayout());
        outerCenteringPanel.setBackground(new Color(245, 247, 250));
        outerCenteringPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        outerCenteringPanel.add(cardsContainer, new GridBagConstraints());

        // Bottom Bar (Logout)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 15));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(110, 35));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        bottomPanel.add(btnLogout);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(outerCenteringPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createCardPanel(String title, String description, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(accentColor);

        JTextArea txtDesc = new JTextArea(description);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        txtDesc.setEditable(false);
        txtDesc.setBackground(Color.WHITE);

        JButton btnAction = new JButton("Open " + title);
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAction.setBackground(accentColor);
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setPreferredSize(new Dimension(140, 38));
        btnAction.addActionListener(action);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(txtDesc, BorderLayout.CENTER);
        card.add(btnAction, BorderLayout.SOUTH);

        return card;
    }
}
