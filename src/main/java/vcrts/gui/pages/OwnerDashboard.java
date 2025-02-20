package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import vcrts.db.DatabaseManager;

public class OwnerDashboard extends JPanel {
    private DatabaseManager dbManager;
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton;

    public OwnerDashboard(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));

        JLabel title = new JLabel("Owner Dashboard - Vehicle Tracking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Table for vehicle tracking
        String[] columnNames = { "Vehicle ID", "Owner ID", "Model", "Make", "Year", "VIN", "Jobs Completed", "Arrival Time", "Departure Time", "Residency Time", "Status" };

        tableModel = new DefaultTableModel(new Object[0][columnNames.length], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        vehicleTable = new JTable(tableModel);
        vehicleTable.setBackground(new Color(230, 230, 230));
        vehicleTable.setForeground(Color.BLACK);
        vehicleTable.setRowHeight(30);
        vehicleTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader tableHeader = vehicleTable.getTableHeader();
        tableHeader.setBackground(new Color(200, 200, 200));
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 15));

        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter & Refresh Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(43, 43, 43));

        String[] statuses = { "All", "Idle", "In Use", "Offline" };
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

        updateTable(); // Load initial data
    }

    private void updateTable() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        Object[][] vehicleData = dbManager.getFilteredVehicles(selectedStatus);
        tableModel.setRowCount(0);

        for (Object[] row : vehicleData) {
            tableModel.addRow(row);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = new DatabaseManager();
            JFrame frame = new JFrame("Owner Dashboard Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 600);
            frame.add(new OwnerDashboard(dbManager));
            frame.setVisible(true);
        });
    }
}
