package vcrts.dao;

import vcrts.db.FileManager;
import vcrts.models.Allocation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AllocationDAO {
    private static final Logger logger = Logger.getLogger(AllocationDAO.class.getName());
    private static final String ALLOCATIONS_FILE = "allocations.txt";
    private static final String DELIMITER = "\\|";
    private static final String SEPARATOR = "|";

    /**
     * Converts an Allocation object to a line of text for storage.
     */
    private String allocationToLine(Allocation allocation) {
        return allocation.getAllocationId() + SEPARATOR +
                allocation.getUserId() + SEPARATOR +
                allocation.getJobId();
    }

    /**
     * Converts a line of text to an Allocation object.
     */
    private Allocation lineToAllocation(String line) {
        String[] parts = line.split(DELIMITER);
        if (parts.length < 3) {
            logger.warning("Invalid allocation data format: " + line);
            return null;
        }

        try {
            return new Allocation(
                    Integer.parseInt(parts[0]),  // allocationId
                    parts[1],                     // userId
                    parts[2]                      // jobId
            );
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing allocation ID: " + parts[0], e);
            return null;
        }
    }

    /**
     * Retrieves all allocation records from the file.
     *
     * @return A list of all allocations.
     */
    public List<Allocation> getAllAllocations() {
        List<Allocation> allocations = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(ALLOCATIONS_FILE);

        for (String line : lines) {
            Allocation allocation = lineToAllocation(line);
            if (allocation != null) {
                allocations.add(allocation);
            }
        }

        return allocations;
    }

    /**
     * Adds a new allocation to the file.
     *
     * @param allocation The allocation object to be added.
     * @return true if the allocation was successfully added, false otherwise.
     */
    public boolean addAllocation(Allocation allocation) {
        // Generate a new allocation ID
        int allocationId = FileManager.generateUniqueNumericId(ALLOCATIONS_FILE);
        allocation.setAllocationId(allocationId);

        String allocationLine = allocationToLine(allocation);
        return FileManager.appendLine(ALLOCATIONS_FILE, allocationLine);
    }

    /**
     * Deletes an allocation by its ID.
     *
     * @param allocationId The ID of the allocation to be deleted.
     * @return true if the allocation was successfully deleted, false otherwise.
     */
    public boolean deleteAllocation(int allocationId) {
        List<String> lines = FileManager.readAllLines(ALLOCATIONS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean deleted = false;

        for (String line : lines) {
            Allocation allocation = lineToAllocation(line);
            if (allocation != null && allocation.getAllocationId() == allocationId) {
                deleted = true;
            } else {
                updatedLines.add(line);
            }
        }

        return deleted && FileManager.writeAllLines(ALLOCATIONS_FILE, updatedLines);
    }

    /**
     * Updates an existing allocation's details.
     * @param allocation An Allocation object with updated information.
     * @return true if the update is successful; false otherwise.
     */
    public boolean updateAllocation(Allocation allocation) {
        List<String> lines = FileManager.readAllLines(ALLOCATIONS_FILE);
        List<String> updatedLines = new ArrayList<>();
        boolean updated = false;

        for (String line : lines) {
            Allocation existingAllocation = lineToAllocation(line);
            if (existingAllocation != null && existingAllocation.getAllocationId() == allocation.getAllocationId()) {
                updatedLines.add(allocationToLine(allocation));
                updated = true;
            } else {
                updatedLines.add(line);
            }
        }

        return updated && FileManager.writeAllLines(ALLOCATIONS_FILE, updatedLines);
    }
}
