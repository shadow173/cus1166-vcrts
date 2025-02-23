package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class OwnerDashboard extends JPanel {
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public OwnerDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));

        JLabel title = new JLabel("Owner Dashboard - Vehicle Tracking", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Table setup for vehicles
        String[] columnNames = { "Owner ID", "Model", "Make", "Year", "VIN", "Residency Time" };
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

        // Refresh Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(43, 43, 43));

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> updateTable());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Auto-refresh every 10 seconds
        Timer timer = new Timer(10000, e -> updateTable());
        timer.start();

        updateTable(); // Load initial data
    }

    public void updateTable() {
        tableModel.setRowCount(0); // Clear table before adding new data

        try {
            List<String> lines = Files.readAllLines(Paths.get("owner_vehicles.txt"));
            int entryLines = 7; // Ensure each vehicle entry has 7 lines

            for (int i = 0; i + entryLines <= lines.size(); i += entryLines) {
                if (i + 6 < lines.size()) { // Ensure we have 7 lines for each entry
                    try {
                        String ownerId = getValue(lines.get(i + 1));
                        String model = getValue(lines.get(i + 2));
                        String make = getValue(lines.get(i + 3));
                        String year = getValue(lines.get(i + 4));
                        String vin = getValue(lines.get(i + 5));
                        String residencyTime = getValue(lines.get(i + 6));

                        if (!ownerId.equals("N/A") && !vin.equals("N/A")) {
                            tableModel.addRow(new Object[]{ownerId, model, make, year, vin, residencyTime});
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Skipping invalid vehicle entry at line " + (i + 1));
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading vehicle data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getValue(String line) {
        if (line == null || !line.contains(": ")) return "N/A";
        String[] parts = line.split(": ", 2);
        return (parts.length > 1) ? parts[1].trim() : "N/A";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Owner Dashboard Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 600);
            frame.add(new OwnerDashboard());
            frame.setVisible(true);
        });
    }
}
