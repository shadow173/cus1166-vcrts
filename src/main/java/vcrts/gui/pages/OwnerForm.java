package vcrts.gui.pages;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import vcrts.dao.VehicleDAO;
import vcrts.models.Vehicle;

public class OwnerForm extends JPanel {
    private int ownerId;
    private JTextField modelField, makeField, yearField, vinField;
    private JSpinner hoursSpinner, minutesSpinner, secondsSpinner;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    public OwnerForm(int ownerId) {
        this.ownerId = ownerId;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Owner Vehicle Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Use GridBagLayout for more control over form layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create and add owner ID field (read-only)
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel ownerIdLabel = new JLabel("Owner ID:", SwingConstants.CENTER);
        formPanel.add(ownerIdLabel, gbc);

        gbc.gridx = 1;
        JTextField ownerIdField = new JTextField(String.valueOf(ownerId));
        ownerIdField.setEditable(false);
        ownerIdField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(ownerIdField, gbc);

        // Create and add model field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel modelLabel = new JLabel("Model:", SwingConstants.CENTER);
        formPanel.add(modelLabel, gbc);

        gbc.gridx = 1;
        modelField = new JTextField(15);
        modelField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(modelField, gbc);

        // Create and add make field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel makeLabel = new JLabel("Make:", SwingConstants.CENTER);
        formPanel.add(makeLabel, gbc);

        gbc.gridx = 1;
        makeField = new JTextField(15);
        makeField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(makeField, gbc);

        // Create and add year field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel yearLabel = new JLabel("Year:", SwingConstants.CENTER);
        formPanel.add(yearLabel, gbc);

        gbc.gridx = 1;
        yearField = new JTextField(15);
        yearField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(yearField, gbc);

        // Create and add VIN field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel vinLabel = new JLabel("VIN:", SwingConstants.CENTER);
        formPanel.add(vinLabel, gbc);

        gbc.gridx = 1;
        vinField = new JTextField(15);
        vinField.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(vinField, gbc);

        // Create and add residency time with spinners
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel residencyTimeLabel = new JLabel("Residency Time:", SwingConstants.CENTER);
        formPanel.add(residencyTimeLabel, gbc);

        gbc.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Hours spinner (0-23)
        SpinnerNumberModel hoursModel = new SpinnerNumberModel(1, 0, 23, 1);
        hoursSpinner = new JSpinner(hoursModel);
        JSpinner.NumberEditor hoursEditor = new JSpinner.NumberEditor(hoursSpinner, "00");
        hoursSpinner.setEditor(hoursEditor);
        hoursSpinner.setPreferredSize(new Dimension(60, 25));

        // Minutes spinner (0-59)
        SpinnerNumberModel minutesModel = new SpinnerNumberModel(0, 0, 59, 1);
        minutesSpinner = new JSpinner(minutesModel);
        JSpinner.NumberEditor minutesEditor = new JSpinner.NumberEditor(minutesSpinner, "00");
        minutesSpinner.setEditor(minutesEditor);
        minutesSpinner.setPreferredSize(new Dimension(60, 25));

        // Seconds spinner (0-59)
        SpinnerNumberModel secondsModel = new SpinnerNumberModel(0, 0, 59, 1);
        secondsSpinner = new JSpinner(secondsModel);
        JSpinner.NumberEditor secondsEditor = new JSpinner.NumberEditor(secondsSpinner, "00");
        secondsSpinner.setEditor(secondsEditor);
        secondsSpinner.setPreferredSize(new Dimension(60, 25));

        timePanel.add(hoursSpinner);
        timePanel.add(new JLabel("h"));
        timePanel.add(minutesSpinner);
        timePanel.add(new JLabel("m"));
        timePanel.add(secondsSpinner);
        timePanel.add(new JLabel("s"));

        formPanel.add(timePanel, gbc);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the submit button inside the formPanel to move it up
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;  // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Register Vehicle");
        formPanel.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            String model = modelField.getText().trim();
            String make = makeField.getText().trim();
            String year = yearField.getText().trim();
            String vin = vinField.getText().trim();

            // Format residency time from spinners
            int hours = (Integer) hoursSpinner.getValue();
            int minutes = (Integer) minutesSpinner.getValue();
            int seconds = (Integer) secondsSpinner.getValue();
            String residencyTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

            if(model.isEmpty() || make.isEmpty() || year.isEmpty() || vin.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hours == 0 && minutes == 0 && seconds == 0) {
                JOptionPane.showMessageDialog(this, "Residency time must be greater than zero!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vehicle vehicle = new Vehicle(ownerId, model, make, year, vin, residencyTime);

            if(vehicleDAO.addVehicle(vehicle)){
                JOptionPane.showMessageDialog(this,
                        "Vehicle registered successfully! Registration time: " + vehicle.getRegisteredTimestamp(),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear fields after successful registration
                modelField.setText("");
                makeField.setText("");
                yearField.setText("");
                vinField.setText("");
                hoursSpinner.setValue(1);
                minutesSpinner.setValue(0);
                secondsSpinner.setValue(0);
            } else {
                JOptionPane.showMessageDialog(this, "Error registering vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mainPanel, BorderLayout.CENTER);
    }
}
