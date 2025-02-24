package vcrts.gui;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import vcrts.models.User;
import vcrts.gui.pages.*;

public class MainFrame extends JFrame {
    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("VCRTS Application");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add static pages
        mainPanel.add(new StartupPage(this), "startup");
        mainPanel.add(new LoginPage(this), "login");
        mainPanel.add(new CreateAccountPage(this), "createAccount");

        // Initially show the startup page.
        add(mainPanel);
        showPage("startup");
    }

    /**
     * Switches to the specified page.
     */
    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    /**
     * After successful login, this method is called with the authenticated User.
     * The appropriate dashboard is then added and displayed.
     */
    public void showDashboard(User user) {
        // Remove any previously added dashboard
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("dashboard")) {
                mainPanel.remove(comp);
            }
        }

        // Based on user role, add the correct dashboard.
        if (user.getRole().equalsIgnoreCase("vehicle_owner")) {
            ClientDashboard clientDashboard = new ClientDashboard(user);
            clientDashboard.setName("dashboard");
            mainPanel.add(clientDashboard, "dashboard");
        } else if (user.getRole().equalsIgnoreCase("job_owner")) {
            OwnerDashboard ownerDashboard = new OwnerDashboard(user.getUserId());
            ownerDashboard.setName("dashboard");
            mainPanel.add(ownerDashboard, "dashboard");
        } else if (user.getRole().equalsIgnoreCase("cloud_controller")) {
            CloudControllerDashboard cloudDashboard = new CloudControllerDashboard();
            cloudDashboard.setName("dashboard");
            mainPanel.add(cloudDashboard, "dashboard");
        } else {
            logger.severe("Unknown user role: " + user.getRole());
            JOptionPane.showMessageDialog(this, "Unknown user role!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        showPage("dashboard");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
