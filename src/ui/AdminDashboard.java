package ui;

import dao.TicketDAO;
import model.Ticket;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

/**
 * AdminDashboard
 * Comprehensive Admin Control Panel featuring ticket management, search/filters,
 * keyword rule management, and a custom-drawn Java 2D live analytics chart panel.
 * Uses responsive layouts so components expand smoothly when window is resized or maximized.
 */
public class AdminDashboard extends JFrame {

    private final User adminUser;
    private final TicketDAO ticketDAO;

    private JTable tableTickets;
    private DefaultTableModel tableModel;

    private JComboBox<String> comboPriorityFilter;
    private JComboBox<String> comboStatusFilter;
    private JTextField txtSearch;
    private AnalyticsChartPanel analyticsPanel;

    public AdminDashboard(User adminUser) {
        this.adminUser = adminUser;
        this.ticketDAO = new TicketDAO();
        initUI();
        loadTickets();
    }

    private void initUI() {
        setTitle("Admin Portal - Helpdesk Ticketing System");
        setSize(1200, 720);
        setMinimumSize(new Dimension(950, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("System Admin Control Center");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Admin: " + adminUser.getName() + " (" + adminUser.getEmail() + ")");
        lblUser.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblUser.setForeground(new Color(236, 240, 241));

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUser, BorderLayout.EAST);

        // Top Action Bar (Buttons)
        JPanel actionToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionToolBar.setBackground(new Color(236, 240, 241));

        JButton btnUpdateTicket = new JButton("Update Selected Ticket");
        btnUpdateTicket.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdateTicket.setBackground(new Color(41, 128, 185));
        btnUpdateTicket.setForeground(Color.WHITE);
        btnUpdateTicket.setPreferredSize(new Dimension(170, 32));

        JButton btnManageKeywords = new JButton("Manage Priority Keywords");
        btnManageKeywords.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnManageKeywords.setBackground(new Color(52, 73, 94));
        btnManageKeywords.setForeground(Color.WHITE);
        btnManageKeywords.setPreferredSize(new Dimension(190, 32));

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setPreferredSize(new Dimension(110, 32));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(90, 32));

        actionToolBar.add(btnUpdateTicket);
        actionToolBar.add(btnManageKeywords);
        actionToolBar.add(btnRefresh);
        actionToolBar.add(btnLogout);

        // Filter & Search Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filters"));

        comboPriorityFilter = new JComboBox<>(new String[]{"ALL", "HIGH", "MEDIUM", "LOW"});
        comboStatusFilter = new JComboBox<>(new String[]{"ALL", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"});
        txtSearch = new JTextField(22);
        txtSearch.setToolTipText("Search title, description, or employee name");

        JButton btnResetFilter = new JButton("Reset Filters");

        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(comboPriorityFilter);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(comboStatusFilter);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnResetFilter);

        // Top Container for Action Bar + Filters
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(actionToolBar, BorderLayout.CENTER);
        topContainer.add(filterPanel, BorderLayout.SOUTH);

        // Table Setup
        String[] columnNames = {"ID", "Employee Name", "Title", "Category", "Priority", "Status", "Admin Remark", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableTickets = new JTable(tableModel);
        tableTickets.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTickets.setRowHeight(25);
        tableTickets.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTickets.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tableTickets.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tableTickets.getColumnModel().getColumn(1).setPreferredWidth(140); // Name
        tableTickets.getColumnModel().getColumn(2).setPreferredWidth(210); // Title
        tableTickets.getColumnModel().getColumn(3).setPreferredWidth(120); // Category
        tableTickets.getColumnModel().getColumn(4).setPreferredWidth(90);  // Priority
        tableTickets.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        tableTickets.getColumnModel().getColumn(6).setPreferredWidth(180); // Remark
        tableTickets.getColumnModel().getColumn(7).setPreferredWidth(140); // Date

        tableTickets.getColumnModel().getColumn(4).setCellRenderer(new MyTicketsFrame.PriorityCellRenderer());
        tableTickets.getColumnModel().getColumn(5).setCellRenderer(new MyTicketsFrame.StatusCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableTickets);

        // Analytics Panel (Custom Java 2D Bar Chart)
        analyticsPanel = new AnalyticsChartPanel();
        analyticsPanel.setMinimumSize(new Dimension(300, 0));

        // Split Pane between Table and Analytics with proportional resizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTable, analyticsPanel);
        splitPane.setDividerLocation(840);
        splitPane.setResizeWeight(0.72); // Allocates extra width to table on resize

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topContainer, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Action Listeners
        comboPriorityFilter.addActionListener(e -> loadTickets());
        comboStatusFilter.addActionListener(e -> loadTickets());
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadTickets();
            }
        });

        btnResetFilter.addActionListener(e -> {
            comboPriorityFilter.setSelectedIndex(0);
            comboStatusFilter.setSelectedIndex(0);
            txtSearch.setText("");
            loadTickets();
        });

        btnRefresh.addActionListener(e -> loadTickets());

        btnUpdateTicket.addActionListener(e -> openUpdateFrame());

        btnManageKeywords.addActionListener(e -> new ManageKeywordsFrame().setVisible(true));

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of Admin Portal?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void loadTickets() {
        tableModel.setRowCount(0);

        String priority = (String) comboPriorityFilter.getSelectedItem();
        String status = (String) comboStatusFilter.getSelectedItem();
        String search = txtSearch.getText().trim();

        List<Ticket> tickets = ticketDAO.getAllTickets(priority, status, search);

        for (Ticket t : tickets) {
            Object[] row = {
                t.getTicketId(),
                t.getUserName() != null ? t.getUserName() : "Employee #" + t.getUserId(),
                t.getTitle(),
                t.getCategory(),
                t.getPriority(),
                t.getStatus(),
                t.getAdminRemark() != null ? t.getAdminRemark() : "-",
                t.getCreatedAt() != null ? t.getCreatedAt().toString() : ""
            };
            tableModel.addRow(row);
        }

        Map<String, Integer> priorityCounts = ticketDAO.getTicketCountsByPriority();
        Map<String, Integer> statusCounts = ticketDAO.getTicketCountsByStatus();
        analyticsPanel.updateData(priorityCounts, statusCounts);
    }

    private void openUpdateFrame() {
        int selectedRow = tableTickets.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ticket from the table to update status or add remarks.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);
        String priority = (String) tableModel.getValueAt(selectedRow, 4);
        String status = (String) tableModel.getValueAt(selectedRow, 5);
        String remark = (String) tableModel.getValueAt(selectedRow, 6);

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketId);
        ticket.setTitle(title);
        ticket.setUserName(userName);
        ticket.setPriority(priority);
        ticket.setStatus(status);
        ticket.setAdminRemark(remark);

        new UpdateTicketFrame(ticket, this::loadTickets).setVisible(true);
    }

    private static class AnalyticsChartPanel extends JPanel {

        private Map<String, Integer> priorityCounts;
        private Map<String, Integer> statusCounts;

        public AnalyticsChartPanel() {
            setBorder(BorderFactory.createTitledBorder("Live Ticket Analytics"));
            setBackground(Color.WHITE);
        }

        public void updateData(Map<String, Integer> priorityCounts, Map<String, Integer> statusCounts) {
            this.priorityCounts = priorityCounts;
            this.statusCounts = statusCounts;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (priorityCounts == null || statusCounts == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();

            // Section 1: Priority Breakdown
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2d.setColor(new Color(41, 128, 185));
            g2d.drawString("Tickets by Priority", 15, 25);

            String[] priorities = {"HIGH", "MEDIUM", "LOW"};
            Color[] priorityColors = {new Color(192, 57, 43), new Color(211, 84, 0), new Color(39, 174, 96)};

            int maxValPriority = 1;
            for (String p : priorities) {
                maxValPriority = Math.max(maxValPriority, priorityCounts.getOrDefault(p, 0));
            }

            int yOffset = 45;
            int maxBarWidth = Math.max(50, width - 120);

            for (int i = 0; i < priorities.length; i++) {
                String p = priorities[i];
                int count = priorityCounts.getOrDefault(p, 0);
                int barWidth = (int) (((double) count / maxValPriority) * maxBarWidth);
                barWidth = Math.max(barWidth, 6);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2d.setColor(Color.BLACK);
                g2d.drawString(p, 15, yOffset + 15);

                g2d.setColor(priorityColors[i]);
                g2d.fillRect(75, yOffset, barWidth, 20);

                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(String.valueOf(count), 85 + barWidth, yOffset + 15);

                yOffset += 32;
            }

            // Separator Line
            g2d.setColor(new Color(220, 220, 220));
            g2d.drawLine(15, yOffset + 10, width - 15, yOffset + 10);

            // Section 2: Status Breakdown
            yOffset += 35;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2d.setColor(new Color(41, 128, 185));
            g2d.drawString("Tickets by Status", 15, yOffset);

            yOffset += 20;
            String[] statuses = {"OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"};
            Color[] statusColors = {
                new Color(41, 128, 185), 
                new Color(230, 126, 34), 
                new Color(39, 174, 96), 
                new Color(127, 140, 141)
            };

            int maxValStatus = 1;
            for (String s : statuses) {
                maxValStatus = Math.max(maxValStatus, statusCounts.getOrDefault(s, 0));
            }

            for (int i = 0; i < statuses.length; i++) {
                String s = statuses[i];
                int count = statusCounts.getOrDefault(s, 0);
                int barWidth = (int) (((double) count / maxValStatus) * maxBarWidth);
                barWidth = Math.max(barWidth, 6);

                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2d.setColor(Color.BLACK);
                g2d.drawString(s, 15, yOffset + 15);

                g2d.setColor(statusColors[i]);
                g2d.fillRect(95, yOffset, barWidth, 20);

                g2d.setColor(Color.DARK_GRAY);
                g2d.drawString(String.valueOf(count), 105 + barWidth, yOffset + 15);

                yOffset += 32;
            }
        }
    }
}
