package interfaces;

import models.entities.Appointment;
import models.entities.Availability;
import models.enums.DoctorAvailability;

import java.time.LocalDate;
import java.util.List;

/**
 * The AvailabilityManager interface defines methods for managing a doctor's availability schedule.
 * It includes methods for retrieving, updating, resetting, viewing, and modifying availability slots,
 * as well as handling conflicts with appointments.
 */
public interface AvailabilityManager {
    /**
     * Loads the availability list for a specified doctor.
     *
     * @param doctorId The ID of the doctor whose availability list is being retrieved.
     */
    void getAvailList(String doctorId);

    /**
     * Updates the availability data for a specified doctor.
     *
     * @param doctorId The ID of the doctor whose availability data is being updated.
     */
    void updateData(String doctorId);

    /**
     * Resets the availability data, clearing any loaded availability list.
     */
    void resetData();

    /**
     * Displays all availability slots for a specified doctor.
     *
     * @param doctorId The ID of the doctor whose availability is being viewed.
     */
    void viewAllAvail(String doctorId);

    /**
     * Adds a new availability slot for a specified doctor.
     *
     * @param doctorId   The ID of the doctor for whom the availability slot is being added.
     * @param date       The date of the availability slot.
     * @param startTime  The start time of the availability slot.
     * @param endTime    The end time of the availability slot.
     * @param status     The availability status of the doctor (e.g., AVAILABLE or BUSY).
     */
    void addAvail(String doctorId, LocalDate date, String startTime, String endTime, DoctorAvailability status);

    /**
     * Retrieves availability slots for a specified doctor on a specific date.
     *
     * @param doctorId The ID of the doctor whose availability is being retrieved.
     * @param date     The date for which availability slots are requested.
     * @return A list of Availability objects representing the available slots on the specified date.
     */
    List<Availability> slotsForDate(String doctorId, LocalDate date);

    /**
     * Edits an existing availability slot for a specified doctor.
     *
     * @param doctorId     The ID of the doctor whose availability slot is being edited.
     * @param desiredDate  The date of the availability slot.
     * @param oldStart     The original start time of the slot to be edited.
     * @param changedStart The new start time, if being changed (otherwise, pass null).
     * @param changedEnd   The new end time, if being changed (otherwise, pass null).
     * @param changedStatus The new status of the slot (e.g., AVAILABLE or BUSY).
     */
    void editOneSlot(String doctorId, LocalDate desiredDate, String oldStart, String changedStart, String changedEnd, DoctorAvailability changedStatus);

    /**
     * Updates the status of an availability slot in the data source to reflect a change due to an appointment.
     *
     * @param oneAppt       The Appointment object that affects the availability.
     * @param changedStatus The new availability status (e.g., BOOKED) due to the appointment.
     * @param availFilePath The file path where availability data is stored.
     */
    void updateAvailabilitySlot(Appointment oneAppt, DoctorAvailability changedStatus, String availFilePath);

}
