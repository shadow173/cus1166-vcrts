package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcrts.dao.JobDAO;
import vcrts.gui.MainFrame;
import vcrts.models.Job;
import vcrts.models.User;

public class ClientDashboard extends JPanel {
    private static final Logger logger = Logger.getLogger(ClientDashboard.class.getName());

    private User client; // Authenticated client (job owner)
    private JobDAO jobDAO = new JobDAO();

    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton, logoutButton, addJobButton;

    public ClientDashboard(User client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));

        // Top panel with title and Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(43, 43, 43));
        JLabel titleLabel = new JLabel("Client Dashboard - Job Tracking", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        logoutButton.addActionListener(e -> {
            // In production, clear session data etc.
            SwingUtilities.getWindowAncestor(this).dispose();
            new MainFrame().setVisible(true);
        });

        // Table setup
        String[] columnNames = {"Client ID", "Job ID", "Status", "Job Duration", "Deadline"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        jobTable = new JTable(tableModel);
        jobTable.setBackground(new Color(230, 230, 230));
        jobTable.setForeground(Color.BLACK);
        jobTable.setRowHeight(30);
        jobTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JTableHeader header = jobTable.getTableHeader();
        header.setBackground(new Color(200, 200, 200));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(jobTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter, Refresh & Add Job Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(43, 43, 43));
        String[] statuses = {"All", "Queued", "In Progress", "Completed"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setFont(new Font("Arial", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> updateTable());
        controlPanel.add(statusFilter);

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> updateTable());
        controlPanel.add(refreshButton);

        addJobButton = new JButton("Add Job");
        addJobButton.setBackground(Color.WHITE);
        addJobButton.setFont(new Font("Arial", Font.BOLD, 14));
        addJobButton.addActionListener(e -> openAddJobDialog());
        controlPanel.add(addJobButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Auto-refresh every 10 seconds
        new Timer(10000, e -> updateTable()).start();
        updateTable();
    }

    /**
     * Opens a dialog with input fields for adding a new job.
     */
    private void openAddJobDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField jobIdField = new JTextField(15);
        JTextField statusField = new JTextField(15);
        JTextField durationField = new JTextField(15);
        JTextField deadlineField = new JTextField(15);

        panel.add(new JLabel("Job ID:"));
        panel.add(jobIdField);
        panel.add(new JLabel("Status:"));
        panel.add(statusField);
        panel.add(new JLabel("Job Duration:"));
        panel.add(durationField);
        panel.add(new JLabel("Deadline (yyyy-MM-dd):"));
        panel.add(deadlineField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Job", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String jobId = jobIdField.getText().trim();
            String status = statusField.getText().trim();
            String duration = durationField.getText().trim();
            String deadline = deadlineField.getText().trim();

            if (jobId.isEmpty() || status.isEmpty() || duration.isEmpty() || deadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create a new Job object; using jobId as job_name as well.
            Job newJob = new Job(jobId, jobId, client.getUserId(), duration, deadline, status);
            boolean success = jobDAO.addJob(newJob);
            if (success) {
                JOptionPane.showMessageDialog(this, "Job added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add job!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void updateTable() {
        try {
            tableModel.setRowCount(0);
            String selectedStatus = (String) statusFilter.getSelectedItem();
            // If "All" is selected, getJobsByClient returns all jobs for the client.
            List<Job> jobs = jobDAO.getJobsByClient(client.getUserId(), selectedStatus);
            for (Job job : jobs) {
                tableModel.addRow(new Object[]{
                        job.getJobOwnerId(),
                        job.getJobId(),
                        job.getStatus(),
                        job.getDuration(),
                        job.getDeadline()
                });
            }
        } catch(Exception ex) {
            Logger.getLogger(ClientDashboard.class.getName()).log(Level.SEVERE, "Error updating job table: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error loading job data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
