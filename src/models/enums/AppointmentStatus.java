package models.enums;

public enum AppointmentStatus {
    SCHEDULED,     // appointment booked but not confirmed
    CONFIRMED,     // appointment confirmed by both patient and doctor
    CANCELED,      // appointment cancelled
    COMPLETED,      // appointment has taken place
    DECLINED        // appointment request has been declined by the doctor
}
