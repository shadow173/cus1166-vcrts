package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import vcrts.db.DatabaseManager;

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

        // Table setup
        String[] columnNames = { "Client ID", "Job ID", "Status", "Estimated Completion Time", "Deadline" };
        tableModel = new DefaultTableModel(new Object[0][columnNames.length], columnNames) {
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

    public void updateTable() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);

        // Read jobs from client_jobs.txt
        try {
            List<String> lines = Files.readAllLines(Paths.get("client_jobs.txt"));
            int jobEntryLines = 6; // Each job entry has 6 lines
            for (int i = 0; i + jobEntryLines <= lines.size(); i += jobEntryLines) {
                String clientId = lines.get(i + 1).split(": ")[1];
                String jobId = lines.get(i + 2).split(": ")[1]; // Job Title
                String duration = lines.get(i + 4).split(": ")[1]; // Job Duration
                String deadline = lines.get(i + 5).split(": ")[1]; // Deadline

                // Default status to "Queued"
                tableModel.addRow(new Object[]{clientId, jobId, "Queued", duration, deadline});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading job data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = new DatabaseManager();
            JFrame frame = new JFrame("Client Dashboard Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.add(new ClientDashboard(dbManager));
            frame.setVisible(true);
        });
    }
}
