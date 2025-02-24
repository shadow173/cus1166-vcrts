package vcrts.gui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vcrts.dao.VehicleDAO;
import vcrts.gui.MainFrame;
import vcrts.models.Vehicle;

public class OwnerDashboard extends JPanel {
    private static final Logger logger = Logger.getLogger(OwnerDashboard.class.getName());

    private int ownerId;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Components for the vehicle list view
    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public OwnerDashboard(int ownerId) {
        this.ownerId = ownerId;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top navigation with title and Logout button
        JPanel topNav = new JPanel(new BorderLayout());
        topNav.setBackground(new Color(43, 43, 43));
        JLabel titleLabel = new JLabel("Owner Dashboard - Vehicle Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        topNav.add(titleLabel, BorderLayout.CENTER);
        JButton logoutButton = new JButton("Logout");
        topNav.add(logoutButton, BorderLayout.EAST);
        logoutButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(this).dispose();
            new MainFrame().setVisible(true);
        });
        add(topNav, BorderLayout.NORTH);

        // Navigation panel to switch between registration form and list view
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setBackground(new Color(43, 43, 43));
        JButton registerVehicleButton = new JButton("Register Vehicle");
        JButton viewVehiclesButton = new JButton("View Registered Vehicles");
        navPanel.add(registerVehicleButton);
        navPanel.add(viewVehiclesButton);
        add(navPanel, BorderLayout.SOUTH);

        // Content panel with CardLayout: one card for OwnerForm and one for vehicle list view.
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        // Assume OwnerForm is implemented to accept ownerId and perform vehicle registration via VehicleDAO.
        contentPanel.add(new OwnerForm(ownerId), "form");
        contentPanel.add(createVehicleListPanel(), "list");
        add(contentPanel, BorderLayout.CENTER);

        registerVehicleButton.addActionListener(e -> cardLayout.show(contentPanel, "form"));
        viewVehiclesButton.addActionListener(e -> {
            refreshVehicleTable();
            cardLayout.show(contentPanel, "list");
        });
    }

    private JPanel createVehicleListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(43, 43, 43));

        JLabel listTitle = new JLabel("Registered Vehicles", SwingConstants.CENTER);
        listTitle.setFont(new Font("Arial", Font.BOLD, 20));
        listTitle.setForeground(Color.WHITE);
        panel.add(listTitle, BorderLayout.NORTH);

        String[] columnNames = {"Owner ID", "Model", "Make", "Year", "VIN", "Residency Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        vehicleTable = new JTable(tableModel);
        vehicleTable.setBackground(new Color(230, 230, 230));
        vehicleTable.setForeground(Color.BLACK);
        vehicleTable.setRowHeight(30);
        vehicleTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JTableHeader header = vehicleTable.getTableHeader();
        header.setBackground(new Color(200, 200, 200));
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 15));
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.setBackground(new Color(43, 43, 43));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshVehicleTable());
        controlPanel.add(refreshButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        new Timer(10000, e -> refreshVehicleTable()).start();
        refreshVehicleTable();
        return panel;
    }

    public void refreshVehicleTable() {
        try {
            tableModel.setRowCount(0);
            List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwner(ownerId);
            for (Vehicle v : vehicles) {
                tableModel.addRow(new Object[]{
                        v.getOwnerId(),
                        v.getModel(),
                        v.getMake(),
                        v.getYear(),
                        v.getVin(),
                        v.getResidencyTime()
                });
            }
        } catch(Exception ex) {
            logger.log(Level.SEVERE, "Error refreshing vehicle table: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error loading vehicle data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
