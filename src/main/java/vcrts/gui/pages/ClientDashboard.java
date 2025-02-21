package vcrts.gui.pages;

import vcrts.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientDashboard extends JPanel {
    private DatabaseManager dbManager;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton;

    public ClientDashboard(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));

        JLabel title = new JLabel("Client Dashboard - Job Tracking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Tables for job tracking
        String[] columnNames = { "Client ID", "Job ID", "Status", "Estimated Completion Time", "Deadline" };
        tableModel = new DefaultTableModel(new Object[0][5], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        jobTable = new JTable(tableModel);
        jobTable.setBackground(new Color(230, 230, 230));
        jobTable.setForeground(Color.BLACK);
        jobTable.setRowHeight(30);
        jobTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader tableHeader = jobTable.getTableHeader();
        tableHeader.setBackground(new Color(200, 200, 200));
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(jobTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter & Refresh Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(43, 43, 43));

        String[] statuses = { "All", "Queued", "In Progress", "Completed" };
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setForeground(Color.BLACK);
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> updateTable());
        controlPanel.add(statusFilter);

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> updateTable());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Auto-refresh every 10 seconds
        Timer timer = new Timer(10000, e -> updateTable());
        timer.start();

        updateTable();
    }

    private void updateTable() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        Object[][] jobData = dbManager.getFilteredJobs(selectedStatus);
        tableModel.setRowCount(0);

        // Format deadline date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (Object[] row : jobData) {
            if (row.length < 3) continue; // Skip if row data is incomplete

            Object[] updatedRow = new Object[5];  // Ensure exactly 5 columns
            updatedRow[0] = "Client001";  // Placeholder for Client ID (Replace with real data)
            updatedRow[1] = row[0];  // Job ID
            updatedRow[2] = row[1];  // Status
            updatedRow[3] = row[2];  // Estimated Completion Time
            updatedRow[4] = dateFormat.format(new Date());  // Deadline (Replace with real deadline)

            tableModel.addRow(updatedRow);
        }
    }
}