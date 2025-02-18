package vcrts.gui;

import javax.swing.*;
import java.awt.*;
import vcrts.gui.pages.LoginPage;
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

//        mainPanel.add(new LoginPage(this), "login");
//        mainPanel.add(new ClientDashboard(this), "dashboard");
//        mainPanel.add(new ClientForm(this), "clientForm");

        add(mainPanel);
    }

    // Method to switch between pages
    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
