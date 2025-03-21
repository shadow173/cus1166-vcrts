package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import vcrts.dao.JobDAO;
import vcrts.dao.UserDAO;
import vcrts.dao.AllocationDAO;
import vcrts.dao.CloudControllerDAO;
import vcrts.models.Job;
import vcrts.models.User;
import vcrts.models.Allocation;

public class CloudControllerDashboard extends JPanel {
    private JTable jobTable, userTable, allocationTable, scheduleTable;
    private DefaultTableModel jobTableModel, userTableModel, allocationTableModel, scheduleTableModel;
    private JButton addJobButton, editJobButton, deleteJobButton;
    private JButton addUserButton, editUserButton, deleteUserButton;
    private JButton allocateButton, removeAllocationButton;
    private JButton calculateTimesButton, assignVehiclesButton, advanceQueueButton;
    private JComboBox<String> userDropdown, jobDropdown;
    private JLabel queueStatusLabel;

    // DAO instances
    private JobDAO jobDAO = new JobDAO();
    private UserDAO userDAO = new UserDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();
    private CloudControllerDAO cloudControllerDAO = new CloudControllerDAO();

    public CloudControllerDashboard() {
        setLayout(new BorderLayout());

        // Top panel with title and menu bar
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Cloud Controller", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JMenu menu = new JMenu("Menu");
        JMenuItem editProfile = new JMenuItem("Edit Profile");
        JMenuItem logout = new JMenuItem("Logout");
        JMenuItem exit = new JMenuItem("Exit to Desktop");
        menu.add(editProfile);
        menu.add(logout);
        menu.add(exit);
        menuBar.add(menu);
        topPanel.add(menuBar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Tabbed pane for Jobs, Users, Allocations, and Schedule
        JTabbedPane tabbedPane = new JTabbedPane();

        // Jobs Tab
        JPanel jobPanel = new JPanel(new BorderLayout());
        String[] jobColumns = {"Job ID", "Job Name", "Job Owner", "Duration", "Deadline", "Status", "Created At"};
        jobTableModel = new DefaultTableModel(jobColumns, 0);
        jobTable = new JTable(jobTableModel);

        // Center-align table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < jobTable.getColumnCount(); i++) {
            jobTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        jobPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);

        JPanel jobActionPanel = new JPanel();
        addJobButton = new JButton("Add Job");
        editJobButton = new JButton("Edit Job");
        deleteJobButton = new JButton("Delete Job");
        jobActionPanel.add(addJobButton);
        jobActionPanel.add(editJobButton);
        jobActionPanel.add(deleteJobButton);
        jobPanel.add(jobActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Jobs", jobPanel);

        // Users Tab
        JPanel userPanel = new JPanel(new BorderLayout());
        String[] userColumns = {"User ID", "Username", "Email", "Role"};
        userTableModel = new DefaultTableModel(userColumns, 0);
        userTable = new JTable(userTableModel);

        // Center-align table cells
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel userActionPanel = new JPanel();
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");
        userActionPanel.add(addUserButton);
        userActionPanel.add(editUserButton);
        userActionPanel.add(deleteUserButton);
        userPanel.add(userActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Users", userPanel);

        // Allocations Tab
        JPanel allocationPanel = new JPanel(new BorderLayout());
        String[] allocationColumns = {"Allocation ID", "User", "Job"};
        allocationTableModel = new DefaultTableModel(allocationColumns, 0);
        allocationTable = new JTable(allocationTableModel);

        // Center-align table cells
        for (int i = 0; i < allocationTable.getColumnCount(); i++) {
            allocationTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        allocationPanel.add(new JScrollPane(allocationTable), BorderLayout.CENTER);

        JPanel allocationControls = new JPanel();
        userDropdown = new JComboBox<>();
        jobDropdown = new JComboBox<>();
        allocateButton = new JButton("Allocate User to Job");
        removeAllocationButton = new JButton("Remove Allocation");
        allocationControls.add(new JLabel("Select User:"));
        allocationControls.add(userDropdown);
        allocationControls.add(new JLabel("Select Job:"));
        allocationControls.add(jobDropdown);
        allocationControls.add(allocateButton);
        allocationControls.add(removeAllocationButton);
        allocationPanel.add(allocationControls, BorderLayout.SOUTH);
        tabbedPane.addTab("Allocations", allocationPanel);

        // Schedule Tab (New)
        JPanel schedulePanel = new JPanel(new BorderLayout());

        // Add a status panel at the top of the schedule tab
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        queueStatusLabel = new JLabel("Queue Status: Loading...");
        queueStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(queueStatusLabel);
        schedulePanel.add(statusPanel, BorderLayout.NORTH);

        String[] scheduleColumns = {"Job ID", "Job Name", "Duration", "Time to Complete", "Status", "Completion Time"};
        scheduleTableModel = new DefaultTableModel(scheduleColumns, 0);
        scheduleTable = new JTable(scheduleTableModel);

        // Center-align table cells
        for (int i = 0; i < scheduleTable.getColumnCount(); i++) {
            scheduleTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel scheduleControlPanel = new JPanel();
        calculateTimesButton = new JButton("Calculate Completion Times");
        assignVehiclesButton = new JButton("Assign Vehicles to Jobs");
        advanceQueueButton = new JButton("Advance Job Queue");
        scheduleControlPanel.add(calculateTimesButton);
        scheduleControlPanel.add(assignVehiclesButton);
        scheduleControlPanel.add(advanceQueueButton);
        schedulePanel.add(scheduleControlPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Schedule", schedulePanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Load data using DAO methods
        loadJobData();
        loadUserData();
        loadAllocationData();
        loadAllocationDropdowns();
        loadScheduleData();
        updateQueueStatus();

        // Button actions
        addJobButton.addActionListener(e -> addNewJob());
        editJobButton.addActionListener(e -> editSelectedJob());
        deleteJobButton.addActionListener(e -> deleteSelectedJob());
        addUserButton.addActionListener(e -> addNewUser());
        editUserButton.addActionListener(e -> editSelectedUser());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        allocateButton.addActionListener(e -> allocateUserToJob());
        removeAllocationButton.addActionListener(e -> removeSelectedAllocation());

        // Schedule button actions
        calculateTimesButton.addActionListener(e -> calculateCompletionTimes());
        assignVehiclesButton.addActionListener(e -> assignVehiclesToJobs());
        advanceQueueButton.addActionListener(e -> advanceJobQueue());

        // Menu actions
        editProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Profile clicked"));
        logout.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logging out..."));
        exit.addActionListener(e -> System.exit(0));

        // Add tab change listener to refresh data when switching to schedule tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) { // Schedule tab
                loadScheduleData();
                updateQueueStatus();
            }
        });
    }

    private void loadJobData() {
        jobTableModel.setRowCount(0);
        List<Job> jobs = jobDAO.getAllJobs();
        for (Job job : jobs) {
            jobTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getJobOwnerId(),
                    job.getDuration(),
                    job.getDeadline(),
                    job.getStatus(),
                    job.getCreatedTimestamp()
            });
        }
    }

    private void loadUserData() {
        userTableModel.setRowCount(0);
        List<User> users = userDAO.getAllVehicleOwners();
        for (User user : users) {
            userTableModel.addRow(new Object[]{user.getUserId(), user.getFullName(), user.getEmail(), user.getRole()});
        }
    }

    private void loadAllocationData() {
        allocationTableModel.setRowCount(0);
        List<Allocation> allocations = allocationDAO.getAllAllocations();
        for (Allocation allocation : allocations) {
            allocationTableModel.addRow(new Object[]{allocation.getAllocationId(), allocation.getUserId(), allocation.getJobId()});
        }
    }

    private void loadScheduleData() {
        scheduleTableModel.setRowCount(0);
        Map<String, String> completionTimes = cloudControllerDAO.loadSchedule();
        List<Job> jobs = jobDAO.getAllJobs();

        // Sort jobs by creation timestamp for FIFO display
        jobs.sort((a, b) -> a.getCreatedTimestamp().compareTo(b.getCreatedTimestamp()));

        // Running total for completion time calculation
        long runningTotalMinutes = 0;

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

            // Add current job duration to running total (for FIFO calculation)
            if (!job.getStatus().equals(CloudControllerDAO.STATE_COMPLETED)) {
                runningTotalMinutes += durationMinutes;
            }

            // Format the running total as hours and minutes
            long totalHours = runningTotalMinutes / 60;
            long totalMinutes = runningTotalMinutes % 60;
            String timeToComplete = totalHours > 0 ?
                    String.format("%dh %dm", totalHours, totalMinutes) :
                    String.format("%dm", totalMinutes);

            // Get completion time
            String completionTime = completionTimes.getOrDefault(job.getJobId(), "Not calculated");

            // Add row to table
            scheduleTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getDuration(),
                    job.getStatus().equals(CloudControllerDAO.STATE_COMPLETED) ? "Completed" : timeToComplete,
                    job.getStatus(),
                    completionTime
            });
        }
    }

    private void updateQueueStatus() {
        Map<String, Integer> summary = cloudControllerDAO.getJobQueueSummary();
        queueStatusLabel.setText(String.format("Queue Status: %d Queued | %d In Progress | %d Completed",
                summary.getOrDefault(CloudControllerDAO.STATE_QUEUED, 0),
                summary.getOrDefault(CloudControllerDAO.STATE_PROGRESS, 0),
                summary.getOrDefault(CloudControllerDAO.STATE_COMPLETED, 0)));
    }

    private void loadAllocationDropdowns() {
        userDropdown.removeAllItems();
        jobDropdown.removeAllItems();

        List<User> users = userDAO.getAllVehicleOwners();
        for (User user : users) {
            userDropdown.addItem(user.getUserId() + " - " + user.getFullName());
        }

        List<Job> jobs = jobDAO.getAllJobs();
        for (Job job : jobs) {
            jobDropdown.addItem(job.getJobId() + " - " + job.getJobName());
        }
    }

    /**
     * Calculates completion times for all jobs using FIFO scheduling
     */
    private void calculateCompletionTimes() {
        Map<String, String> completionTimes = cloudControllerDAO.calculateCompletionTimes();
        if (completionTimes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No jobs found to schedule.", "Schedule", JOptionPane.INFORMATION_MESSAGE);
        } else {
            loadScheduleData();
            loadJobData();
            updateQueueStatus();

            // Show the calculation results
            String output = cloudControllerDAO.generateSchedulingOutput();
            JTextArea textArea = new JTextArea(output);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Job Scheduling Results (FIFO)",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Assigns vehicles to jobs based on availability and FIFO order
     */
    private void assignVehiclesToJobs() {
        int assignmentCount = cloudControllerDAO.assignVehiclesToJobs();
        if (assignmentCount > 0) {
            loadJobData();
            loadScheduleData();
            updateQueueStatus();
            JOptionPane.showMessageDialog(this,
                    "Assigned vehicles to " + assignmentCount + " jobs.",
                    "Vehicle Assignment",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No new assignments were made. Check vehicle availability and job queue.",
                    "Vehicle Assignment",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Advances the job queue by completing the current in-progress job
     * and moving the next job to in-progress status
     */
    private void advanceJobQueue() {
        String nextJobId = cloudControllerDAO.advanceJobQueue();
        if (nextJobId != null) {
            loadJobData();
            loadScheduleData();
            updateQueueStatus();
            JOptionPane.showMessageDialog(this,
                    "Current job completed. Job " + nextJobId + " is now in progress.",
                    "Queue Advanced",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No jobs to advance. The queue may be empty or all jobs may be completed.",
                    "Queue Status",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addNewJob() {
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

        // Job Name field
        JPanel jobNamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jobNamePanel.add(new JLabel("Job Name:"));
        JTextField jobNameField = new JTextField(15);
        jobNameField.setHorizontalAlignment(SwingConstants.CENTER);
        jobNamePanel.add(jobNameField);
        panel.add(jobNamePanel);

        // Job Owner field
        JPanel jobOwnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jobOwnerPanel.add(new JLabel("Job Owner ID:"));
        JTextField jobOwnerField = new JTextField(15);
        jobOwnerField.setHorizontalAlignment(SwingConstants.CENTER);
        jobNamePanel.add(jobOwnerField);
        panel.add(jobOwnerPanel);

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

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Job", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String jobId = jobIdField.getText().trim();
                String jobName = jobNameField.getText().trim();
                int jobOwner = Integer.parseInt(jobOwnerField.getText().trim());

                // Format the duration from spinner values
                int hours = (Integer) hoursSpinner.getValue();
                int minutes = (Integer) minutesSpinner.getValue();
                int seconds = (Integer) secondsSpinner.getValue();
                String duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                // Use current date as deadline for simplicity
                String deadline = java.time.LocalDate.now().toString();

                if (jobId != null && jobName != null) {
                    // New jobs are automatically queued
                    Job job = new Job(jobId, jobName, jobOwner, duration, deadline, CloudControllerDAO.STATE_QUEUED);
                    if (jobDAO.addJob(job)) {
                        loadJobData();
                        loadAllocationDropdowns();

                        // Recalculate the schedule
                        cloudControllerDAO.calculateCompletionTimes();
                        loadScheduleData();
                        updateQueueStatus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add job.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Job Owner ID. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow != -1) {
            String jobId = (String) jobTableModel.getValueAt(selectedRow, 0);
            String newJobName = JOptionPane.showInputDialog(this, "Enter new Job Name:", jobTableModel.getValueAt(selectedRow, 1));

            if (newJobName != null) {
                // Find the job in the data
                List<Job> jobs = jobDAO.getAllJobs();
                for (Job job : jobs) {
                    if (job.getJobId().equals(jobId)) {
                        job.setJobName(newJobName);
                        jobDAO.updateJob(job);
                        break;
                    }
                }

                loadJobData();
                loadScheduleData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a job to edit.");
        }
    }

    private void deleteSelectedJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow != -1) {
            String jobId = (String) jobTableModel.getValueAt(selectedRow, 0);
            if (jobDAO.deleteJob(jobId)) {
                loadJobData();
                loadAllocationDropdowns();
                loadScheduleData();
                updateQueueStatus();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete job.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a job to delete.");
        }
    }

    private void addNewUser() {
        String fullName = JOptionPane.showInputDialog(this, "Enter Username:");
        String email = JOptionPane.showInputDialog(this, "Enter Email:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");
        if (fullName != null && email != null && password != null) {
            User user = new User(fullName, email, "vehicle_owner,job_owner", password);
            if (userDAO.addUser(user)) {
                loadUserData();
                loadAllocationDropdowns();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user.");
            }
        }
    }

    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) userTableModel.getValueAt(selectedRow, 0);
            User user = userDAO.getUserById(userId);

            if (user != null) {
                String newName = JOptionPane.showInputDialog(this, "Enter new Username:", user.getFullName());
                if (newName != null) {
                    user.setFullName(newName);
                    if (userDAO.updateUser(user)) {
                        loadUserData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update user.");
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) userTableModel.getValueAt(selectedRow, 0);
            if (userDAO.deleteUser(String.valueOf(userId))) {
                loadUserData();
                loadAllocationDropdowns();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void allocateUserToJob() {
        String userSelection = (String) userDropdown.getSelectedItem();
        String jobSelection = (String) jobDropdown.getSelectedItem();
        if (userSelection != null && jobSelection != null) {
            String userId = userSelection.split(" - ")[0];
            String jobId = jobSelection.split(" - ")[0];
            Allocation allocation = new Allocation(userId, jobId);
            if (allocationDAO.addAllocation(allocation)) {
                loadAllocationData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to allocate user to job.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select both a user and a job.");
        }
    }

    private void removeSelectedAllocation() {
        int selectedRow = allocationTable.getSelectedRow();
        if (selectedRow != -1) {
            int allocationId = (int) allocationTableModel.getValueAt(selectedRow, 0);
            if (allocationDAO.deleteAllocation(allocationId)) {
                loadAllocationData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove allocation.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an allocation to remove.");
        }
    }
}
