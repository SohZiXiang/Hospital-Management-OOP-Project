package interfaces;

import models.entities.User;

/**
 * The {@code DeleteExcel} interface defines the contract for classes that handle
 * the removal of data from an Excel file.
 *
 * <p>This interface provides a method for deleting a record from an Excel file,
 * specifically identifying the record by its unique identifier (ID). The method
 * is designed to be implemented in classes that will provide the specific logic
 * for removing the data.</p>
 */
public interface DeleteExcel {


    /**
     * Removes a record from the Excel file based on the provided ID.
     *
     * @param id the unique identifier of the record to remove.
     * @param currentUser the {@link User} object representing the currently logged-in user,
     *                    used for logging or tracking who performed the action.
     */
    void removeFromExcel(String id, User currentUser);
}
