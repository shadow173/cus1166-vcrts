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

public class ClientForm extends JFrame {
    private JPanel mainPanel;

    public ClientForm() {
        setTitle("Job Submission Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        showClientPanel();

        add(mainPanel);
    }

    private void showClientPanel() {
        mainPanel.removeAll();

        JLabel titleLabel = new JLabel("Job Submission Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField clientIdField = new JTextField(15);
        JTextField jobIdField = new JTextField(15);
        JTextField jobDurationField = new JTextField(15);
        JComboBox<String> durationUnit = new JComboBox<>(new String[]{"Minutes", "Hours"});
        JTextField jobDeadlineField = new JTextField(15);

        clientIdField.setPreferredSize(new Dimension(150, 25));
        jobIdField.setPreferredSize(new Dimension(150, 25));
        jobDurationField.setPreferredSize(new Dimension(150, 25));
        jobDeadlineField.setPreferredSize(new Dimension(150, 25));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.add(new JLabel("Client ID:"));
        formPanel.add(clientIdField);
        formPanel.add(new JLabel("Job ID:"));
        formPanel.add(jobIdField);
        formPanel.add(new JLabel("Job Duration:"));
        formPanel.add(jobDurationField);
        formPanel.add(new JLabel("Duration Unit:"));
        formPanel.add(durationUnit);
        formPanel.add(new JLabel("Job Deadline (yyyy-MM-dd):"));
        formPanel.add(jobDeadlineField);

        JButton submitButton = new JButton("Submit Job");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            String clientId = clientIdField.getText();
            String jobId = jobIdField.getText();
            String jobDuration = jobDurationField.getText();
            String durationType = (String) durationUnit.getSelectedItem();
            String jobDeadline = jobDeadlineField.getText();

            if (clientId.isEmpty() || jobId.isEmpty() || jobDuration.isEmpty() || jobDeadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Automatically assign values
            String status = "Queued"; // Default status
            String estimatedCompletion = jobDuration + " " + durationType.toLowerCase(); // Estimated Completion Time

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String data = String.format("Timestamp: %s\nClient ID: %s\nJob ID: %s\nStatus: %s\nEstimated Completion Time: %s\nJob Deadline: %s\n",
                    timestamp, clientId, jobId, status, estimatedCompletion, jobDeadline);

            try (FileWriter writer = new FileWriter("client_jobs.txt", true)) {
                writer.write(data);
                JOptionPane.showMessageDialog(this, "Job submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving job: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewJobsButton = new JButton("View Registered Jobs");
        viewJobsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewJobsButton.addActionListener(e -> showRegisteredJobsPanel());

        mainPanel.add(titleLabel);
        mainPanel.add(formPanel);
        mainPanel.add(submitButton);
        mainPanel.add(viewJobsButton);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showRegisteredJobsPanel() {
        mainPanel.removeAll();

        JLabel titleLabel = new JLabel("Registered Jobs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jobsPanel = new JPanel();
        jobsPanel.setLayout(new BoxLayout(jobsPanel, BoxLayout.Y_AXIS));
        jobsPanel.setBackground(Color.WHITE);

        try {
            List<String> jobs = Files.readAllLines(Paths.get("client_jobs.txt"));
            int jobEntryLines = 6;
            for (int i = 0; i + jobEntryLines <= jobs.size(); i += jobEntryLines) {
                String job = String.join("\n", jobs.subList(i, i + jobEntryLines));
                JPanel jobPanel = createJobPanel(job, i);
                jobsPanel.add(jobPanel);
                jobsPanel.add(Box.createVerticalStrut(10));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No jobs registered yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(jobsPanel);
        scrollPane.setPreferredSize(new Dimension(700, 400));

        JButton backButton = new JButton("Back to Form");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> showClientPanel());

        mainPanel.add(titleLabel);
        mainPanel.add(scrollPane);
        mainPanel.add(backButton);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createJobPanel(String job, int index) {
        JPanel jobPanel = new JPanel();
        jobPanel.setLayout(new BorderLayout());
        jobPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JTextArea jobTextArea = new JTextArea(job);
        jobTextArea.setEditable(false);
        jobTextArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteJob(index));

        jobPanel.add(jobTextArea, BorderLayout.CENTER);
        jobPanel.add(deleteButton, BorderLayout.EAST);

        return jobPanel;
    }

    private void deleteJob(int index) {
        try {
            List<String> jobs = Files.readAllLines(Paths.get("client_jobs.txt"));
            int jobEntryLines = 6;
            if (index >= 0 && index + jobEntryLines <= jobs.size()) {
                jobs.subList(index, index + jobEntryLines).clear();
                Files.write(Paths.get("client_jobs.txt"), jobs);
                showRegisteredJobsPanel();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting job: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientForm frame = new ClientForm();
            frame.setVisible(true);
        });
    }
}
