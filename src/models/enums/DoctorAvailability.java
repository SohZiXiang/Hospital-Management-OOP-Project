package models.enums;

/**
 * Enum representing the availability status of a doctor.
 * This enum provides three states: AVAILABLE, BUSY, and BOOKED.
 * Each status is used to indicate whether a doctor is free, occupied, or reserved for an appointment.
 */
public enum DoctorAvailability {
    /**
     * Indicates that the doctor is available for appointments.
     */
    AVAILABLE,
    /**
     * Indicates that the doctor is currently busy and not available for appointments.
     */
    BUSY,
    /**
     * Indicates that the doctor is booked for an appointment.
     */
    BOOKED
}
