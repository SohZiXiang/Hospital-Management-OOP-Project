package models.records;

import com.twilio.rest.microvisor.v1.App;
import models.entities.Appointment;
import models.entities.Medicine;
import models.enums.*;

import java.util.*;
import java.util.ArrayList;

/**
 * Represents the outcome record for an appointment, including the appointment details,
 * medications prescribed, consultation notes, and final status.
 */
 public class AppointmentOutcomeRecord {
    private Appointment appt;
    private Date appointmentDate;
    private String serviceType;
    private List<PrescribedMedication> prescriptions;
    private String consultationNotes;
    private String apptStatus;

    /**
     * Inner class representing a prescribed medication and its current status.
     */
    public static class PrescribedMedication {
        private Medicine medicine;
        private PrescriptionStatus status;

        public PrescribedMedication(Medicine medicine) {
            this.medicine = medicine;
            this.status = PrescriptionStatus.PENDING;
        }

        /**
         * Constructor to create a new prescribed medication with a pending status.
         *
         * @param medicine The medicine prescribed.
         */
        public PrescribedMedication(Medicine medicine, PrescriptionStatus status) {
            this.medicine = medicine;
            this.status = status;
        }

        /**
         * Gets the medicine prescribed.
         *
         * @return The prescribed medicine.
         */
        public Medicine getMedicine() {
            return medicine;
        }

        /**
         * Gets the prescription status.
         *
         * @return The current prescription status.
         */
        public PrescriptionStatus getStatus() {
            return status;
        }

        /**
         * Sets the prescription status.
         *
         * @param status The new prescription status.
         */
        public void setStatus(PrescriptionStatus status) {
            this.status = status;
        }
    }


    /**
     * Constructs an AppointmentOutcomeRecord with appointment details, medications,
     * consultation notes, and the final appointment status.
     *
     * @param appt             The appointment associated with the outcome record.
     * @param appointmentDate  The date of the appointment.
     * @param serviceType      The type of service provided during the appointment.
     * @param medicines        The list of medicines prescribed during the appointment.
     * @param consultationNotes Notes from the consultation.
     * @param outcomeStatus    The final status of the appointment.
     */
    public AppointmentOutcomeRecord(Appointment appt, Date appointmentDate, String serviceType,
                                    List<Medicine> medicines,
                                    String consultationNotes, String outcomeStatus) {
        this.appt = appt;
        this.appointmentDate = appointmentDate;
        this.serviceType = serviceType;
        this.consultationNotes = consultationNotes;
        this.prescriptions = new ArrayList<>();
        for (Medicine medicine : medicines) {
            this.prescriptions.add(new PrescribedMedication(medicine));
        }
        this.apptStatus = outcomeStatus;

    }

    /**
     * Gets the associated appointment.
     *
     * @return The appointment.
     */
    public Appointment getAppt() { return appt; }

    /**
     * Gets the appointment date.
     *
     * @return The date of the appointment.
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
     * Gets the service type of the appointment.
     *
     * @return The type of service.
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the service type of the appointment.
     *
     * @param serviceType The service type to set.
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * Gets the list of prescribed medications.
     *
     * @return The list of prescribed medications.
     */
    public List<PrescribedMedication> getPrescriptions() {
        return prescriptions;
    }

    /**
     * Sets a new list of prescribed medications.
     *
     * @param medicines The list of medicines to set as prescriptions.
     */
    public void setPrescriptions(List<Medicine> medicines) {
        this.prescriptions.clear();
        for (Medicine medicine : medicines) {
            this.prescriptions.add(new PrescribedMedication(medicine));
        }
    }

    /**
     * Adds a new prescribed medication to the prescriptions list.
     *
     * @param medicine The medicine to be prescribed.
     */
     public void addPrescription(Medicine medicine) {
         this.prescriptions.add(new PrescribedMedication(medicine));
     }


    /**
     * Updates the prescription status of a specified medication.
     *
     * @param medicineName The name of the medicine.
     * @param newStatus    The new prescription status.
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
     * Gets the consultation notes for the appointment.
     *
     * @return The consultation notes.
     */
    public String getConsultationNotes() {
        return consultationNotes;
    }

    /**
     * Sets the consultation notes for the appointment.
     *
     * @param consultationNotes The consultation notes to set.
     */
    public void setConsultationNotes(String consultationNotes) {
        this.consultationNotes = consultationNotes;
    }

    /**
     * Gets the appointment status.
     *
     * @return The status of the appointment.
     */
    public String getApptStatus() { return apptStatus; }

    /**
     * Sets the appointment status.
     *
     * @param apptStatus The status to set for the appointment.
     */
     public void setApptStatus(String apptStatus) {
        this.apptStatus = apptStatus;
     }
}
