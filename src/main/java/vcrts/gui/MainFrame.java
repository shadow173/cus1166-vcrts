package vcrts.gui;

import javax.swing.*;
import java.awt.*;
import vcrts.gui.pages.StartupPage;
import vcrts.gui.pages.LoginPage;
import vcrts.gui.pages.CreateAccountPage;
import vcrts.gui.pages.ClientDashboard;
import vcrts.gui.pages.ClientForm;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        setTitle("VCRTS Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //  CardLayout to manage different pages
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add pages with identifiers
        mainPanel.add(new StartupPage(this), "startup");
        mainPanel.add(new LoginPage(this), "login");
        mainPanel.add(new CreateAccountPage(this), "createAccount");
//        mainPanel.add(new ClientDashboard(this), "dashboard");
//        mainPanel.add(new ClientForm(this), "clientForm");

        add(mainPanel);

        // Start with the startup page
        showPage("startup");
    }

    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }
}
