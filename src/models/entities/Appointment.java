package models.entities;
import java.util.*;

import models.enums.AppointmentStatus;

/**
 * The Appointment class represents a medical appointment between a patient and a doctor,
 * including details such as appointment ID, patient ID, doctor ID, status, date, time, and outcome record.
 */
public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private AppointmentStatus status;
    private Date appointmentDate;
    private String appointmentTime;
    private String outcomeRecord;

    /**
     * Constructs an Appointment instance with the specified details.
     *
     * @param appointmentId   The unique ID of the appointment.
     * @param patientId       The unique ID of the patient.
     * @param doctorId        The unique ID of the doctor.
     * @param appointmentDate The date of the appointment.
     * @param appointmentTime The time of the appointment.
     * @param outcomeRecord   The outcome record for the appointment.
     */
    public Appointment(String appointmentId, String patientId, String doctorId, Date appointmentDate, String appointmentTime, String outcomeRecord) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = AppointmentStatus.SCHEDULED;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.outcomeRecord = outcomeRecord;
    }

    /**
     * Gets the patient ID associated with this appointment.
     *
     * @return The patient ID as a String.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Gets the appointment ID.
     *
     * @return The appointment ID as a String.
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * Gets the current status of the appointment.
     *
     * @return The appointment status as an AppointmentStatus enum.
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * Gets the doctor ID associated with this appointment.
     *
     * @return The doctor ID as a String.
     */
    public String getDoctorId() {
        return doctorId;
    }

    /**
     * Gets the date of the appointment.
     *
     * @return The appointment date as a Date object.
     */
    public Date getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * Gets the time of the appointment.
     *
     * @return The appointment time as a String (in hh:mm a format).
     */
    public String getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * Gets the outcome record of the appointment.
     *
     * @return The outcome record as a String.
     */
    public String getOutcomeRecord() {
        return outcomeRecord;
    }

    /**
     * Sets the status of the appointment.
     *
     * @param status The new status to set (e.g., SCHEDULED, CONFIRMED).
     */
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Sets the time of the appointment.
     *
     * @param appointmentTime The new time to set (in hh:mm a format).
     */
    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    /**
     * Sets the outcome record of the appointment.
     *
     * @param outcomeRecord The new outcome record to set.
     */
    public void setOutcomeRecord(String outcomeRecord) {
        this.outcomeRecord = outcomeRecord;
    }

    /**
     * Sets the date of the appointment.
     *
     * @param appointmentDate The new appointment date to set.
     */
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Sets the doctor ID for this appointment.
     *
     * @param doctorId The new doctor ID to set.
     */
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
