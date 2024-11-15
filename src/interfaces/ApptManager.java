package interfaces;

import app.loaders.InventoryLoader;
import models.entities.Appointment;
import models.entities.Medicine;
import models.enums.AppointmentStatus;
import models.records.AppointmentOutcomeRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The ApptManager interface defines methods for managing doctor appointments, viewing schedules,
 * updating appointment statuses, recording outcomes, and handling medicine inventory related to appointments.
 */
public interface ApptManager {
    /**
     * Retrieves the list of appointments for a specified doctor.
     *
     * @param doctorId The ID of the doctor whose appointment list is being retrieved.
     * @return A list of Appointment objects for the specified doctor.
     */
    List<Appointment> getApptList(String doctorId);

    /**
     * Updates the appointment data for a specified doctor based on the type of data.
     *
     * @param docID The ID of the doctor whose appointment data is being updated.
     * @param type  The type of data to update (e.g., "appt" for appointments, "outcome" for outcomes).
     */
    void updateData(String docID, String type);

    /**
     * Resets the specified type of appointment data.
     *
     * @param type The type of data to reset (e.g., "appt" for appointments, "outcome" for outcomes).
     */
    void resetData(String type);

    /**
     * Displays a doctor's schedule, showing confirmed appointments and available slots.
     *
     * @param doctorId The ID of the doctor whose schedule is being viewed.
     */
    void viewDoctorSchedule(String doctorId);

    /**
     * Displays all upcoming confirmed appointments.
     */
    void viewUpcomingAppt();

    /**
     * Retrieves a list of appointments that are pending review.
     *
     * @return A list of Appointment objects that are pending review.
     */
    List<Appointment> apptPendingReviews();

    /**
     * Updates the status of a specific appointment.
     *
     * @param oneAppt       The Appointment object to update.
     * @param changedStatus The new status to set for the appointment.
     */
    void updateAppointmentStatus(Appointment oneAppt, AppointmentStatus changedStatus);

    /**
     * Cancels a specific appointment.
     *
     * @param oneAppt The Appointment object to cancel.
     */
    void cancelAppt(Appointment oneAppt);

    /**
     * Finds an appointment by date and start time.
     *
     * @param desiredDate The date of the desired appointment.
     * @param startTime   The start time of the desired appointment.
     * @return An Optional containing the Appointment if found, or empty if not found.
     */
    Optional<Appointment> findAppt(LocalDate desiredDate, String startTime);

    /**
     * Retrieves the list of appointment outcome records for a specified doctor.
     *
     * @param doctorId The ID of the doctor whose outcome records are being retrieved.
     * @return A list of AppointmentOutcomeRecord objects for the specified doctor.
     */
    List<AppointmentOutcomeRecord> getOutcomeRecords(String doctorId);

    /**
     * Retrieves the list of medicines from the inventory.
     *
     * @return A list of Medicine objects representing the current inventory.
     */
    List<Medicine> getMedData();

    /**
     * Finds an appointment by its unique appointment ID.
     *
     * @param apptID The unique ID of the appointment.
     * @return The Appointment object if found, or null if not found.
     */
    Appointment findApptByID(String apptID);

    /**
     * Displays the current stock of medicines in the inventory.
     */
    void viewMedsStock();

    /**
     * Records the outcome of a specific appointment, including service type, prescribed medications,
     * consultation notes, and the outcome status.
     *
     * @param appointment       The Appointment object representing the appointment.
     * @param serviceType       The type of service provided in the appointment.
     * @param prescriptions     A list of prescribed medications for the appointment.
     * @param consultationNotes Notes from the consultation during the appointment.
     * @param outcomeStatus     The outcome status of the appointment.
     */
    void recordAppointmentOutcome(Appointment appointment, String serviceType,
                                  List<AppointmentOutcomeRecord.PrescribedMedication> prescriptions,
                                  String consultationNotes, String outcomeStatus);

    /**
     * Displays all appointment outcomes for a specified doctor.
     *
     * @param doctorID The ID of the doctor whose appointment outcomes are being viewed.
     */
    void viewAllAppointmentOutcomes(String doctorID);
}
