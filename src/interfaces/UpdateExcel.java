package interfaces;

import models.entities.*;

/**
 * The {@code UpdateExcel} interface defines the contract for classes that handle
 * updating data in an Excel file.
 *
 * <p>This interface provides methods for updating both staff and medicine data in an Excel file.
 * The methods are designed to be overridden in the implementing classes to provide specific logic for
 * updating the data in the appropriate format.</p>
 */
public interface UpdateExcel {

    /**
     * Updates staff data in an Excel file.
     *
     * @param staff the {@link Staff} object containing the updated data.
     * @param currentUser the {@link User} object representing the currently logged-in user,
     *                    used for logging or tracking who performed the action.
     */
    default void updateToExcel(Staff staff, User currentUser){};

    /**
     * Updates medicine data in an Excel file.
     *
     * @param medicine the {@link Medicine} object containing the updated data.
     * @param oldName the previous name of the medicine, used to locate the record to update.
     * @param currentUser the {@link User} object representing the currently logged-in user,
     *                    used for logging or tracking who performed the action.
     */
    default void updateToExcel(Medicine medicine, String oldName, User currentUser){};
}
