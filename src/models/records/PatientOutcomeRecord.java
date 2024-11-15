package models.records;

/**
 * The PatientOutcomeRecord class represents the outcome details of a patient's appointment.
 * It includes information such as the appointment ID, service type, consultation notes,
 * prescribed medicine, and the outcome status.
 */
public class PatientOutcomeRecord {
    private String appointmentId;
    private String serviceType;
    private String consultationNote;
    private String medicine;
    private String medicineStatus;
    private String outcomeStatus;

    /**
     * Constructs a PatientOutcomeRecord with the specified details.
     *
     * @param appointmentId   The unique identifier for the appointment.
     * @param serviceType     The type of service provided during the appointment.
     * @param consultationNote Notes from the consultation.
     * @param medicine         The name of the prescribed medicine.
     * @param medicineStatus   The status of the prescribed medicine.
     * @param outcomeStatus    The overall outcome status of the appointment.
     */
    public PatientOutcomeRecord(String appointmentId, String serviceType, String consultationNote,
                                String medicine, String medicineStatus, String outcomeStatus) {
        this.appointmentId = appointmentId;
        this.serviceType = serviceType;
        this.consultationNote = consultationNote;
        this.medicine = medicine;
        this.medicineStatus = medicineStatus;
        this.outcomeStatus = outcomeStatus;
    }

    /**
     * Gets the appointment ID.
     *
     * @return The unique identifier for the appointment.
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Gets the service type provided during the appointment.
     *
     * @return The service type.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Gets the consultation notes.
     *
     * @return The consultation notes.
     */
    public String getConsultationNote() {
        return consultationNote;
    }

    /**
     * Gets the name(s) of the prescribed medicine(s).
     *
     * @return The name(s) of the prescribed medicine(s).
     */
    public String getMedicine() {
        return medicine;
    }

    /**
     * Gets the status of the prescribed medicine(s).
     *
     * @return The medicine status.
     */
    public String getMedicineStatus() {
        return medicineStatus;
    }

    /**
     * Gets the overall outcome status of the appointment.
     *
     * @return The outcome status.
     */
    public String getOutcomeStatus() {
        return outcomeStatus;
    }

    /**
     * Sets the appointment ID.
     *
     * @param appointmentId The unique identifier for the appointment.
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * Sets the service type provided during the appointment.
     *
     * @param serviceType The service type.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Sets the consultation notes.
     *
     * @param consultationNote The consultation notes.
     */
    public void setConsultationNote(String consultationNote) {
        this.consultationNote = consultationNote;
    }

    /**
     * Sets the name(s) of the prescribed medicine(s).
     *
     * @param medicine The name(s) of the prescribed medicine(s).
     */
    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    /**
     * Sets the status of the prescribed medicine(s).
     *
     * @param medicineStatus The medicine status.
     */
    public void setMedicineStatus(String medicineStatus) {
        this.medicineStatus = medicineStatus;
    }

    /**
     * Sets the overall outcome status of the appointment.
     *
     * @param outcomeStatus The outcome status.
     */
    public void setOutcomeStatus(String outcomeStatus) {
        this.outcomeStatus = outcomeStatus;
    }
}
