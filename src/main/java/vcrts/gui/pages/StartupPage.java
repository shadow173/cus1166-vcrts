package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vcrts.gui.MainFrame;

public class StartupPage extends JPanel {
    private final MainFrame parent;

    public StartupPage(MainFrame parent) {
        this.parent = parent;
        setOpaque(true);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome to VCRTS!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(welcomeLabel);
        return header;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add vertical glue for spacing
        content.add(Box.createVerticalGlue());

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Account");

        // Style the buttons
        loginButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        createAccountButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginButton.setPreferredSize(new Dimension(150, 40));
        createAccountButton.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        content.add(buttonPanel);

        content.add(Box.createVerticalStrut(20)); // Extra spacing

        // Button actions to switch screens
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showPage("login");
            }
        });
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.showPage("createAccount");
            }
        });

        return content;
    }
}
