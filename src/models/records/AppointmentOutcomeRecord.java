package models.records;

import com.twilio.rest.microvisor.v1.App;
import models.entities.Appointment;
import models.entities.Medicine;
import models.enums.*;

import java.util.*;

/**
 * The AppointmentOutcomeRecord class represents the outcome of an appointment,
 * including details such as service type, prescribed medications, consultation notes,
 * and appointment status.
 */
 public class AppointmentOutcomeRecord {
    private Appointment appt;
    private Date appointmentDate;
    private String serviceType;
    private List<PrescribedMedication> prescriptions;
    private String consultationNotes;
    private String apptStatus;


    /**
     * Wrapper class to keep track of prescribed medication for the pharmacist and doctor.
     * Each PrescribedMedication instance includes the medicine details, quantity, and prescription status.
     */
    public static class PrescribedMedication {
        private Medicine medicine;
        private PrescriptionStatus status;
        private int quantityOfMed;

        /**
         * Constructs a PrescribedMedication instance with a specified medicine and quantity.
         * The prescription status is set to PENDING by default.
         *
         * @param medicine The prescribed medicine.
         * @param quantity The quantity of the prescribed medicine.
         */
        public PrescribedMedication(Medicine medicine, int quantity) {
            this.medicine = medicine;
            this.status = PrescriptionStatus.PENDING;
            this.quantityOfMed = quantity;
        }

        /**
         * Constructs a PrescribedMedication instance with specified medicine, status, and quantity.
         *
         * @param medicine The prescribed medicine.
         * @param status   The status of the prescription.
         * @param quantity The quantity of the prescribed medicine.
         */
        public PrescribedMedication(Medicine medicine, PrescriptionStatus status, int quantity) {
            this.medicine = medicine;
            this.status = status;
            this.quantityOfMed = quantity;

        }

        /**
         * Gets the prescribed medicine.
         *
         * @return The medicine.
         */
        public Medicine getMedicine() {
            return medicine;
        }

        /**
         * Gets the status of the prescription.
         *
         * @return The prescription status.
         */
        public PrescriptionStatus getStatus() {
            return status;
        }

        /**
         * Sets the status of the prescription.
         *
         * @param status The status to set.
         */
        public void setStatus(PrescriptionStatus status) {
            this.status = status;
        }

        /**
         * Gets the quantity of the prescribed medicine.
         *
         * @return The quantity of medicine.
         */
        public int getQuantityOfMed() { return quantityOfMed; }
    }

    /**
     * Constructs an AppointmentOutcomeRecord instance with the specified appointment details.
     *
     * @param appt              The appointment associated with this record.
     * @param appointmentDate   The date of the appointment.
     * @param serviceType       The type of service provided during the appointment.
     * @param medicines         A list of prescribed medications associated with this appointment.
     * @param consultationNotes Notes from the consultation.
     * @param outcomeStatus     The status of the appointment outcome.
     */
    public AppointmentOutcomeRecord(Appointment appt, Date appointmentDate, String serviceType,
                                    List<PrescribedMedication> medicines,
                                    String consultationNotes, String outcomeStatus) {
        this.appt = appt;
        this.appointmentDate = appointmentDate;
        this.serviceType = serviceType;
        this.prescriptions = medicines;
        this.consultationNotes = consultationNotes;
        this.apptStatus = outcomeStatus;

    }

    /**
     * Gets the associated appointment.
     *
     * @return The appointment associated with this outcome record.
     */
    public Appointment getAppt() { return appt; }

    /**
     * Gets the date of the appointment.
     *
     * @return The appointment date.
     */
    public Date getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Sets the appointment date.
     *
     * @param appointmentDate The date to set for the appointment.
     */
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
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
     * Sets the service type for the appointment.
     *
     * @param serviceType The type of service provided.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the list of prescribed medications for this appointment.
     *
     * @return The list of prescribed medications.
     */
    public List<PrescribedMedication> getPrescriptions() {
        return prescriptions;
    }

    /**
     * Changes the prescription status of a specified medication.
     *
     * @param medicineName The name of the medicine whose status is to be changed.
     * @param newStatus    The new status to set for the specified medication.
     */
     public void changePrescriptionStatus(String medicineName, PrescriptionStatus newStatus) {
         for (PrescribedMedication prescribedMedication : prescriptions) {
             if (prescribedMedication.getMedicine().getName().equals(medicineName)) {
                 prescribedMedication.setStatus(newStatus);
                 break;
             }
         }
     }

     /**
     * Gets the consultation notes.
     *
     * @return The consultation notes.
     */
    public String getConsultationNotes() {
        return consultationNotes;
    }

    /**
     * Sets the consultation notes for this appointment outcome.
     *
     * @param consultationNotes The notes from the consultation.
     */
    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    /**
     * Gets the status of the appointment outcome.
     *
     * @return The appointment outcome status.
     */
    public String getApptStatus() { return apptStatus; }

    /**
     * Sets the status of the appointment outcome.
     *
     * @param apptStatus The status of the appointment outcome.
     */
    public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
    }
}
