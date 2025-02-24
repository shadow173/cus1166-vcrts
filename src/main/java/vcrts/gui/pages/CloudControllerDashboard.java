package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import vcrts.dao.JobDAO;
import vcrts.dao.UserDAO;
import vcrts.dao.AllocationDAO;
import vcrts.models.Job;
import vcrts.models.User;
import vcrts.models.Allocation;

public class CloudControllerDashboard extends JPanel {
    private JTable jobTable, userTable, allocationTable;
    private DefaultTableModel jobTableModel, userTableModel, allocationTableModel;
    private JButton addJobButton, editJobButton, deleteJobButton;
    private JButton addUserButton, editUserButton, deleteUserButton;
    private JButton allocateButton, removeAllocationButton;
    private JComboBox<String> userDropdown, jobDropdown;

    // DAO instances
    private JobDAO jobDAO = new JobDAO();
    private UserDAO userDAO = new UserDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();

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

        // Tabbed pane for Jobs, Users, and Allocations
        JTabbedPane tabbedPane = new JTabbedPane();

        // Jobs Tab
        JPanel jobPanel = new JPanel(new BorderLayout());
        String[] jobColumns = {"Job ID", "Job Name", "Job Owner", "Duration", "Deadline", "Status"};
        jobTableModel = new DefaultTableModel(jobColumns, 0);
        jobTable = new JTable(jobTableModel);
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

        add(tabbedPane, BorderLayout.CENTER);

        // Load data using DAO methods
        loadJobData();
        loadUserData();
        loadAllocationData();
        loadAllocationDropdowns();

        // Button actions
        addJobButton.addActionListener(e -> addNewJob());
        editJobButton.addActionListener(e -> editSelectedJob());
        deleteJobButton.addActionListener(e -> deleteSelectedJob());
        addUserButton.addActionListener(e -> addNewUser());
        editUserButton.addActionListener(e -> editSelectedUser());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        allocateButton.addActionListener(e -> allocateUserToJob());
        removeAllocationButton.addActionListener(e -> removeSelectedAllocation());

        // Menu actions
        editProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Profile clicked"));
        logout.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logging out..."));
        exit.addActionListener(e -> System.exit(0));
    }

    private void loadJobData() {
        jobTableModel.setRowCount(0);
        List<Job> jobs = jobDAO.getAllJobs();
        for (Job job : jobs) {
            jobTableModel.addRow(new Object[]{job.getJobId(), job.getJobName(), job.getJobOwnerId(), job.getDuration(), job.getDeadline(), job.getStatus()});
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

    private void addNewJob() {
        String jobId = JOptionPane.showInputDialog(this, "Enter Job ID:");
        String jobName = JOptionPane.showInputDialog(this, "Enter Job Name:");
      // add more error handling for jobowner int validation
        int jobOwner = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Job Owner (User ID):"));
        String duration = JOptionPane.showInputDialog(this, "Enter Estimated Duration:");
        String deadline = JOptionPane.showInputDialog(this, "Enter Deadline:");
        String status = JOptionPane.showInputDialog(this, "Enter Job Status:");

        if (jobId != null && jobName != null && status != null) {
            Job job = new Job(jobId, jobName, jobOwner, duration, deadline, status);
            if (jobDAO.addJob(job)) {
                loadJobData();
                loadAllocationDropdowns();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add job.");
            }
        }
    }

    private void editSelectedJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow != -1) {
            String jobId = (String) jobTableModel.getValueAt(selectedRow, 0);
            String newJobName = JOptionPane.showInputDialog(this, "Enter new Job Name:", jobTableModel.getValueAt(selectedRow, 1));
            // For brevity, we update only the table display. You can add jobDAO.updateJob(job) here.
            if (newJobName != null) {
                jobTableModel.setValueAt(newJobName, selectedRow, 1);
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
            User user = new User( fullName, email, "vehicle_owner", password);
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
            String userId = (String) userTableModel.getValueAt(selectedRow, 0);
            String newName = JOptionPane.showInputDialog(this, "Enter new Username:", userTableModel.getValueAt(selectedRow, 1));
            // For brevity, we update only the table display. Add userDAO.updateUser(user) as needed.
            if (newName != null) {
                userTableModel.setValueAt(newName, selectedRow, 1);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            String userId = (String) userTableModel.getValueAt(selectedRow, 0);
            if (userDAO.deleteUser(userId)) {
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
            int allocationId = (Integer) allocationTableModel.getValueAt(selectedRow, 0);
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
