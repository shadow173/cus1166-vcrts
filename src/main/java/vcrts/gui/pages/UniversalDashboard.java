package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import vcrts.gui.MainFrame;
import vcrts.models.User;

public class UniversalDashboard extends JPanel {
    private static final Logger logger = Logger.getLogger(UniversalDashboard.class.getName());

    private MainFrame mainFrame;
    private User user;
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JToggleButton vehicleOwnerButton, jobOwnerButton;

    // Component panels for different roles
    private ClientDashboard clientDashboard;
    private OwnerDashboard ownerDashboard;
    private CloudControllerDashboard cloudDashboard;

    public UniversalDashboard(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.user = user;

        setLayout(new BorderLayout());

        // Create top panel with user info and role selector
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Create content panel with CardLayout for different role views
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        add(contentPanel, BorderLayout.CENTER);

        // Initialize dashboard for the user's current role
        initializeRoleDashboards();

        // Show the appropriate dashboard
        if (user.hasRole("cloud_controller")) {
            // Cloud controllers get the cloud controller dashboard
            showCloudControllerDashboard();
        } else {
            // Regular users get either vehicle owner or job owner dashboard
            if (user.getRole().equals("vehicle_owner")) {
                switchToVehicleOwner();
            } else {
                switchToJobOwner();
            }
        }
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(43, 43, 43));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullName(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);

        // Center panel for role selector (only for regular users)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(new Color(43, 43, 43));

        if (user.hasRole("cloud_controller")) {
            // Cloud controller gets a special label
            JLabel roleLabel = new JLabel("Role: Cloud Controller", SwingConstants.CENTER);
            roleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            roleLabel.setForeground(Color.YELLOW);
            centerPanel.add(roleLabel);
        } else {
            // Regular users get toggle buttons for switching roles
            ButtonGroup roleGroup = new ButtonGroup();

            // Vehicle Owner button
            vehicleOwnerButton = new JToggleButton("Vehicle Owner");
            vehicleOwnerButton.setFont(new Font("Arial", Font.BOLD, 14));
            vehicleOwnerButton.addActionListener(e -> switchToVehicleOwner());
            roleGroup.add(vehicleOwnerButton);
            centerPanel.add(vehicleOwnerButton);

            // Job Owner button
            jobOwnerButton = new JToggleButton("Job Owner");
            jobOwnerButton.setFont(new Font("Arial", Font.BOLD, 14));
            jobOwnerButton.addActionListener(e -> switchToJobOwner());
            roleGroup.add(jobOwnerButton);
            centerPanel.add(jobOwnerButton);
        }

        panel.add(centerPanel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.logout());
        panel.add(logoutButton, BorderLayout.EAST);

        return panel;
    }

    private void initializeRoleDashboards() {
        if (user.hasRole("cloud_controller")) {
            // Cloud controllers only get the cloud controller dashboard
            cloudDashboard = new CloudControllerDashboard();
            contentPanel.add(cloudDashboard, "cloud_controller");
        } else {
            // Regular users get both vehicle owner and job owner dashboards
            if (user.hasRole("job_owner")) {
                clientDashboard = new ClientDashboard(user);
                contentPanel.add(clientDashboard, "job_owner");
            }

            if (user.hasRole("vehicle_owner")) {
                ownerDashboard = new OwnerDashboard(user.getUserId());
                contentPanel.add(ownerDashboard, "vehicle_owner");
            }
        }
    }

    private void showCloudControllerDashboard() {
        contentLayout.show(contentPanel, "cloud_controller");
    }

    private void switchToVehicleOwner() {
        user.setCurrentRole("vehicle_owner");
        if (vehicleOwnerButton != null) {
            vehicleOwnerButton.setSelected(true);
        }
        contentLayout.show(contentPanel, "vehicle_owner");

        // Refresh vehicle data
        if (ownerDashboard != null) {
            ownerDashboard.refreshVehicleTable();
        }
    }

    private void switchToJobOwner() {
        user.setCurrentRole("job_owner");
        if (jobOwnerButton != null) {
            jobOwnerButton.setSelected(true);
        }
        contentLayout.show(contentPanel, "job_owner");

        // Refresh job data
        if (clientDashboard != null) {
            clientDashboard.updateTable();
        }
    }
}
