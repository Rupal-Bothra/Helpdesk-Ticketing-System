package ui;

import dao.TicketDAO;
import model.Ticket;
import model.User;
import service.PriorityService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * ComplaintFrame
 * Swing UI for Employees to raise a new complaint.
 * Uses a centered card layout that remains centered upon window resize/maximize.
 */
public class ComplaintFrame extends JFrame {

    private final User currentUser;
    private final JFrame parentDashboard;

    private JTextField txtTitle;
    private JComboBox<String> comboCategory;
    private JTextArea txtDescription;
    private JButton btnSubmit;
    private JButton btnCancel;

    private final TicketDAO ticketDAO;
    private final PriorityService priorityService;

    public ComplaintFrame(User user, JFrame parentDashboard) {
        this.currentUser = user;
        this.parentDashboard = parentDashboard;
        this.ticketDAO = new TicketDAO();
        this.priorityService = new PriorityService();
        initUI();
    }

    private void initUI() {
        setTitle("Raise New Complaint - Helpdesk Ticketing System");
        setSize(650, 560);
        setMinimumSize(new Dimension(520, 460));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentDashboard);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        headerPanel.setBackground(new Color(39, 174, 96));
        JLabel lblHeader = new JLabel("Raise New Helpdesk Complaint");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);

        // Card Panel (Form Card)
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
        JLabel lblCardTitle = new JLabel("Complaint Details", SwingConstants.CENTER);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCardTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(lblCardTitle, gbc);

        gbc.gridwidth = 1;

        // Title Field
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.25;
        cardPanel.add(new JLabel("Complaint Title:"), gbc);

        txtTitle = new JTextField(25);
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtTitle.setPreferredSize(new Dimension(280, 28));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.75;
        cardPanel.add(txtTitle, gbc);

        // Category Dropdown
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.25;
        cardPanel.add(new JLabel("Category:"), gbc);

        String[] categories = {
            "IT Infrastructure", 
            "Software", 
            "Hardware", 
            "Network & Internet", 
            "Access & Security", 
            "General"
        };
        comboCategory = new JComboBox<>(categories);
        comboCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCategory.setPreferredSize(new Dimension(280, 28));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.75;
        cardPanel.add(comboCategory, gbc);

        // Description Field
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.25; gbc.anchor = GridBagConstraints.NORTHWEST;
        cardPanel.add(new JLabel("Description:"), gbc);

        txtDescription = new JTextArea(6, 25);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setPreferredSize(new Dimension(280, 120));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.75; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        cardPanel.add(scrollDesc, gbc);

        // Info Note
        JLabel lblNote = new JLabel("<html><i>* Priority (HIGH / MEDIUM / LOW) will be assigned automatically based on keyword rules.</i></html>");
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblNote.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(lblNote, gbc);

        // Buttons Panel inside Card
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnSubmit = new JButton("Submit Complaint");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSubmit.setBackground(new Color(39, 174, 96));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.setPreferredSize(new Dimension(160, 35));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, gbc);

        // Outer Panel using GridBagLayout to keep Card Panel centered
        JPanel outerCenteringPanel = new JPanel(new GridBagLayout());
        outerCenteringPanel.setBackground(new Color(245, 247, 250));
        outerCenteringPanel.add(cardPanel, new GridBagConstraints());

        // Content Pane Assembly
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(outerCenteringPanel, BorderLayout.CENTER);

        // Listeners
        btnSubmit.addActionListener(this::submitComplaint);
        btnCancel.addActionListener(e -> dispose());
    }

    private void submitComplaint(ActionEvent e) {
        String title = txtTitle.getText().trim();
        String category = (String) comboCategory.getSelectedItem();
        String description = txtDescription.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in both Title and Description fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PriorityService.PriorityResult priorityResult = priorityService.calculatePriorityWithDetails(description);
        String assignedPriority = priorityResult.getPriority();

        StringBuilder summary = new StringBuilder();
        summary.append("Auto Priority Engine Evaluation:\n");
        summary.append("--------------------------------------------------\n");
        summary.append("Assigned Priority: ").append(assignedPriority).append("\n");
        summary.append("Calculated Score: ").append(priorityResult.getScore()).append("\n");
        if (!priorityResult.getMatchedKeywords().isEmpty()) {
            summary.append("Detected Keywords: ").append(String.join(", ", priorityResult.getMatchedKeywords())).append("\n");
        } else {
            summary.append("Detected Keywords: None (Default LOW assigned)\n");
        }
        summary.append("--------------------------------------------------\n");
        summary.append("Proceed to submit ticket?");

        int choice = JOptionPane.showConfirmDialog(this, summary.toString(), "Confirm Ticket Submission", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Ticket ticket = new Ticket();
        ticket.setUserId(currentUser.getUserId());
        ticket.setTitle(title);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setPriority(assignedPriority);
        ticket.setStatus("OPEN");

        boolean created = ticketDAO.createTicket(ticket);
        if (created) {
            JOptionPane.showMessageDialog(this, "Complaint ticket submitted successfully!\nPriority Assigned: " + assignedPriority, "Ticket Submitted", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit complaint ticket to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
