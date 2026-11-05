package ui;

import dao.TicketDAO;
import model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * UpdateTicketFrame
 * Modal GUI for Admin to update ticket status and enter resolution remarks.
 * Uses a centered card layout that remains centered upon window resize/maximize.
 */
public class UpdateTicketFrame extends JFrame {

    private final Ticket ticket;
    private final Runnable onUpdateCallback;

    private JComboBox<String> comboStatus;
    private JTextArea txtRemark;
    private JButton btnSave;
    private JButton btnCancel;
    private TicketDAO ticketDAO;

    public UpdateTicketFrame(Ticket ticket, Runnable onUpdateCallback) {
        this.ticket = ticket;
        this.onUpdateCallback = onUpdateCallback;
        this.ticketDAO = new TicketDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Update Ticket #" + ticket.getTicketId() + " - " + ticket.getTitle());
        setSize(580, 460);
        setMinimumSize(new Dimension(480, 380));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblHeader = new JLabel("Update Ticket Status & Remarks");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        JLabel lblCardTitle = new JLabel("Ticket #" + ticket.getTicketId() + " Resolution Form", SwingConstants.CENTER);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCardTitle.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        cardPanel.add(lblCardTitle, gbc);

        gbc.gridwidth = 1;

        // Info details
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        cardPanel.add(new JLabel("Priority Level:"), gbc);
        JLabel lblPriorityVal = new JLabel(ticket.getPriority() + " Priority");
        lblPriorityVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        cardPanel.add(lblPriorityVal, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        cardPanel.add(new JLabel("Raised By:"), gbc);
        cardPanel.add(new JLabel(ticket.getUserName() != null ? ticket.getUserName() : "Employee"), gbc);

        // Status ComboBox
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        cardPanel.add(new JLabel("Update Status:"), gbc);

        String[] statuses = {"OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"};
        comboStatus = new JComboBox<>(statuses);
        comboStatus.setSelectedItem(ticket.getStatus());
        comboStatus.setPreferredSize(new Dimension(240, 28));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        cardPanel.add(comboStatus, gbc);

        // Remarks JTextArea
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3; gbc.anchor = GridBagConstraints.NORTHWEST;
        cardPanel.add(new JLabel("Admin Remark:"), gbc);

        txtRemark = new JTextArea(5, 20);
        txtRemark.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtRemark.setLineWrap(true);
        txtRemark.setWrapStyleWord(true);
        txtRemark.setText(ticket.getAdminRemark() != null ? ticket.getAdminRemark() : "");
        JScrollPane scrollRemark = new JScrollPane(txtRemark);
        scrollRemark.setPreferredSize(new Dimension(240, 100));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        cardPanel.add(scrollRemark, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);
        btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setPreferredSize(new Dimension(140, 35));

        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(btnSave);
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

        btnSave.addActionListener(this::saveTicketStatus);
        btnCancel.addActionListener(e -> dispose());
    }

    private void saveTicketStatus(ActionEvent e) {
        String newStatus = (String) comboStatus.getSelectedItem();
        String remark = txtRemark.getText().trim();

        boolean updated = ticketDAO.updateTicketStatus(ticket.getTicketId(), newStatus, remark);
        if (updated) {
            JOptionPane.showMessageDialog(this, "Ticket #" + ticket.getTicketId() + " updated successfully!", "Update Success", JOptionPane.INFORMATION_MESSAGE);
            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update ticket status in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
