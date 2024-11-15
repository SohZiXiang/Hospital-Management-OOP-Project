package models.entities;
import models.enums.*;
import models.services.PatientService;
import java.time.LocalDate;
import java.util.*;

/**
 * The Patient class represents a patient in the hospital management system.
 * It extends the User class and includes patient-specific details such as
 * medical records, contact information, and blood type.
 * The class provides methods for managing patient data, appointments, and
 * medical records through interactions with the PatientService class.
 */
public class Patient extends User {
    private String patientID;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String  email;
    private BloodType bloodType;
    private List<String> pastDiagnoses;
    private List<String> pastTreatments;

    PatientService patientService = new PatientService();

    /**
     * Constructs a Patient with the given hospital ID.
     *
     * @param hospitalID The hospital ID of the patient.
     */
    public Patient(String hospitalID) {
        super(hospitalID);
    }

    /**
     * Constructs a Patient with full details.
     *
     * @param hospitalID      The hospital ID of the patient.
     * @param name            The name of the patient.
     * @param password        The password for the patient account.
     * @param patientID       The unique ID of the patient.
     * @param dateOfBirth     The date of birth of the patient.
     * @param gender          The gender of the patient.
     * @param phoneNumber     The phone number of the patient.
     * @param email           The email address of the patient.
     * @param bloodType       The blood type of the patient.
     * @param pastDiagnoses   A list of the patient's past diagnoses.
     * @param pastTreatments  A list of the patient's past treatments.
     */
    public Patient(String hospitalID, String name, String password,
                   String patientID, LocalDate dateOfBirth, Gender gender,
                   String phoneNumber, String email, BloodType bloodType,
                   List<String> pastDiagnoses, List<String> pastTreatments) {
        super(hospitalID, name, password, gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
    }

    /**
     * Constructs a Patient with a slightly modified parameter order.
     *
     * @param hospitalID      The hospital ID of the patient.
     * @param patientID       The unique ID of the patient.
     * @param name            The name of the patient.
     * @param dateOfBirth     The date of birth of the patient.
     * @param gender          The gender of the patient.
     * @param email           The email address of the patient.
     * @param bloodType       The blood type of the patient.
     * @param pastDiagnoses   A list of the patient's past diagnoses.
     * @param pastTreatments  A list of the patient's past treatments.
     * @param phoneNumber     The phone number of the patient.
     */
    public Patient(String hospitalID,
                   String patientID, String name, LocalDate dateOfBirth, Gender gender,
                   String email, BloodType bloodType, List<String> pastDiagnoses, List<String> pastTreatments, String phoneNumber) {
        super(hospitalID = patientID, name,"P@ssw0rd123", gender);
        this.patientID = patientID;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.bloodType = bloodType;
        this.pastDiagnoses = (pastDiagnoses != null) ? new ArrayList<>(pastDiagnoses) : new ArrayList<>();
        this.pastTreatments = (pastTreatments != null) ? new ArrayList<>(pastTreatments) : new ArrayList<>();
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the patient ID.
     *
     * @return The patient ID.
     */
    public String getPatientID() {
        return patientID;
    }

    /**
     * Gets the date of birth of the patient.
     *
     * @return The patient's date of birth.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Gets the patient's phone number.
     *
     * @return The phone number of the patient.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the patient's email address.
     *
     * @return The email address of the patient.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the blood type of the patient.
     *
     * @return The patient's blood type.
     */
    public BloodType getBloodType() {
        return bloodType;
    }

    /**
     * Gets the list of the patient's past diagnoses.
     *
     * @return A list of past diagnoses.
     */
    public List<String> getPastDiagnoses() {
        return pastDiagnoses;
    }

    /**
     * Gets the list of the patient's past treatments.
     *
     * @return A list of past treatments.
     */
    public List<String> getPastTreatments() {
        return pastTreatments;
    }

    /**
     * Sets the patient's phone number.
     *
     * @param phoneNumber The new phone number for the patient.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the patient's email address.
     *
     * @param email The new email address for the patient.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Adds a diagnosis to the patient's past diagnoses list.
     *
     * @param diagnosis The diagnosis to add.
     */
    public void addDiagnosis(String diagnosis) {
        if (this.pastDiagnoses == null) {
            this.pastDiagnoses = new ArrayList<>();
        }
        this.pastDiagnoses.add(diagnosis);
    }


    /**
     * Adds a treatment to the patient's past treatments list.
     *
     * @param treatment The treatment to add.
     */
    public void addTreatment(String treatment) {
        if (this.pastTreatments == null) {
            this.pastTreatments = new ArrayList<>();
        }
        this.pastTreatments.add(treatment);
    }

    /**
     * Gets the role of the patient.
     *
     * @return The patient's role as Role.PATIENT.
     */
    @Override
    public Role getRole() {
        return Role.PATIENT;
    }

    /**
     * Creates an appointment for the user.
     *
     * @param user          The user creating the appointment.
     * @param option        The selected option for the appointment slot.
     * @param appointmentID The unique ID of the appointment.
     * @param create        Boolean indicating if this is a new appointment or a rescheduling.
     */
    public void createAppointment(User user, int option, String appointmentID, boolean create) {
        patientService.createAppointment(user, option, appointmentID, create);
    }

    /**
     * Loads and displays the user's appointments through patient service.
     *
     * @param user The user whose appointments are being loaded.
     */
    public void loadAppointmentData(User user){
        patientService.loadAppointmentData(user);
    }

    /**
     * Cancels an appointment for the user through patient service.
     *
     * @param user          The user canceling the appointment.
     * @param appointmentID The ID of the appointment to cancel.
     * @param cancel        Boolean indicating whether to cancel the appointment.
     */
    public void cancelAppointment(User user, String appointmentID, boolean cancel) {
        patientService.cancelAppointment(user, appointmentID, cancel, 0);
    }

    /**
     * Reschedules an existing appointment for the user through patient service.
     *
     * @param user          The user rescheduling the appointment.
     * @param option        The selected option for the new appointment slot.
     * @param appointmentID The ID of the appointment to reschedule.
     */
    public void rescheduleAppointment(User user, int option, String appointmentID) {
        patientService.rescheduleAppointment(user, option, appointmentID);
    }

    /**
     * Loads and displays past appointment outcomes for the user through patient service.
     *
     * @param user The user whose outcomes are being loaded.
     */
    public void loadOutcomeData(User user){
        patientService.loadOutcomeData(user);
    }

    /**
     * Loads and displays the user's medical record data through patient service.
     *
     * @param user The user whose medical records are being loaded.
     */
    public void loadMedicalRecordData(User user) {
        patientService.loadMedicalRecordData(user);
    }

    /**
     * Updates the contact information (email or phone number) of the user through patient service.
     *
     * @param user     The user updating their contact information.
     * @param patient  The patient object with updated contact details.
     * @param email    Boolean indicating whether the email is being updated.
     */
    public void updateContact(User user, Patient patient, boolean email) {
        patientService.updateContact(user, patient, email);
    }

}
