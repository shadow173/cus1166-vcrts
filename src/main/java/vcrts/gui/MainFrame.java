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
    private User currentUser;

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
        this.currentUser = user;

        // Remove any previously added dashboard
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("dashboard")) {
                mainPanel.remove(comp);
            }
        }

        // Create a universal dashboard that can handle multiple roles
        UniversalDashboard dashboard = new UniversalDashboard(this, user);
        dashboard.setName("dashboard");
        mainPanel.add(dashboard, "dashboard");
        showPage("dashboard");
    }

    /**
     * Returns the currently logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Log out the current user and return to the startup page
     */
    public void logout() {
        this.currentUser = null;
        showPage("startup");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
