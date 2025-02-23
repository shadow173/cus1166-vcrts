package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OwnerForm extends JPanel {
    private JPanel mainPanel;
    private JTextField ownerIdField;
    private JTextField modelField;
    private JTextField makeField;
    private JTextField yearField;
    private JTextField vinField;
    private JTextField residencyTimeField;

    public OwnerForm() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        showOwnerForm();
        add(mainPanel, BorderLayout.CENTER);
    }

    private void showOwnerForm() {
        mainPanel.removeAll();

        JLabel titleLabel = new JLabel("Owner Vehicle Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields for Owner ID, Model, Make, Year, VIN, and Residency Time
        ownerIdField = new JTextField(15);
        modelField = new JTextField(15);
        makeField = new JTextField(15);
        yearField = new JTextField(15);
        vinField = new JTextField(15);
        residencyTimeField = new JTextField(15);

        ownerIdField.setPreferredSize(new Dimension(150, 25));
        modelField.setPreferredSize(new Dimension(150, 25));
        makeField.setPreferredSize(new Dimension(150, 25));
        yearField.setPreferredSize(new Dimension(150, 25));
        vinField.setPreferredSize(new Dimension(150, 25));
        residencyTimeField.setPreferredSize(new Dimension(150, 25));

        // Form panel with 6 fields
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(new JLabel("Owner ID:"));
        formPanel.add(ownerIdField);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        formPanel.add(new JLabel("Make:"));
        formPanel.add(makeField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Approximate Residency Time (hh:mm:ss):"));
        formPanel.add(residencyTimeField);

        JButton submitButton = new JButton("Register Vehicle");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(e -> {
            String ownerId = ownerIdField.getText();
            String model = modelField.getText();
            String make = makeField.getText();
            String year = yearField.getText();
            String vin = vinField.getText();
            String residencyTime = residencyTimeField.getText();

            // Validate that all fields are filled
            if (ownerId.isEmpty() || model.isEmpty() || make.isEmpty() || year.isEmpty() || vin.isEmpty() || residencyTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Format the data to be saved
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String data = String.format("Timestamp: %s\nOwner ID: %s\nModel: %s\nMake: %s\nYear: %s\nVIN: %s\nResidency Time: %s",
                    timestamp, ownerId, model, make, year, vin, residencyTime);

            // Save the data to a file
            try (FileWriter writer = new FileWriter("owner_vehicles.txt", true)) {
                if (Files.exists(Paths.get("owner_vehicles.txt")) && Files.size(Paths.get("owner_vehicles.txt")) > 0) {
                    writer.write("\n"); // Add a newline only if the file is not empty
                }
                writer.write(data); // Write the new entry
                JOptionPane.showMessageDialog(this, "Vehicle registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving vehicle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewVehiclesButton = new JButton("View Registered Vehicles");
        viewVehiclesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewVehiclesButton.addActionListener(e -> showRegisteredVehiclesPanel());

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> showOwnerForm());

        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(submitButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(viewVehiclesButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(backButton);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showRegisteredVehiclesPanel() {
        mainPanel.removeAll();

        JLabel titleLabel = new JLabel("Registered Vehicles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel vehiclesPanel = new JPanel();
        vehiclesPanel.setLayout(new BoxLayout(vehiclesPanel, BoxLayout.Y_AXIS));
        vehiclesPanel.setBackground(Color.WHITE);

        try {
            List<String> vehicles = Files.readAllLines(Paths.get("owner_vehicles.txt"));
            int totalLines = vehicles.size();

            for (int i = 0; i < totalLines; i += 7) {
                // Check if there are enough lines for a complete vehicle
                if (i + 7 > totalLines) {
                    // Skip incomplete entry
                    System.out.println("Skipping incomplete vehicle entry at line " + (i + 1));
                    continue;
                }

                // Combine 7 lines for display
                String vehicle = String.join("\n", vehicles.subList(i, i + 7));
                JPanel vehiclePanel = createVehiclePanel(vehicle);
                vehiclesPanel.add(vehiclePanel);
                vehiclesPanel.add(Box.createVerticalStrut(10));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No vehicles registered yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(vehiclesPanel);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> showOwnerForm());

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(backButton);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createVehiclePanel(String vehicle) {
        JPanel vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BorderLayout());
        vehiclePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JTextArea vehicleTextArea = new JTextArea(vehicle);
        vehicleTextArea.setEditable(false);
        vehicleTextArea.setFont(new Font("Arial", Font.PLAIN, 14));

        vehiclePanel.add(vehicleTextArea, BorderLayout.CENTER);

        return vehiclePanel;
    }

    // Main method to test the OwnerForm independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Owner Vehicle Registration");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            OwnerForm ownerForm = new OwnerForm();
            frame.add(ownerForm);

            frame.setVisible(true);
        });
    }
}