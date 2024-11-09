package models.enums;

/**
 * Enum representing the various statuses of an appointment.
 * Each constant represents a different state an appointment can be in.
 */
public enum AppointmentStatus {

    /**
     * Appointment is booked but not confirmed by the doctor.
     */
    SCHEDULED,

    /**
     * Appointment has been confirmed by both the patient and the doctor.
     */
    CONFIRMED,

    /**
     * Appointment has been cancelled by the patient or the doctor.
     */
    CANCELLED,

    /**
     * Appointment has taken place successfully.
     */
    COMPLETED,

    /**
     * Appointment request has been declined by the doctor.
     */
    DECLINED
}
