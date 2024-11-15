package interfaces;

import models.entities.*;


/**
 * The {@code WriteExcel} interface defines the contract for classes that handle
 * writing data to an Excel file.
 *
 * <p>This interface provides methods for writing both staff and medicine data to an Excel file.
 * The methods are designed to be overridden in the implementing classes to provide specific logic for
 * saving the data in the appropriate format.</p>
 */
public interface WriteExcel {

    /**
     * Writes staff data to an Excel file.
     *
     * @param staff the {@link Staff} object containing the data to be written.
     * @param currentUser the {@link User} object representing the currently logged-in user,
     *                    used for logging or tracking who performed the action.
     */
    default void writeToExcel(Staff staff, User currentUser){};

    /**
     * Writes medicine data to an Excel file.
     *
     * @param medicine the {@link Medicine} object containing the data to be written.
     * @param currentUser the {@link User} object representing the currently logged-in user,
     *                    used for logging or tracking who performed the action.
     */
    default void writeToExcel(Medicine medicine, User currentUser){};
}
