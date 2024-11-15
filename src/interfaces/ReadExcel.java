package interfaces;

import models.entities.User;


/**
 * The {@code ReadExcel} interface defines the contract for classes that handle
 * reading data from an Excel file.
 *
 * <p>This interface provides a method to read data based on a specific identifier (e.g., user ID).
 * Implementing classes will provide their own logic for fetching and processing the data from the Excel file.</p>
 */
public interface ReadExcel {
    /**
     * Reads data from an Excel file based on the given identifier.
     *
     * @param id the identifier (e.g., user ID) used to locate the relevant data in the Excel file.
     *           The exact type of ID depends on the implementation (e.g., could be a staff ID or user ID).
     * @throws IllegalArgumentException if the provided ID is invalid or not found in the Excel file.
     * @throws Exception if there is an error while reading from the Excel file.
     */
     void readFromExcel(String id);
}
