package vcrts.db;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Vehicle {
    private String vehicleId;
    private String make;
    private String model;
    private String year;
    private String vin;
    private int jobsCompleted;
    private String arrivalTime;
    private String departureTime;
    private String status;

    public Vehicle(String vehicleId, String make, String model, String year, String vin, int jobsCompleted, String arrivalTime, String departureTime, String status) {
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.jobsCompleted = jobsCompleted;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getResidencyTime() {
        if (departureTime == null || departureTime.isEmpty()) {
            return "Still Active";
        }
        return calculateTimeDifference(arrivalTime, departureTime);
    }

    private String calculateTimeDifference(String start, String end) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        try {
            Date startTime = format.parse(start);
            Date endTime = format.parse(end);
            long diff = endTime.getTime() - startTime.getTime();
            long diffMinutes = diff / (60 * 1000);
            long hours = diffMinutes / 60;
            long minutes = diffMinutes % 60;
            return hours + " hrs " + minutes + " mins";
        } catch (Exception e) {
            return "Error";
        }
    }

    public Object[] toArray() {
        return new Object[]{vehicleId, "Owner001", model, make, year, vin, jobsCompleted, arrivalTime, departureTime, getResidencyTime(), status};
    }
}