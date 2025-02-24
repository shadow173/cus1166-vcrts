package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import vcrts.dao.VehicleDAO;
import vcrts.models.Vehicle;

public class OwnerForm extends JPanel {
    private int ownerId;
    private JTextField modelField, makeField, yearField, vinField, residencyTimeField;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    public OwnerForm(int ownerId) {
        this.ownerId = ownerId;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Owner Vehicle Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(new JLabel("Owner ID:"));
        JTextField ownerIdField = new JTextField(ownerId);
        ownerIdField.setEditable(false);
        formPanel.add(ownerIdField);
        formPanel.add(new JLabel("Model:"));
        modelField = new JTextField(15);
        formPanel.add(modelField);
        formPanel.add(new JLabel("Make:"));
        makeField = new JTextField(15);
        formPanel.add(makeField);
        formPanel.add(new JLabel("Year:"));
        yearField = new JTextField(15);
        formPanel.add(yearField);
        formPanel.add(new JLabel("VIN:"));
        vinField = new JTextField(15);
        formPanel.add(vinField);
        formPanel.add(new JLabel("Residency Time (hh:mm:ss):"));
        residencyTimeField = new JTextField(15);
        formPanel.add(residencyTimeField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        JButton submitButton = new JButton("Register Vehicle");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(submitButton);

        submitButton.addActionListener(e -> {
            String model = modelField.getText().trim();
            String make = makeField.getText().trim();
            String year = yearField.getText().trim();
            String vin = vinField.getText().trim();
            String residencyTime = residencyTimeField.getText().trim();

            if(model.isEmpty() || make.isEmpty() || year.isEmpty() || vin.isEmpty() || residencyTime.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Optionally record a timestamp if needed
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Vehicle vehicle = new Vehicle(ownerId, model, make, year, vin, residencyTime);

            if(vehicleDAO.addVehicle(vehicle)){
                JOptionPane.showMessageDialog(this, "Vehicle registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Clear fields after successful registration
                modelField.setText("");
                makeField.setText("");
                yearField.setText("");
                vinField.setText("");
                residencyTimeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error registering vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewVehiclesButton = new JButton("View Registered Vehicles");
        viewVehiclesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewVehiclesButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Use the navigation above to view registered vehicles.", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(viewVehiclesButton);

        add(mainPanel, BorderLayout.CENTER);
    }
}
