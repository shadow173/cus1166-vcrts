package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;
import vcrts.gui.MainFrame;
import vcrts.dao.UserDAO;
import vcrts.models.User;

public class LoginPage extends JPanel {
    private static final Logger logger = Logger.getLogger(LoginPage.class.getName());
    private MainFrame parent;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;
    private UserDAO userDAO = new UserDAO();

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
        JLabel title = new JLabel("Login", SwingConstants.CENTER);
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

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:", SwingConstants.CENTER);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.CENTER);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(120, 35));
        formPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        backButton = new JButton("Back");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backButton.setPreferredSize(new Dimension(120, 35));
        formPanel.add(backButton, gbc);

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Both email and password are required.", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = userDAO.authenticate(email, password);
            if (user != null) {
                logger.info("User authenticated: " + user);

                // Check if user is a cloud controller
                if (user.hasRole("cloud_controller")) {
                    // Set cloud_controller as the current role
                    user.setCurrentRole("cloud_controller");

                    // Show cloud controller dashboard
                    parent.showDashboard(user);
                    return;
                }

                // Ensure regular users have both roles
                if (!user.hasRole("vehicle_owner")) {
                    user.addRole("vehicle_owner");
                }
                if (!user.hasRole("job_owner")) {
                    user.addRole("job_owner");
                }

                // Set default role to vehicle_owner
                user.setCurrentRole("vehicle_owner");

                parent.showDashboard(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> parent.showPage("startup"));

        return formPanel;
    }
}
