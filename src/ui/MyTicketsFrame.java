package ui;

import dao.TicketDAO;
import model.Ticket;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MyTicketsFrame
 * Swing UI displaying the logged-in employee's submitted tickets in a JTable.
 * Auto-expands table columns proportionally when window is resized or maximized.
 */
public class MyTicketsFrame extends JFrame {

    private final User currentUser;
    private final TicketDAO ticketDAO;
    private JTable tableTickets;
    private DefaultTableModel tableModel;

    public MyTicketsFrame(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("My Submitted Complaints / Tickets");
        setSize(980, 580);
        setMinimumSize(new Dimension(750, 420));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(142, 68, 173));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("My Tickets Status");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JButton btnRefresh = new JButton("Refresh Table");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setPreferredSize(new Dimension(120, 30));
        btnRefresh.addActionListener(e -> loadTickets());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // JTable Setup
        String[] columnNames = {"ID", "Title", "Category", "Priority", "Status", "Admin Remark", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTickets = new JTable(tableModel);
        tableTickets.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTickets.setRowHeight(26);
        tableTickets.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Enable responsive column auto-resizing
        tableTickets.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Column preferred widths
        tableTickets.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableTickets.getColumnModel().getColumn(1).setPreferredWidth(220); // Title
        tableTickets.getColumnModel().getColumn(2).setPreferredWidth(120); // Category
        tableTickets.getColumnModel().getColumn(3).setPreferredWidth(90);  // Priority
        tableTickets.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        tableTickets.getColumnModel().getColumn(5).setPreferredWidth(220); // Remark
        tableTickets.getColumnModel().getColumn(6).setPreferredWidth(150); // Date

        tableTickets.getColumnModel().getColumn(3).setCellRenderer(new PriorityCellRenderer());
        tableTickets.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tableTickets);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTickets() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = ticketDAO.getTicketsByUserId(currentUser.getUserId());

        for (Ticket t : tickets) {
            Object[] row = {
                t.getTicketId(),
                t.getTitle(),
                t.getCategory(),
                t.getPriority(),
                t.getStatus(),
                t.getAdminRemark() != null ? t.getAdminRemark() : "- No remark yet -",
                t.getCreatedAt() != null ? t.getCreatedAt().toString() : ""
            };
            tableModel.addRow(row);
        }
    }

    public static class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String priority = (String) value;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD));

            if (!isSelected) {
                if ("HIGH".equalsIgnoreCase(priority)) {
                    c.setForeground(new Color(192, 57, 43));
                } else if ("MEDIUM".equalsIgnoreCase(priority)) {
                    c.setForeground(new Color(211, 84, 0));
                } else {
                    c.setForeground(new Color(39, 174, 96));
                }
            }
            return c;
        }
    }

    public static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD));

            if (!isSelected) {
                if ("OPEN".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(41, 128, 185));
                } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(230, 126, 34));
                } else if ("RESOLVED".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(39, 174, 96));
                } else {
                    c.setForeground(new Color(127, 140, 141));
                }
            }
            return c;
        }
    }
}
