package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import vcrts.gui.MainFrame;

public class CreateAccountPage extends JPanel {
    private MainFrame parent;

    public CreateAccountPage(MainFrame parent) {
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
        JLabel title = new JLabel("Create a New Account");
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

        // Add form components (fields for username, password, email, etc.)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordField, gbc);


        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton createButton = new JButton("Create Account");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        createButton.setPreferredSize(new Dimension(150, 35));
        formPanel.add(createButton, gbc);

        // Add action listener (dummy implementation for now)
        createButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Account creation not yet implemented.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        return formPanel;
    }
}
