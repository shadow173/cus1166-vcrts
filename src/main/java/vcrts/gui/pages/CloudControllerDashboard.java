package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CloudControllerDashboard extends JFrame {
	private JTable jobTable, userTable, allocationTable;
    private DefaultTableModel jobTableModel, userTableModel, allocationTableModel;
    private JButton addJobButton, editJobButton, deleteJobButton, addUserButton, editUserButton, deleteUserButton, allocateButton,removeAllocationButton ;
    private JComboBox<String> userDropdown, jobDropdown;

    public CloudControllerDashboard() {
        setTitle("Cloud Controller");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Adding Menu Bar with Right Alignment
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
        setJMenuBar(menuBar);

        // Adding Title Label
        JLabel titleLabel = new JLabel("Cloud Controller", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Creating Tabs for Jobs and Users
        JTabbedPane tabbedPane = new JTabbedPane();

        // Job Table Panel
        JPanel jobPanel = new JPanel(new BorderLayout());
        String[] jobColumns = {"Job ID", "Job Name", "Job Owner","Duration","Deadline", "Status"};
        jobTableModel = new DefaultTableModel(jobColumns, 0);
        jobTable = new JTable(jobTableModel);
        jobPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        

        // Job Action Panel
        JPanel jobActionPanel = new JPanel();
        addJobButton = new JButton("Add Job");
        editJobButton = new JButton("Edit Job");
        deleteJobButton = new JButton("Delete Job");
        jobActionPanel.add(addJobButton);
        jobActionPanel.add(editJobButton);
        jobActionPanel.add(deleteJobButton);
        jobPanel.add(jobActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Jobs", jobPanel);


        // User Table Panel
        JPanel userPanel = new JPanel(new BorderLayout());
        String[] userColumns = {"User ID", "Username", "Email", "Vehicle Model", "Vehicle Make", "Vehicle Year", "Vehicle VIN", "Arrival Time", "Departure Time"};
        userTableModel = new DefaultTableModel(userColumns, 0);
        userTable = new JTable(userTableModel);
        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        // User Action Panel
        JPanel userActionPanel = new JPanel();
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");
        userActionPanel.add(addUserButton);
        userActionPanel.add(editUserButton);
        userActionPanel.add(deleteUserButton);
        userPanel.add(userActionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Users", userPanel);

        // Allocation Panel
        JPanel allocationPanel = new JPanel(new BorderLayout());
        String[] allocationColumns = {"User", "Job"};
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
        loadSampleData();


        // Button Actions
        addJobButton.addActionListener(e -> addNewJob());
        editJobButton.addActionListener(e -> editSelectedRow(jobTable, jobTableModel));
        deleteJobButton.addActionListener(e -> deleteSelectedRow(jobTable, jobTableModel));
        addUserButton.addActionListener(e -> addNewUser());
        editUserButton.addActionListener(e -> editSelectedRow(userTable, userTableModel));
        deleteUserButton.addActionListener(e -> deleteSelectedRow(userTable, userTableModel));
        allocateButton.addActionListener(e -> allocateUserToJob());
        removeAllocationButton.addActionListener(e -> removeUserFromJob());
        
        
        // Menu Actions
        editProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Profile clicked"));
        logout.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logging out..."));
        exit.addActionListener(e -> System.exit(0));
    }
    
    private void allocateUserToJob() {
        String selectedUser = (String) userDropdown.getSelectedItem();
        String selectedJob = (String) jobDropdown.getSelectedItem();
        if (selectedUser != null && selectedJob != null) {
            allocationTableModel.addRow(new Object[]{selectedUser, selectedJob});
        } else {
            JOptionPane.showMessageDialog(this, "Please select both a user and a job.");
        }
    }
    
    private void removeUserFromJob() {
        int selectedRow = allocationTable.getSelectedRow();
        if (selectedRow != -1) {
            allocationTableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to remove.");
        }
    }

    private void loadSampleData() {
        jobTableModel.addRow(new Object[]{"101", "Data Backup", "Alice", "2 hours", "2025-03-01", "Running"});
        jobTableModel.addRow(new Object[]{"102", "Security Scan", "Bob", "3 hours", "2025-03-05", "Pending"});
        userTableModel.addRow(new Object[]{
        	    "001", 
        	    "jane_doe", 
        	    "jane.doe@example.com", 
        	    "Model 3", 
        	    "Tesla", 
        	    "2022", 
        	    "5YJ3E1EA8NF123456", 
        	    "10:30 AM", 
        	    "2:45 PM"
        	});

        	userTableModel.addRow(new Object[]{
        	    "002", 
        	    "michael_smith", 
        	    "michael.smith@example.com", 
        	    "Mustang", 
        	    "Ford", 
        	    "2019", 
        	    "1FA6P8TH4K5154321", 
        	    "9:15 AM", 
        	    "1:30 PM"
        	});
            allocationTableModel.addRow(new Object[]{"jane_doe", "Data Backup"});
            allocationTableModel.addRow(new Object[]{"michael_smith", "Security Scan"});
        
    }

    private void addNewJob() {
        String jobId = JOptionPane.showInputDialog(this, "Enter Job ID:");
        String jobName = JOptionPane.showInputDialog(this, "Enter Job Name:");
        String jobOwner = JOptionPane.showInputDialog(this, "Enter Job Owner:");
        String duration = JOptionPane.showInputDialog(this, "Enter Estimated Duration:");
        String deadline = JOptionPane.showInputDialog(this, "Enter Deadline:");
        String jobStatus = JOptionPane.showInputDialog(this, "Enter Job Status:");


        if (jobId != null && jobName != null && jobStatus != null) {
            jobTableModel.addRow(new Object[]{jobId, jobName, jobStatus});
        }
    }

    private void addNewUser() {
        String userId = JOptionPane.showInputDialog(this, "Enter User ID:");
        String username = JOptionPane.showInputDialog(this, "Enter Username:");
        String email = JOptionPane.showInputDialog(this, "Enter Email:");
        String vehicleModel = JOptionPane.showInputDialog(this, "Enter Vehicle Model:");
        String vehicleMake = JOptionPane.showInputDialog(this, "Enter Vehicle Make:");
        String vehicleYear = JOptionPane.showInputDialog(this, "Enter Vehicle Year:");
        String vehicleVIN = JOptionPane.showInputDialog(this, "Enter Vehicle VIN:");
        String arrivalTime = JOptionPane.showInputDialog(this, "Enter Arrival Time:");
        String departureTime = JOptionPane.showInputDialog(this, "Enter Departure Time:");
        

        if (userId != null && username != null) {
            userTableModel.addRow(new Object[]{userId, username, email, vehicleModel, vehicleMake, vehicleYear, vehicleVIN, arrivalTime, departureTime});
        }
    }

    private void editSelectedRow(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                String newValue = JOptionPane.showInputDialog(this, "Edit " + table.getColumnName(i),
                        model.getValueAt(selectedRow, i));
                if (newValue != null) {
                    model.setValueAt(newValue, selectedRow, i);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
        }
    }

    private void deleteSelectedRow(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CloudControllerDashboard().setVisible(true));
    }
}
