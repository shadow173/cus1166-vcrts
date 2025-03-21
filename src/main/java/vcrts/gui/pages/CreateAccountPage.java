package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import vcrts.dao.UserDAO;
import vcrts.gui.MainFrame;
import vcrts.models.User;

public class CreateAccountPage extends JPanel {
    private MainFrame parent;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton createButton;
    private JButton backButton;

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
        JLabel title = new JLabel("Create a New Account", SwingConstants.CENTER);
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
        gbc.anchor = GridBagConstraints.CENTER;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel fullNameLabel = new JLabel("Full Name:", SwingConstants.CENTER);
        fullNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        fullNameField = new JTextField(15);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fullNameField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:", SwingConstants.CENTER);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.CENTER);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(passwordField, gbc);

        // Notice about roles
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel rolesInfoLabel = new JLabel("Your account will have access to both Vehicle Owner and Job Owner functions.", SwingConstants.CENTER);
        rolesInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        formPanel.add(rolesInfoLabel, gbc);

        // Create Account Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        createButton = new JButton("Create Account");
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        createButton.setPreferredSize(new Dimension(150, 35));
        formPanel.add(createButton, gbc);

        // Back Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.setPreferredSize(new Dimension(150, 35));
        formPanel.add(backButton, gbc);

        // Action Listener for Create Account Button
        createButton.addActionListener(e -> {
            String fullName = fullNameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Automatically assign both vehicle_owner and job_owner roles
            String roles = "vehicle_owner,job_owner";

            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.addUser(new User(fullName, email, roles, password));
            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                parent.showPage("login");
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Action Listener for Back Button
        backButton.addActionListener(e -> parent.showPage("startup"));

        return formPanel;
    }
}
