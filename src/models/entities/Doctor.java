package models.entities;

import java.time.*;

import interfaces.*;
import models.enums.*;
import models.records.*;
import models.services.*;
import java.util.*;

import models.services.AvailService;

/**
 * The Doctor class extends Staff and represents a doctor within the hospital management system.
 * It provides comprehensive functionality to manage appointments, availability, and patient records.
 * This class interacts with ApptManager, AvailabilityManager, and PatientManager interfaces to
 * manage and retrieve information about doctor appointments, availability slots, and patient data.
 *
 * Key Functionalities:
 * - Load and manage patient records: retrieve, filter, and update patient data.
 * - Load and manage availability slots: view, add, and edit availability for a specified date and time.
 * - Load and manage appointment records: view schedules, check upcoming appointments, and update appointment statuses.
 * - Record appointment outcomes, including prescribed medications and consultation notes.
 * - Access medication data to view current stock levels.
 *
 * Dependencies:
 * - ApptManager: Manages appointment-related data and actions.
 * - AvailabilityManager: Manages availability-related data and actions.
 * - PatientManager: Manages patient-related data and actions.
 *
 * This class provides methods that allow doctors to effectively manage their schedules, patients, and
 * overall workflow within the hospital management system, enabling efficient patient care and resource allocation.
 */
public class Doctor extends Staff{
    private final ApptManager manageAppts;
    private final AvailabilityManager manageAvails;
    private final PatientManager managePatients;

    /**
     * Constructs a Doctor instance with the specified details.
     *
     * @param hospitalID The hospital ID of the doctor.
     * @param staffId    The staff ID of the doctor.
     * @param name       The name of the doctor.
     * @param gender     The gender of the doctor.
     * @param age        The age of the doctor.
     */
    public Doctor(String hospitalID, String staffId, String name, Gender gender, int age) {
        super(hospitalID, staffId, name, gender, age);
        this.managePatients = new PatientService();
        this.manageAvails = new AvailService(null);
        this.manageAppts = new ApptService(this.manageAvails, this.managePatients);

        ((AvailService) this.manageAvails).setApptManager(this.manageAppts);
    }

    /**
     * Alternate constructor for creating a Doctor instance without a hospital ID.
     *
     * @param staffId The staff ID of the doctor.
     * @param name    The name of the doctor.
     * @param gender  The gender of the doctor.
     * @param age     The age of the doctor.
     */
    public Doctor(String staffId, String name, Gender gender, int age) {
        super(staffId, staffId, name, gender, age);
        this.managePatients = new PatientService();
        this.manageAvails = new AvailService(null);
        this.manageAppts = new ApptService(this.manageAvails, this.managePatients);

        ((AvailService) this.manageAvails).setApptManager(this.manageAppts);
    }


    /* START OF METHODS TO LOAD DATA */

    /**
     * Loads the patient list for the doctor.
     */
    public void getPatientList() {
        managePatients.getPatientList();
    }

    /**
     * Loads the appointment list for the doctor.
     */
    public void getAvailList() {
        manageAvails.getAvailList(this.getStaffId());
    }

    /**
     * Loads the availability list for the doctor.
     */
    public void getApptList() {
        manageAppts.getApptList(this.getStaffId());

    }

    /**
     * Loads the appointment outcome records for the doctor.
     */
    public void getOutcomeRecords() {
        manageAppts.getOutcomeRecords(this.getStaffId());
    }

    /**
     * Loads the medication data.
     */
    public void getMedData() {
        manageAppts.getMedData();
    }

    /* END OF METHODS TO LOAD DATA */

    /* START OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    /**
     * Resets the appointment data based on the specified type.
     *
     * @param type The type of data to reset ("appt" or "outcome").
     */
    public void resetApptData(String type) {
        manageAppts.resetData(type);
        resetPatientData();
    }

    /**
     * Resets the patient data.
     */
    public void resetPatientData() {
        managePatients.resetData();
    }

    /**
     * Resets the availability data.
     */
    public void resetAvailData() {
        manageAvails.resetData();
    }

    /* END OF METHODS TO UPDATE/RESET DATA ONCE NEW RECORD ADDED/USER LOGOUTS */

    /* START OF METHODS TO DO WITH PATIENT DATA */

    /**
     * Displays all patient records.
     */
    public void showAllPatientsRecords() {
        managePatients.showAllPatientsRecords();
    }

    /**
     * Filters and displays patient details by ID.
     *
     * @param patientID The ID of the patient to filter.
     */
    public void filterPatients(String patientID) {
        managePatients.filterPatients(patientID);
    }

    /**
     * Checks if a patient with the specified ID is valid.
     *
     * @param patientID The ID of the patient.
     * @return The Patient object if valid, or null if not.
     */
    public Patient checkWhetherPatientValid(String patientID) {
        return managePatients.checkWhetherPatientValid(patientID);
    }

    /**
     * Updates the medical record of a patient with a new diagnosis and treatment.
     *
     * @param patient      The patient whose record is being updated.
     * @param newDiagnosis The new diagnosis to add.
     * @param newTreatment The new treatment to add.
     */
    public void updateMedicalRecord(Patient patient, String newDiagnosis, String newTreatment) {
        managePatients.updateMedicalRecord(patient, newDiagnosis, newTreatment);
    }

    /* END OF METHODS TO DO WITH PATIENT DATA */

    /* START OF MANAGEMENT OF DOCTOR AVAILABILITY METHODS */

    /**
     * Views all availability slots for the doctor.
     */
    public void viewAllAvail() {
        manageAvails.viewAllAvail(this.getStaffId());
    }

    /**
     * Adds a new availability slot for the doctor.
     *
     * @param date      The date of availability.
     * @param startTime The start time of the slot.
     * @param endTime   The end time of the slot.
     * @param status    The availability status (Available or Busy).
     */
    public void addAvail(LocalDate date, String startTime, String endTime,
                         DoctorAvailability status) {
        manageAvails.addAvail(this.getStaffId(), date, startTime, endTime, status);
    }

    /**
     * Retrieves all availability slots for a specific date.
     *
     * @param date The date to retrieve availability for.
     * @return A list of availability slots for the specified date.
     */
    public List<Availability> slotsForDate(LocalDate date) {
        return manageAvails.slotsForDate(this.getStaffId(), date);
    }

    /**
     * Edits a specific availability slot.
     *
     * @param desiredDate   The date of the slot to edit.
     * @param oldStart      The original start time of the slot.
     * @param changedStart  The new start time of the slot.
     * @param changedEnd    The new end time of the slot.
     * @param changedStatus The new status (Available or Busy).
     */
    public void editOneSlot(LocalDate desiredDate, String oldStart, String changedStart, String changedEnd,
                            DoctorAvailability changedStatus) {
        manageAvails.editOneSlot(this.getStaffId(), desiredDate, oldStart, changedStart, changedEnd,
                changedStatus);
        manageAvails.updateData(this.getStaffId());

    }

    /* END OF MANAGEMENT OF DOCTOR AVAILABILITY METHODS */

    /* START OF DOCTOR APPOINTMENTS METHODS */

    /**
     * Views the doctor's schedule for the current month.
     */
    public void viewDoctorSchedule() {
        manageAppts.viewDoctorSchedule(this.getStaffId());
    }

    /**
     * Views upcoming confirmed appointments for the doctor.
     */
    public void viewUpcomingAppt() {
        manageAppts.viewUpcomingAppt();
    }

    /**
     * Retrieves a list of appointments pending review.
     *
     * @return A list of pending review appointments.
     */
    public List<Appointment> apptPendingReviews() {
        return manageAppts.apptPendingReviews();
    }

    /**
     * Updates the status of a specific appointment.
     *
     * @param oneAppt      The appointment to update.
     * @param changedStatus The new status of the appointment.
     */
    public void updateAppointmentStatus(Appointment oneAppt, AppointmentStatus changedStatus) {
        manageAppts.updateAppointmentStatus(oneAppt, changedStatus);
        manageAppts.updateData(this.getStaffId(), "appt");
        manageAvails.updateData(this.getStaffId());
    }

    /* END OF DOCTOR APPOINTMENTS METHODS */

    /* START OF RECORDING APPT OUTCOME METHODS */

    /**
     * Finds an appointment by its ID.
     *
     * @param apptID The ID of the appointment to find.
     * @return The appointment if found, otherwise null.
     */
    public Appointment findApptByID(String apptID) {
        return manageAppts.findApptByID(apptID);
    }

    /**
     * Views the stock of available medications.
     */
    public void viewMedsStock() {
        manageAppts.viewMedsStock();
    }

    /**
     * Records the outcome of an appointment.
     *
     * @param appointment       The appointment for which the outcome is recorded.
     * @param serviceType       The type of service provided.
     * @param prescriptions     A list of prescribed medications.
     * @param consultationNotes Notes from the consultation.
     * @param outcomeSts        The outcome status.
     */
    public void recordAppointmentOutcome(Appointment appointment, String serviceType,
                                         List<AppointmentOutcomeRecord.PrescribedMedication> prescriptions,
                                         String consultationNotes, String outcomeSts) {
        manageAppts.recordAppointmentOutcome(appointment, serviceType, prescriptions, consultationNotes, outcomeSts);
    }

    /**
     * Views all appointment outcomes for the doctor.
     */
    public void viewAllAppointmentOutcomes() {
        manageAppts.viewAllAppointmentOutcomes(this.getStaffId());
    }

    /* END OF RECORDING APPT OUTCOME METHODS */

    /**
     * Gets the role of the doctor.
     *
     * @return The role of the doctor as an enumeration value.
     */
    @Override
    public Role getRole() {
        return Role.DOCTOR;
    }
}
