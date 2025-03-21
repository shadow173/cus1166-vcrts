package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcrts.dao.CloudControllerDAO;
import vcrts.dao.JobDAO;
import vcrts.models.Job;
import vcrts.models.User;

public class ClientDashboard extends JPanel {
    private static final Logger logger = Logger.getLogger(ClientDashboard.class.getName());

    private User client; // Authenticated client (job owner)
    private JobDAO jobDAO = new JobDAO();
    private CloudControllerDAO cloudControllerDAO = new CloudControllerDAO();

    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton, addJobButton, viewTimesButton;

    public ClientDashboard(User client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(new Color(43, 43, 43));

        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(43, 43, 43));
        JLabel titleLabel = new JLabel("Job Owner Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Job ID", "Status", "Duration", "Time to Complete", "Created At", "Estimated Completion"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        jobTable = new JTable(tableModel);
        jobTable.setBackground(new Color(230, 230, 230));
        jobTable.setForeground(Color.BLACK);
        jobTable.setRowHeight(30);
        jobTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jobTable.getColumnCount(); i++) {
            jobTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = jobTable.getTableHeader();
        header.setBackground(new Color(200, 200, 200));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 15));

        // Center table header text
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(jobTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter, Refresh & Add Job Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(43, 43, 43));

        // Center-align the components
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
//
//        JLabel statusLabel = new JLabel("Status Filter:");
//        statusLabel.setForeground(Color.WHITE);
//        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        controlPanel.add(statusLabel);

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

//        viewTimesButton = new JButton("View Completion Times");
//        viewTimesButton.setBackground(Color.WHITE);
//        viewTimesButton.setFont(new Font("Arial", Font.BOLD, 14));
//        viewTimesButton.addActionListener(e -> updateTableWithCompletionTimes());
//        controlPanel.add(viewTimesButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Auto-refresh every 10 seconds
        new Timer(10000, e -> updateTable()).start();
        updateTable();
    }

    /**
     * Opens a dialog with input fields for adding a new job.
     */
    private void openAddJobDialog() {
        // Create a panel with more sophisticated layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Job ID field
        JPanel jobIdPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jobIdPanel.add(new JLabel("Job ID:"));
        JTextField jobIdField = new JTextField(15);
        jobIdField.setHorizontalAlignment(SwingConstants.CENTER);
        jobIdPanel.add(jobIdField);
        panel.add(jobIdPanel);

        // Duration panel with spinner for hours, minutes, and seconds
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        durationPanel.add(new JLabel("Duration:"));

        // Hours spinner (0-23)
        SpinnerNumberModel hoursModel = new SpinnerNumberModel(0, 0, 23, 1);
        JSpinner hoursSpinner = new JSpinner(hoursModel);
        JSpinner.NumberEditor hoursEditor = new JSpinner.NumberEditor(hoursSpinner, "00");
        hoursSpinner.setEditor(hoursEditor);
        hoursSpinner.setPreferredSize(new Dimension(60, 25));

        // Minutes spinner (0-59)
        SpinnerNumberModel minutesModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner minutesSpinner = new JSpinner(minutesModel);
        JSpinner.NumberEditor minutesEditor = new JSpinner.NumberEditor(minutesSpinner, "00");
        minutesSpinner.setEditor(minutesEditor);
        minutesSpinner.setPreferredSize(new Dimension(60, 25));

        // Seconds spinner (0-59)
        SpinnerNumberModel secondsModel = new SpinnerNumberModel(0, 0, 59, 1);
        JSpinner secondsSpinner = new JSpinner(secondsModel);
        JSpinner.NumberEditor secondsEditor = new JSpinner.NumberEditor(secondsSpinner, "00");
        secondsSpinner.setEditor(secondsEditor);
        secondsSpinner.setPreferredSize(new Dimension(60, 25));

        durationPanel.add(hoursSpinner);
        durationPanel.add(new JLabel("h"));
        durationPanel.add(minutesSpinner);
        durationPanel.add(new JLabel("m"));
        durationPanel.add(secondsSpinner);
        durationPanel.add(new JLabel("s"));
        panel.add(durationPanel);

        // Deadline date picker
        JPanel deadlinePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deadlinePanel.add(new JLabel("Deadline:"));

        // Date picker using JSpinner with date editor
        Calendar calendar = Calendar.getInstance();
        Date initialDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 10); // Allow dates up to 10 years in the future
        Date lastDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -20); // Allow dates up to 10 years in the past
        Date firstDate = calendar.getTime();
        SpinnerDateModel dateModel = new SpinnerDateModel(initialDate, firstDate, lastDate, Calendar.DAY_OF_MONTH);

        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(150, 25));

        deadlinePanel.add(dateSpinner);
        panel.add(deadlinePanel);

        // Note about status
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel noteLabel = new JLabel("Note: Job status will be automatically set based on FIFO scheduling.");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        notePanel.add(noteLabel);
        panel.add(notePanel);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Job", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String jobId = jobIdField.getText().trim();

            // Format the duration from spinner values
            int hours = (Integer) hoursSpinner.getValue();
            int minutes = (Integer) minutesSpinner.getValue();
            int seconds = (Integer) secondsSpinner.getValue();
            String duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            // Format the deadline from date spinner
            Date selectedDate = (Date) dateSpinner.getValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String deadline = dateFormat.format(selectedDate);

            if (jobId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Job ID is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hours == 0 && minutes == 0 && seconds == 0) {
                JOptionPane.showMessageDialog(this, "Duration must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Jobs are automatically set to "Queued" status
            Job newJob = new Job(jobId, jobId, client.getUserId(), duration, deadline, CloudControllerDAO.STATE_QUEUED);
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

            // Keep track of cumulative time for FIFO
            long cumulativeMinutes = 0;

            for (Job job : jobs) {
                // Calculate job duration in minutes
                long durationMinutes = 0;
                try {
                    String[] timeParts = job.getDuration().split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);

                    durationMinutes = hours * 60 + minutes + (seconds > 0 ? 1 : 0); // Round up seconds
                } catch (Exception e) {
                    durationMinutes = 60; // Default to 1 hour if parsing fails
                }

                // Add to cumulative time if not completed
                if (!job.getStatus().equals(CloudControllerDAO.STATE_COMPLETED)) {
                    cumulativeMinutes += durationMinutes;
                }

                // Format the cumulative time as hours and minutes
                long totalHours = cumulativeMinutes / 60;
                long totalMinutes = cumulativeMinutes % 60;
                String timeToComplete = totalHours > 0 ?
                        String.format("%dh %dm", totalHours, totalMinutes) :
                        String.format("%dm", totalMinutes);

                // For completed jobs, don't show time to complete
                if (job.getStatus().equals(CloudControllerDAO.STATE_COMPLETED)) {
                    timeToComplete = "Completed";
                }

                tableModel.addRow(new Object[]{
                        job.getJobId(),
                        job.getStatus(),
                        job.getDuration(),
                        timeToComplete,
                        job.getCreatedTimestamp(),
                        "Not calculated"  // Placeholder for completion time
                });
            }
        } catch(Exception ex) {
            Logger.getLogger(ClientDashboard.class.getName()).log(Level.SEVERE, "Error updating job table: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error loading job data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the table with completion time information from the cloud controller
     */
    private void updateTableWithCompletionTimes() {
        try {
            // Update the table first
            updateTable();

            // Get completion times from cloud controller
            Map<String, String> completionTimes = cloudControllerDAO.loadSchedule();

            // If no schedule exists, inform the user
            if (completionTimes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No completion times have been calculated yet. Please contact the Cloud Controller.",
                        "Schedule Information",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Update the completion time column in the table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String jobId = (String) tableModel.getValueAt(i, 0);
                String completionTime = completionTimes.get(jobId);
                if (completionTime != null) {
                    tableModel.setValueAt(completionTime, i, 5);
                }
            }

            // Create a simple summary view just for this client's jobs
            StringBuilder summary = new StringBuilder();
            summary.append("Your Job Completion Times (FIFO Scheduling)\n");
            summary.append("===========================================\n\n");

            // Track running total for FIFO calculation
            long runningTotalMinutes = 0;

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String jobId = (String) tableModel.getValueAt(i, 0);
                String status = (String) tableModel.getValueAt(i, 1);
                String duration = (String) tableModel.getValueAt(i, 2);
                String completionTime = (String) tableModel.getValueAt(i, 5);

                // Calculate job duration in minutes
                long durationMinutes = 0;
                try {
                    String[] timeParts = duration.split(":");
                    int hours = Integer.parseInt(timeParts[0]);
                    int minutes = Integer.parseInt(timeParts[1]);
                    int seconds = Integer.parseInt(timeParts[2]);

                    durationMinutes = hours * 60 + minutes + (seconds > 0 ? 1 : 0); // Round up seconds
                } catch (Exception e) {
                    durationMinutes = 60; // Default to 1 hour if parsing fails
                }

                // Add to running total if not completed
                if (!status.equals(CloudControllerDAO.STATE_COMPLETED)) {
                    runningTotalMinutes += durationMinutes;
                }

                // Format duration
                long durationHours = durationMinutes / 60;
                long durationMin = durationMinutes % 60;
                String formattedDuration = durationHours > 0 ?
                        String.format("%dh %dm", durationHours, durationMin) :
                        String.format("%dm", durationMin);

                // Format time to complete
                long totalHours = runningTotalMinutes / 60;
                long totalMinutes = runningTotalMinutes % 60;
                String timeToComplete = totalHours > 0 ?
                        String.format("%dh %dm", totalHours, totalMinutes) :
                        String.format("%dm", totalMinutes);

                if (status.equals(CloudControllerDAO.STATE_COMPLETED)) {
                    timeToComplete = "Completed";
                }

                summary.append(String.format("Job ID: %s\n", jobId));
                summary.append(String.format("Status: %s\n", status));
                summary.append(String.format("Duration: %s\n", formattedDuration));
                summary.append(String.format("Time to Complete: %s\n", timeToComplete));
                summary.append(String.format("Estimated Completion: %s\n", completionTime));
                summary.append("-------------------------------------------\n");
            }

            // Display the summary
            JTextArea textArea = new JTextArea(summary.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Your Job Schedule",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch(Exception ex) {
            Logger.getLogger(ClientDashboard.class.getName()).log(Level.SEVERE, "Error updating completion times: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error loading completion time data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
