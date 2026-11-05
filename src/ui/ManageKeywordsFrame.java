package ui;

import dao.KeywordDAO;
import model.Keyword;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * ManageKeywordsFrame
 * Admin UI screen for managing auto-priority keyword definitions, weights, and priority levels stored in MySQL.
 * Uses auto-resizing table and split pane for responsive window expansion.
 */
public class ManageKeywordsFrame extends JFrame {

    private final KeywordDAO keywordDAO;

    private JTable tableKeywords;
    private DefaultTableModel tableModel;

    private JTextField txtKeyword;
    private JSpinner spinnerWeight;
    private JComboBox<String> comboPriority;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    private int selectedKeywordId = -1;

    public ManageKeywordsFrame() {
        this.keywordDAO = new KeywordDAO();
        initUI();
        loadKeywords();
    }

    private void initUI() {
        setTitle("Manage Priority Keywords & Weight Rules");
        setSize(920, 560);
        setMinimumSize(new Dimension(750, 440));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        headerPanel.setBackground(new Color(52, 73, 94));
        JLabel lblHeader = new JLabel("Keyword Priority Rule Management");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);

        // Table Panel (Left / Center)
        String[] columnNames = {"ID", "Keyword / Phrase", "Weight Score", "Assigned Priority"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKeywords = new JTable(tableModel);
        tableKeywords.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableKeywords.setRowHeight(25);
        tableKeywords.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableKeywords.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Priority Badge renderer on Column 3
        tableKeywords.getColumnModel().getColumn(3).setCellRenderer(new MyTicketsFrame.PriorityCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableKeywords);
        scrollTable.setBorder(BorderFactory.createTitledBorder("Active Keywords in Database"));

        // Form & Actions Panel (Right)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Keyword Configuration"));
        formPanel.setMinimumSize(new Dimension(320, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Keyword Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Keyword:"), gbc);
        txtKeyword = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.65;
        formPanel.add(txtKeyword, gbc);

        // Weight Input (Spinner 1-10)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Weight Score:"), gbc);
        spinnerWeight = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.65;
        formPanel.add(spinnerWeight, gbc);

        // Priority Level ComboBox
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.35;
        formPanel.add(new JLabel("Priority Level:"), gbc);
        comboPriority = new JComboBox<>(new String[]{"HIGH", "MEDIUM", "LOW"});
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.65;
        formPanel.add(comboPriority, gbc);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnAdd = new JButton("Add Keyword");
        btnAdd.setBackground(new Color(39, 174, 96));
        btnAdd.setForeground(Color.WHITE);

        btnUpdate = new JButton("Update Selected");
        btnUpdate.setBackground(new Color(41, 128, 185));
        btnUpdate.setForeground(Color.WHITE);

        btnDelete = new JButton("Delete Selected");
        btnDelete.setBackground(new Color(192, 57, 43));
        btnDelete.setForeground(Color.WHITE);

        btnClear = new JButton("Clear Form");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Split Layout with proportional resizing
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTable, formPanel);
        splitPane.setDividerLocation(540);
        splitPane.setResizeWeight(0.65);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        // Event Listeners
        tableKeywords.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateFormFromTable();
            }
        });

        btnAdd.addActionListener(e -> addKeyword());
        btnUpdate.addActionListener(e -> updateKeyword());
        btnDelete.addActionListener(e -> deleteKeyword());
        btnClear.addActionListener(e -> clearForm());
    }

    private void loadKeywords() {
        tableModel.setRowCount(0);
        List<Keyword> keywords = keywordDAO.getAllKeywords();

        for (Keyword k : keywords) {
            Object[] row = {
                k.getKeywordId(),
                k.getKeyword(),
                k.getWeight(),
                k.getPriorityLevel()
            };
            tableModel.addRow(row);
        }
    }

    private void populateFormFromTable() {
        int selectedRow = tableKeywords.getSelectedRow();
        if (selectedRow >= 0) {
            selectedKeywordId = (int) tableModel.getValueAt(selectedRow, 0);
            txtKeyword.setText((String) tableModel.getValueAt(selectedRow, 1));
            spinnerWeight.setValue((int) tableModel.getValueAt(selectedRow, 2));
            comboPriority.setSelectedItem((String) tableModel.getValueAt(selectedRow, 3));
        }
    }

    private void addKeyword() {
        String keywordStr = txtKeyword.getText().trim();
        int weight = (int) spinnerWeight.getValue();
        String priority = (String) comboPriority.getSelectedItem();

        if (keywordStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid keyword phrase.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Keyword keyword = new Keyword(keywordStr, weight, priority);
        boolean added = keywordDAO.addKeyword(keyword);

        if (added) {
            JOptionPane.showMessageDialog(this, "Keyword '" + keywordStr + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadKeywords();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add keyword (it may already exist).", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKeyword() {
        if (selectedKeywordId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a keyword from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String keywordStr = txtKeyword.getText().trim();
        int weight = (int) spinnerWeight.getValue();
        String priority = (String) comboPriority.getSelectedItem();

        if (keywordStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid keyword phrase.", "Input Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Keyword keyword = new Keyword(selectedKeywordId, keywordStr, weight, priority, null);
        boolean updated = keywordDAO.updateKeyword(keyword);

        if (updated) {
            JOptionPane.showMessageDialog(this, "Keyword updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadKeywords();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update keyword in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteKeyword() {
        if (selectedKeywordId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a keyword from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this keyword rule?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = keywordDAO.deleteKeyword(selectedKeywordId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Keyword deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadKeywords();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete keyword.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedKeywordId = -1;
        txtKeyword.setText("");
        spinnerWeight.setValue(1);
        comboPriority.setSelectedIndex(0);
        tableKeywords.clearSelection();
    }
}
