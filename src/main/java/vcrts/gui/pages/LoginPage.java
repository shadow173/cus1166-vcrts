package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import vcrts.gui.MainFrame;

public class LoginPage extends JPanel {
    private MainFrame parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPage(MainFrame parent) {
        this.parent = parent;
        setOpaque(true);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.add(title);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(120, 35));
        formPanel.add(loginButton, gbc);

        // Here, add an action listener to handle login (dummy authentication for now)
        loginButton.addActionListener(e -> {
            // Dummy authentication - replace with real logic later
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if ("admin".equals(username) && "adminpass".equals(password)) {
                parent.showPage("dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return formPanel;
    }
}
